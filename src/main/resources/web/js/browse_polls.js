var trendingPollsTemplate = $('#trending_polls_template').html();


var startIndex = 0;
var browsePageSize = 12;
var fetchAmount = 12;
var recordCount = 1000;

var trendingPollsType = 'day';

$(document).ready(function() {

  setupTrendingPolls();

  setupTrendingPollsBtns();



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

function setupTrendingPolls(period) {

  var fromHomeScreen = (window.location.pathname == "/");

  var order = (period === undefined) ? trendingPollsType + '_score' : period + '_score';

  var pageSize = fromHomeScreen ? 5 : browsePageSize;

  if ((pageSize <= recordCount) || (period !== undefined )) {
    getJson('get_trending_polls/all/all/' + order + '/' + pageSize + '/' + startIndex).done(function(e) {
      var data = JSON.parse(replaceNewlines(e));
      console.log(data);
      recordCount = data['record_count'];
      fillMustacheWithJson(data, trendingPollsTemplate, '#trending_polls_div');
      if (!fromHomeScreen) {
        setupWindowScrolling();
      }
    });
  }
}

function setupTrendingPollsBtns() {
  $('#poll_trending_ul a').on('click', function(e) {
    e.preventDefault();

    $('#poll_trending_ul li').removeClass('active');

    $(this).parent().addClass('active');

    var type = $(this).attr('data-type');
    console.log(type);

    setupTrendingPolls(type);
    trendingPollsType = type;
  });
}
