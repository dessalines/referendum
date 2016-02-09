var trendingPollsTemplate = $('#trending_polls_template').html();
var trendingTagsTemplate = $('#trending_tags_template').html();


$(document).ready(function() {

  setupTrendingPolls('day');

  setupTrendingPollsBtns();


});

function setupTrendingPolls(period) {

  var order = period + '_score';

  getJson('get_trending_polls/all/' + order + '/20/0').done(function(e) {
    var data = JSON.parse(replaceNewlines(e));
    console.log(data);
    fillMustacheWithJson(data, trendingPollsTemplate, '#trending_polls_div');
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


