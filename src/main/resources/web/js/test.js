

$(document).ready(function() {
	setupAddCandidateBtn();
	setupAddCandidateForm();
});

function setupAddCandidateBtn() {
	$('#add_a_candidate').click(function() {
		$(this).addClass('hide');
		$('#candidate_form').removeClass('hide');
	});
}

function setupAddCandidateForm() {
  var candidateForm = '#candidate_form';
  $(candidateForm).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('create_candidate', candidateForm, null, null, null, null, null);
    });
}