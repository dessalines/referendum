var pollId = getLastUrlPath();

$(document).ready(function() {
  setupUnlockForm();

});

function setupUnlockForm() {

  var unlockForm = $('#unlock_form');

  $('#unlock_form input[name=poll_id]').attr('value', pollId);

  $(unlockForm).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('unlock_poll', unlockForm, null, null, function() {

        delay(function() {
          window.location = '/poll/' + pollId;
        }, 1500);

      }, null, null);
    });

}
