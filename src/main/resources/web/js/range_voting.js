$(document).ready(function() {
  initializeAllRangeVotes();

  setupToolTips();

});


function initializeAllRangeVotes() {
  $('.range_vote').each(function() {
    var cId = '#' + this.id;
    setupRangeVote(cId);
  });
}

function setupRangeVote(obj) {

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

  initializeSlider(obj);
  setupClearVote(obj);
  setupThumbs(obj);

}

function slideStopActions(obj, cleared) {

  cleared = (typeof cleared === "undefined") ? false : cleared;

  console.log('done voting');
  $(obj).attr('vote', true);
  $(obj + '_vote').removeClass('hide');

  // set the color and tooltip
  if (!cleared) {
    $(obj + '_vote').css('color', RGBChange(obj));
    $(obj + '_vote').attr('title', 'Vote: ' + $(obj).bootstrapSlider('getValue')).tooltip('fixTitle');
  } else {
    $(obj + '_vote').css('color', '#333');
    $(obj + '_vote').attr('title', 'Vote').tooltip('fixTitle');
  }

  // $(obj + '_slider,' + obj + '_clear_vote').addClass('hide');
  $(obj + '_range_vote_table').addClass('hide');
  // $('.panel').foggy(false);
  // $('.tooltip').tooltip('destroy');
}

function setupThumbs(obj) {

  // Hide slider and clear by default

  // Unhide slider and clear
  $(obj + '_vote').click(function() {
    $(obj + '_range_vote_table').toggleClass('hide');
    // $(obj + '_slider' + ',' + obj + '_clear_vote').toggleClass('hide');
    $(obj + '_vote').addClass('hide');
    // $('.panel').foggy();
    // $('.tooltip').tooltip('destroy');
  });
}

function initializeSlider(obj) {
  $(obj).attr('vote', false);
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
