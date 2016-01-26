var pollId = getLastUrlPath();

$(document).ready(function() {
  // initializeAllRangeVotes();



});


function initializeAllRangeVotes() {

  getJson('get_user_poll_votes/' + pollId).done(function(e) {
    var data = JSON.parse(e);

    console.log(data);

    var votesToCandidateMap = votesArrayToMap(data);

    $('.range_vote').each(function() {
      var candidateId = this.id.split("_")[1];
      var cId = '#' + this.id;
      setupRangeVote(cId, votesToCandidateMap[candidateId]);
    });
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


function setupRangeVote(obj, vote) {

  // With JQuery
  var slider = $(obj).bootstrapSlider({
      reversed: true,
      tooltip: 'show'
    })
    .on('slide', function() {
      RGBChange(obj);
    })
    .on('slideStop', function() {
      slideStopActions(obj);

    });

  initializeSlider(obj, vote);
  setupClearVote(obj);
  setupThumbs(obj);

}

function slideStopActions(obj, cleared) {

  cleared = (typeof cleared === "undefined") ? false : cleared;

  console.log('done voting');
  $(obj).attr('vote', true);
  $(obj + '_vote').removeClass('hide');

  

  // set the color and tooltip
  var rank = null;
  if (!cleared) {
    rank = $(obj).bootstrapSlider('getValue');
    var color = RGBChange(obj);
    $(obj + '_vote').css('color', color);
    $(obj + '_vote_rank').css('background-color', color);
    $(obj + '_vote').attr('title', 'Vote: ' + rank).tooltip('fixTitle');
  } else {
    rank = null;
    $(obj + '_vote').css('color', '#888');
    $(obj + '_vote_rank').css('background-color', '#888');
    $(obj + '_vote').attr('title', 'Vote').tooltip('fixTitle');
  }

  // $(obj + '_slider,' + obj + '_clear_vote').addClass('hide');
  $(obj + '_range_vote_table').addClass('hide');
  // $('.panel').foggy(false);
  // $('.tooltip').tooltip('destroy');

  var candidateId = obj.split("_")[1];
  console.log('candidate id = ' + candidateId);




  console.log('rank = ' + rank);

  // Always save to 10
  if (rank != null) {
    $(obj + '_vote_rank').removeClass('hide');
    $(obj + '_vote_rank').text(rank);
    rank = rank * 10;
  } else {
    $(obj + '_vote_rank').addClass('hide');
  }

  simplePost('save_ballot/' + pollId + '/' + candidateId + '/' + rank, null, null,
    function() {
      // alert('ballot saved');
       setupResults();
    }, null, null, null);


  // recalculate the poll results
 
}

function setupThumbs(obj) {

  // Hide slider and clear by default

  // Unhide slider and clear
  $(obj + '_vote').click(function() {
    $(obj + '_range_vote_table').toggleClass('hide');
    $('[data-toggle="tooltip"]').tooltip('hide');
    
    // $(obj + '_slider' + ',' + obj + '_clear_vote').toggleClass('hide');
    $(obj + '_vote').addClass('hide');
    // $('.panel').foggy();
    // $('.tooltip').tooltip('destroy');
  });
}

function initializeSlider(obj, vote) {


  // console.log(vote);
  if (vote != null) {
    var voteNum = parseFloat(vote['rank']) / 10;

    // Fill the data
    $(obj).bootstrapSlider('setValue', voteNum);
    var color = RGBChange(obj);
    $(obj + '_vote').css('color', color);
    $(obj + '_vote_rank').css('background-color', color);
    $(obj + '_vote').attr('title', 'Vote: ' + voteNum).tooltip('fixTitle');
    $(obj + '_vote_rank').removeClass('hide');
    $(obj + '_vote_rank').text(voteNum);

  } else {
    $(obj).attr('vote', false);
  }

  $(obj + 'Slider .slider-track-high').css('background', '#BABABA');


}

function RGBChange(obj) {

  // convert the value to 0-255

  var val = $(obj).bootstrapSlider('getValue');
  // $(obj).bootstrapSlider('setAttribute','tooltip','show');
  // console.log($(obj).bootstrapSlider('getAttribute','tooltip'));

  var correctId = obj + '_slider';

  var calc = Math.floor(val * 255 / 10);
  var redVal = 255 - calc;
  var greenVal = calc;

  // console.log(redVal);
  var selector = $(correctId).find('.slider-track-high');
  // console.log(selector);
  var color = 'rgb(' + redVal + ',' + greenVal + ',' + 0 + ')';
  $(selector).css('background', color);

  return color;
};



function setupClearVote(obj) {
  $(obj + '_clear_vote').click(function() {
    console.log(obj);
    $(obj).bootstrapSlider('setValue', 5);
    $(obj + '_slider .slider-track-high').css('background', '#BABABA');
    $(obj).attr('vote', false);

    slideStopActions(obj, true);

  });

}
