$(document).ready(function() {

  console.log('got here');
  // With JQuery

  var obj = '#ex1';
  var slider = $(obj).bootstrapSlider({
      reversed: true
    })
    .on('slide', function() {
      RGBChange(obj);
    })
    .on('slideStop', function() {
      console.log('done voting');
      $(obj).attr('vote', true);
    });

  initializeSlider(obj);
  setupUnVote(obj);

  $('[data-toggle="tooltip"]').tooltip({
    container: 'body'
  });
});

function initializeSlider(obj) {
  $(obj).attr('vote', false);
  $(obj + 'Slider .slider-track-high').css('background', '#BABABA');
}

function RGBChange(obj) {

  // convert the value to 0-255
  var val = $(obj).bootstrapSlider('getValue');
  var correctId = obj + 'Slider';

  var calc = Math.floor(val * 255 / 10);
  var redVal = 255 - calc;
  var greenVal = calc;

  // console.log(redVal);
  $(correctId + ' .slider-track-high').css('background', 'rgb(' + redVal + ',' + greenVal + ',' + 0 + ')');
};

function setupUnVote(obj) {
  $('#unvote').click(function() {
      $(obj).bootstrapSlider('setValue', 5);
      $(obj + 'Slider .slider-track-high').css('background', '#BABABA');
      $(obj).attr('vote', false);


    })
    // $(obj).bootstrapSlider('setValue', '5.01');
    // console.log($(obj).bootstrapSlider('getValue'));
}
