var commentsTemplate = $('#comments_template').html();
var recurse = $("#recurse").html();

var pollId = 1;
$(document).ready(function() {
	setupComments();
});

function setupComments() {
  getJson('get_comments/' + pollId).done(function(e) {
    var data = JSON.parse(replaceNewlines(e));
    console.log(data);
    fillMustacheWithJson(data, commentsTemplate, '#comments_div', {"recurse": recurse});
  });
}