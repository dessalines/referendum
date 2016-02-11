var commentsTemplate = $('#comments_template').html();
var recurse = $("#recurse").html();


var commentId = getLastUrlPath();

$(document).ready(function() {

  setupComments();

  setupBtns();
});

function setupComments() {
  getJson('get_comment/' + commentId).done(function(e) {
    var data = JSON.parse(replaceNewlines(e));
    console.log(data);
    fillMustacheWithJson(data, commentsTemplate, '#comments_div', {
      "recurse": recurse
    });

    $('#view_poll_btn').attr('href', '/poll/' + data[0]['pollAid']);

    initializeAllCommentVotes(data);


  });
}

function setupBtns() {

  getJson('get_comment_parent/' + commentId).done(function(e) {
    var parentId = e;
    console.log(parentId);
    if (parentId != "-1") {
      $('#view_parent_li').removeClass('hide');
      $('#view_parent_btn').attr('href', '/comment/' + parentId);
    }


  });
}
