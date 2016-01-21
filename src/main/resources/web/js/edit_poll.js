var pollId = getLastUrlPath();

$(document).ready(function() {
  setupPollForm();
});

function setupPollForm() {
  var pollForm = '#poll_form';

  // Setting the poll id
  $('input[name="poll_id"]').val(pollId);

  $(pollForm).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('save_poll', pollForm, null, null, null, null, null);
    });

  $(pollForm + ' input').on('change', function() {
    var radioSelected = $(pollForm + " input[type='radio']:checked").val();

    if (radioSelected == 'private') {
      $('#private_password').removeClass('hide');
    } else {
      $('#private_password').addClass('hide');
    }
  });

}
