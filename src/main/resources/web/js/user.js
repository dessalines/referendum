var pollsTemplate = $('#polls_template').html();
var commentsTemplate = $('#comments_template').html();

var userAid = getLastUrlPath();

var activeTab = null;



$(document).ready(function() {

  setupTitle();

  setupTrendingPolls();

  setupUserComments();

});

var commentsStartIndex = 0;
var commentsBrowsePageSize = 15;
var commentsRecordCount = 1000;



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
