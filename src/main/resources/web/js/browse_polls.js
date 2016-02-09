var trendingPollsTemplate = $('#trending_polls_template').html();


var startIndex = 0;
var browsePageSize = 3;

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
      browsePageSize += 3;
      setupTrendingPolls();
    }
  });
}

function setupTrendingPolls(period) {

  var order = (period === undefined) ? trendingPollsType + '_score' : period + '_score';

  var pageSize = (window.location.pathname == "/") ? 4 : browsePageSize;

  getJson('get_trending_polls/all/' + order + '/' + pageSize + '/' + startIndex).done(function(e) {
    var data = JSON.parse(replaceNewlines(e));
    console.log(data);
    fillMustacheWithJson(data, trendingPollsTemplate, '#trending_polls_div');
    setupWindowScrolling();
  });
}

function setupTrendingPollsBtns() {
  $('#poll_trending_ul a').on('click', function(e) {
    e.preventDefault();

    $('#poll_trending_ul li').removeClass('active');

    $(this).parent().addClass('active');

    var type = $(this).attr('data-type');
    console.log(type);

    setupTrendingPolls(type);

  });
}
