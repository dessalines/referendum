var trendingTagsTemplate = $('#trending_tags_template').html();


$(document).ready(function() {
  setupTrendingTags('day');
  setupTrendingTagsBtns();
});



function setupTrendingTags(period) {

  var order = period + '_score';

  getJson('get_trending_tags/' + order + '/400/0').done(function(e) {
    var data = JSON.parse(replaceNewlines(e));
    console.log(data);
    fillMustacheWithJson(data, trendingTagsTemplate, '#trending_tags_div');
  });
}

function setupTrendingTagsBtns() {
  $('#tag_trending_ul a').on('click', function(e) {
    e.preventDefault();

    $('#tag_trending_ul li').removeClass('active');

    $(this).parent().addClass('active');

    var type = $(this).attr('data-type');
    console.log(type);

    setupTrendingTags(type);

  });
}