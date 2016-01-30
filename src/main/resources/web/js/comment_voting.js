var pollId = getLastUrlPath();

$(document).ready(function() {
  // initializeAllRangeVotes();



});


function initializeAllCommentVotes(data) {

  $('.comment_vote').each(function() {
    var commentId = this.id.split("_").slice(-1)[0];
    console.log(commentId);
    setupCommentVote(commentId);
  });


  setupToolTips();
}

function votesArrayToMap(arr) {
  var a = {};

  arr.forEach(function(d) {
    a[d['candidate_id']] = d;
  });

  // console.log(a);

  return a;

}


function setupCommentVote(commentId) {

  var commentSliderObj = $('#comment_slider_' + commentId);

  // With JQuery
  var slider = commentSliderObj.bootstrapSlider({
      reversed: true,
      tooltip: 'show'
    })
    .on('slide', function() {
      commentRGBChange(commentId);
    })
    .on('slideStop', function() {
      commentSlideStopActions(commentId);
    });

  initializeCommentSlider(commentId);
  setupCommentAverages(commentId);
  setupCommentClearVote(commentId);
  setupCommentThumbs(commentId);
  setupCommentEdit(commentId);
  setupCommentDelete(commentId);
  setupCommentReply(commentId);
  setupCommentSource(commentId);

}

function setupCommentSource(commentId) {

  // Basically, just swap the comment edit source and comment text,
  // and change the name of the button
  var commentSourceBtn = $('#comment_source_btn_' + commentId);


  commentSourceBtn.click(function() {


    var commentText = $('#comment_text_' + commentId);
    var commentEditText = $('#comment_edit_text_' + commentId);
    var temp = commentText.html();

    console.log(commentEditText.html());
    console.log(commentEditText.text());
    console.log(temp);

    if (commentSourceBtn.text() == 'source') {

      console.log(commentText.html());

      commentText.html(commentEditText.text().replace(/\n/g,"<br>"));
      commentEditText.html(temp);
      commentSourceBtn.text('text');

    } else {

      console.log(commentText.html());

      commentText.html(commentEditText.text());
      commentEditText.html(temp);

      commentSourceBtn.text('source');
    }
  });



}

function setupCommentDelete(commentId) {
  var uid = getCookie('uid');

  var commentDeleteBtn = $('#comment_delete_btn_' + commentId);
  var userId = commentDeleteBtn.attr('user-id');

  if (uid == userId) {
    commentDeleteBtn.parent().removeClass('hide');
  }

  commentDeleteBtn.click(function() {

    simplePost('delete_comment/' + commentId, null, null,
      function() {
        $('#comment_panel_' + commentId).remove();
      }, null, null, null);
  });

}

function setupCommentEdit(commentId) {
  var uid = getCookie('uid');

  var commentEditForm = $('#comment_edit_form_' + commentId);
  var commentEditBtn = $('#comment_edit_btn_' + commentId);
  var commentText = $('#comment_text_' + commentId);
  var commentEditTextArea = $('#comment_edit_text_' + commentId);
  var editText = commentEditTextArea.text();
  console.log(editText);
  commentEditTextArea.markdown({
    onShow: function(e) {
      e.setContent(editText);
    }
  });


  // commentEditTextArea.data('markdown').setContent(editText);
  var userId = commentEditBtn.attr('user-id');

  console.log(userId);
  if (uid == userId) {
    commentEditBtn.parent().removeClass('hide');
  }

  commentEditBtn.click(function() {
    commentText.addClass('hide');
    commentEditForm.removeClass('hide');
    updateTextAreaHeight();
  });


  commentEditForm.bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('edit_comment', commentEditForm, null, null, function() {

        // Change the text
        var md = commentEditTextArea.data('markdown').getContent().replace(/(\r\n|\n|\r)/gm, "--lb--");
        var text = markdown.toHTML(replaceNewlines(md, true, true));
        console.log(md);
        console.log(text);
        commentText.html(text);

        commentEditForm.addClass('hide');
        commentText.removeClass('hide');

      }, null, null);
    });


}

function setupCommentReply(commentId) {

  var commentReplyForm = $('#comment_reply_form_' + commentId);
  var commentReplyBtn = $('#comment_reply_btn_' + commentId);
  var commentText = $('#comment_reply_text_' + commentId);
  var commentReplyTextArea = $('#comment_reply_edit_text_' + commentId);
  var editText = commentReplyTextArea.text();
  console.log(editText);
  commentReplyTextArea.markdown({});


  // commentEditTextArea.data('markdown').setContent(editText);


  commentReplyBtn.click(function() {
    commentReplyForm.removeClass('hide');
  });


  commentReplyForm.bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('create_comment', commentReplyForm, null, null, function() {

        // Need to refetch the comment, for stuff like permalinks, and correct threading.
        setupComments();

        // // Change the text
        // var md = commentEditTextArea.data('markdown').getContent().replace(/(\r\n|\n|\r)/gm, "--lb--");
        // var text = markdown.toHTML(replaceNewlines(md, true, true));
        // console.log(md);
        // console.log(text);
        // commentText.html(text);

        // commentReplyForm.addClass('hide');
        // commentText.removeClass('hide');

      }, null, null);
    });


}

function setupCommentAverages(commentId) {
  var commentRankObj = $('#comment_vote_rank_' + commentId);

  // format and divide by 10
  var voteText = commentRankObj.text().trim();

  console.log(voteText);
  if (voteText != '-1') {
    var avgRank = parseFloat(commentRankObj.text()) / 10;
    var adjRank = avgRank.toFixed(1);

    commentRankObj.text(adjRank);

    var color = colorChange(adjRank);
    commentRankObj.css('background-color', color);
  } else {
    commentRankObj.text("?");
  }

}

function commentSlideStopActions(commentId, cleared) {

  cleared = (typeof cleared === "undefined") ? false : cleared;

  var commentSliderObj = $('#comment_slider_' + commentId);

  console.log('done voting');
  commentSliderObj.attr('vote', true);

  var commentVoteObj = $('#comment_vote_' + commentId);
  commentVoteObj.removeClass('hide');

  var commentVoteRankObj = $('#comment_vote_rank_' + commentId);


  // set the color and tooltip
  var rank = null;
  if (!cleared) {
    rank = commentSliderObj.bootstrapSlider('getValue');
    var color = commentRGBChange(commentId);
    commentVoteObj.css('color', color);

    commentVoteObj.attr('title', 'Vote: ' + rank).tooltip('fixTitle');
  } else {
    rank = null;
    commentVoteObj.css('color', '#888');
    commentVoteObj.attr('title', 'Vote').tooltip('fixTitle');
  }

  // $(obj + '_slider,' + obj + '_clear_vote').addClass('hide');
  $('#comment_vote_table_' + commentId).addClass('hide');
  // $('.panel').foggy(false);
  // $('.tooltip').tooltip('destroy');


  console.log('rank = ' + rank);

  // Always save to 10
  if (rank != null) {
    rank = rank * 10;
  }

  simplePost('save_comment_vote/' + commentId + '/' + rank, null, null,
    function() {
      // alert('ballot saved');
      // TODO reload comments?
    }, null, null, null);

  removeOverlay();
  // recalculate the poll results

}

function setupCommentThumbs(commentId) {

  // Hide slider and clear by default
  var commentVoteObj = $('#comment_vote_' + commentId);


  // Only allow voting on comments that aren't your own
  var uid = getCookie('uid');
  var userId = commentVoteObj.attr('user-id');


  if (uid != userId) {
    // Unhide slider and clear
    commentVoteObj.click(function() {
      var commentVoteTableObj = $('#comment_vote_table_' + commentId);
      commentVoteTableObj.toggleClass('hide');
      $('[data-toggle="tooltip"]').tooltip('hide');

      // $(obj + '_slider' + ',' + obj + '_clear_vote').toggleClass('hide');
      commentVoteObj.addClass('hide');

      addOverlay();
      // $('.panel').foggy();
      // $('.tooltip').tooltip('destroy');
    });
  } else {
    commentVoteObj.removeClass('hand');
    commentVoteObj.addClass('not-allowed');
    commentVoteObj.attr('title', 'Can\'t vote on your own comments').tooltip('fixTitle');
  }
}

function initializeCommentSlider(commentId) {

  var commentSliderObj = $('#comment_slider_' + commentId);
  var vote = commentSliderObj.attr('user-rank');
  // console.log(vote);
  if (vote -= '-1') {
    var voteNum = parseFloat(vote) / 10;

    // Fill the data
    commentSliderObj.bootstrapSlider('setValue', voteNum);

    var commentVoteObj = $('#comment_vote_' + commentId);
    var color = commentRGBChange(commentId);
    commentVoteObj.css('color', color);
    commentVoteObj.attr('title', 'Vote: ' + voteNum).tooltip('fixTitle');

  } else {
    commentSliderObj.attr('vote', false);
  }

  $('#comment_slider_special' + commentId).find('.slider-track-high').css('background', '#BABABA');


}

function commentRGBChange(commentId) {

  // convert the value to 0-255

  var commentSliderObj = $('#comment_slider_' + commentId);
  var commentSliderSpecialObj = $('#comment_slider_special_' + commentId);

  var val = commentSliderObj.bootstrapSlider('getValue');
  // $(obj).bootstrapSlider('setAttribute','tooltip','show');
  // console.log($(obj).bootstrapSlider('getAttribute','tooltip'));

  // var correctId = '#comment_slider_' + commentId + '_slider';

  var calc = Math.floor(val * 255 / 10);
  var redVal = 255 - calc;
  var greenVal = calc;

  // console.log(redVal);
  var selector = commentSliderSpecialObj.find('.slider-track-high');
  // console.log(selector);
  var color = 'rgb(' + redVal + ',' + greenVal + ',' + 0 + ')';
  $(selector).css('background', color);

  return color;
};

function colorChange(val) {
  // $(obj).bootstrapSlider('setAttribute','tooltip','show');
  // console.log($(obj).bootstrapSlider('getAttribute','tooltip'));

  // var correctId = '#comment_slider_' + commentId + '_slider';

  var calc = Math.floor(val * 255 / 10);
  var redVal = 255 - calc;
  var greenVal = calc;

  var color = 'rgb(' + redVal + ',' + greenVal + ',' + 0 + ')';

  return color;

}



function setupCommentClearVote(commentId) {

  var commentClearVoteObj = $('#comment_clear_vote_' + commentId);

  commentClearVoteObj.click(function() {
    var commentSliderObj = $('#comment_slider_' + commentId);
    console.log(commentSliderObj);
    commentSliderObj.bootstrapSlider('setValue', 5);
    $('#comment_slider_special_' + commentId + ' .slider-track-high').css('background', '#BABABA');
    commentSliderObj.attr('vote', false);

    commentSlideStopActions(commentId, true);

  });

}
