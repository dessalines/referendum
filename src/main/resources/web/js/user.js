var pollsTemplate = $('#polls_template').html();
var commentsTemplate = $('#comments_template').html();

var userAid = getLastUrlPath();

var activeTab = null;



$(document).ready(function() {

  setupActiveTabEvents();

  setupTitle();

  setupTrendingPolls();

  setupUserComments();

  setupUserMessages();

});

function setupActiveTabEvents() {

  // First do a check to set the correct one
  // 
  activeTab = window.location.hash;
  if (activeTab == '') {
    activeTab = '#' + $("ul#main_tab_list li.active").attr('name');
  } else {
    bindEvents();
  }

  console.log(activeTab);

  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    activeTab = $(e.target).attr('href');
    console.log(activeTab);
    bindEvents();
  });
}

function bindEvents() {
  $(window).unbind('scroll');
  if (activeTab == '#polls_tab') {
    setupPollsWindowScrolling();
  } else if (activeTab == '#comments_tab') {
    setupCommentsWindowScrolling();
  } else if (activeTab == '#messages_tab') {
    setupMessagesWindowScrolling();
    markMessagesAsRead();
  }
}

function markMessagesAsRead() {
  if (getCookie('uaid') == userAid) {
    simplePost('mark_messages_as_read', null, null,
      function() {
        fetchUnreadMessages();
      }, true, null, null);
  }
}

var commentsStartIndex = 0;
var commentsBrowsePageSize = 15;
var commentsRecordCount = 1000;

function setupCommentsWindowScrolling() {

  $(window).unbind('scroll');
  $(window).scroll(function() {
    if ($(window).scrollTop() + $(window).height() > $(document).height() - 100) {
      $(window).unbind('scroll');
      console.log('near bottom');
      commentsStartIndex += commentsBrowsePageSize;
      setupUserComments();
    }
  });
}

function setupUserComments() {

  var keepFetching = (commentsStartIndex <= commentsRecordCount);

  console.log(keepFetching);

  if (keepFetching) {
    getJson('get_user_comments/' + userAid + '/' + commentsBrowsePageSize + '/' + commentsStartIndex).done(function(e) {
      var data = JSON.parse(replaceNewlines(e));
      console.log(data);
      commentsRecordCount = data['record_count'];

      if (commentsStartIndex == 0) {
        $('#comments_div').empty();
        // $('#candidates_div').empty();
        fillMustacheWithJson(data, commentsTemplate, '#comments_div');
      } else {
        // appending version
        fillMustacheWithJson(data, commentsTemplate, '#comments_div', null, true);
      }
      initializeAllCommentVotes(data);

    });
  }

}

var messagesStartIndex = 0;
var messagesBrowsePageSize = 15;
var messagesRecordCount = 1000;

function setupMessagesWindowScrolling() {

  $(window).unbind('scroll');
  $(window).scroll(function() {
    if ($(window).scrollTop() + $(window).height() > $(document).height() - 100) {
      $(window).unbind('scroll');
      console.log('near bottom');
      messagesStartIndex += messagesBrowsePageSize;
      setupUserMessages();
    }
  });
}



function setupUserMessages() {

  var keepFetching = (messagesStartIndex <= messagesRecordCount);

  console.log(keepFetching);

  if (keepFetching) {
    getJson('get_user_messages/' + userAid + '/' + messagesBrowsePageSize + '/' + messagesStartIndex).done(function(e) {
      var data = JSON.parse(replaceNewlines(e));
      console.log(data);
      messagesRecordCount = data['record_count'];

      if (messagesStartIndex == 0) {
        $('#messages_div').empty();
        // $('#candidates_div').empty();
        fillMustacheWithJson(data, commentsTemplate, '#messages_div');
      } else {
        // appending version
        fillMustacheWithJson(data, commentsTemplate, '#messages_div', null, true);
      }
      initializeAllCommentVotes(data);

    });
  }

}



var pollsStartIndex = 0;
var pollsBrowsePageSize = 15;
var pollsRecordCount = 1000;

function setupPollsWindowScrolling() {

  $(window).unbind('scroll');
  $(window).scroll(function() {
    if ($(window).scrollTop() + $(window).height() > $(document).height() - 100) {
      $(window).unbind('scroll');
      console.log('near bottom');
      pollsStartIndex += pollsBrowsePageSize;
      setupTrendingPolls();
    }
  });
}


function setupTrendingPolls() {

  var keepFetching = (pollsStartIndex <= pollsRecordCount);

  console.log(keepFetching);

  if (keepFetching) {
    getJson('get_trending_polls/all/' + userAid + '/created/' + pollsBrowsePageSize + '/' + pollsStartIndex).done(function(e) {
      var data = JSON.parse(replaceNewlines(e));
      console.log(data);
      pollsRecordCount = data['record_count'];

      if (pollsStartIndex == 0) {
        $('#polls_div').empty();
        // $('#candidates_div').empty();
        fillMustacheWithJson(data, pollsTemplate, '#polls_div');
      } else {
        // appending version
        fillMustacheWithJson(data, pollsTemplate, '#polls_div', null, true);
      }

    });
  }
}

function setupTitle() {
  getJson('get_user_info/' + userAid).done(function(e) {
    var data = JSON.parse(e);
    console.log(data);

    $('#user_name').text(data['user_name']);

  });
}
