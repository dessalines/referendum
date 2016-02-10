var trendingPollsTemplate = $('#trending_polls_template').html();


var startIndex = 0;
var browsePageSize = 15;
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
      startIndex += browsePageSize;
      setupTrendingPolls();
    }
  });
}

function setupTrendingPolls(period) {

  var fromHomeScreen = (window.location.pathname == "/");

  var order = (period === undefined) ? trendingPollsType + '_score' : period + '_score';

  var pageSize = fromHomeScreen ? 5 : browsePageSize;

  var keepFetching = ((startIndex + browsePageSize) <= recordCount);

  console.log(keepFetching);

  if (keepFetching || (period !== undefined)) {
    getJson('get_trending_polls/all/all/' + order + '/' + pageSize + '/' + startIndex).done(function(e) {
      var data = JSON.parse(replaceNewlines(e));
      console.log(data);
      recordCount = data['record_count'];

      if (startIndex == 0) {
        fillMustacheWithJson(data, trendingPollsTemplate, '#trending_polls_div');
      } else {
        // appending version
        fillMustacheWithJson(data, trendingPollsTemplate, '#trending_polls_div', null, true);
      }

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
