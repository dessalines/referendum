$(document).ready(function() {


  initializeAllRangeVotes();

  $('[data-toggle="tooltip"]').tooltip({
    container: 'body'
  });


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
      console.log('done voting');
      $(obj).attr('vote', true);
      $(obj + '_vote').removeClass('hide');
    });

  initializeSlider(obj);
  setupClearVote(obj);
  setupThumbs(obj);
  


}

function setupThumbs(obj) {

	// Hide slider and clear by default
	$(obj + '_slider'  + ',' + obj + '_clear_vote').addClass('hide');

	// Unhide slider and clear
	$(obj + '_vote').click(function() {
		$(obj + '_slider' + ',' + obj + '_clear_vote').toggleClass('hide');
		$(obj + '_vote').addClass('hide');
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
  $(selector).css('background', 'rgb(' + redVal + ',' + greenVal + ',' + 0 + ')');
};

function setupClearVote(obj) {
  $(obj + '_clear_vote').click(function() {
    var obj = '#' + $(this).parent().find('input').attr('id');
    console.log(obj);
    $(obj).bootstrapSlider('setValue', 5);
    $(obj + '_slider .slider-track-high').css('background', '#BABABA');
    $(obj).attr('vote', false);

  });
  // $(obj).bootstrapSlider('setValue', '5.01');
  // console.log($(obj).bootstrapSlider('getValue'));
}
