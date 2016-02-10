var pollsTemplate = $('#polls_template').html();

var userId = getLastUrlPath();


var startIndex = 0;
var browsePageSize = 12;
var fetchAmount = 12;
var recordCount = 1000;

$(document).ready(function() {

  setupTitle();

  setupTrendingPolls();

});

function setupWindowScrolling() {
  $(window).scroll(function() {
    if ($(window).scrollTop() + $(window).height() > $(document).height() - 100) {
      $(window).unbind('scroll');
      console.log('near bottom');
      // startIndex += browsePageSize;
      browsePageSize += fetchAmount;
      setupTrendingPolls();
    }
  });
}

function setupTrendingPolls() {

  var fromHomeScreen = (window.location.pathname == "/");

  var pageSize = fromHomeScreen ? 4 : browsePageSize;

  if ((pageSize <= recordCount)) {
    getJson('get_trending_polls/all/' + userId + '/created/' + pageSize + '/' + startIndex).done(function(e) {
      var data = JSON.parse(replaceNewlines(e));
      console.log(data);
      recordCount = data['record_count'];
      fillMustacheWithJson(data, pollsTemplate, '#polls_div');
      if (!fromHomeScreen) {
        setupWindowScrolling();
      }
    });
  }
}

function setupTitle() {
  getJson('get_user_info/' + userId).done(function(e) {
    var data = JSON.parse(e);
    console.log(data);

    $('#user_name').text(data['user_name']);

  });
}