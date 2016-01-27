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
