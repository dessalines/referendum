var pollId = getLastUrlPath();

$(document).ready(function() {
  setupPollForm();
});

function setupPollForm() {
  var pollForm = '#poll_form';

  // Setting the vars
  fillPollForm();

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

function fillPollForm() {
  getJson('get_poll/' + pollId).done(function(e) {

  	console.log(e);

    var data = JSON.parse(replaceNewlines(e));

    console.log(data);

    $('#view_poll').attr('href','/poll/' + pollId);
    $('input[name="poll_id"]').val(pollId);
    $('input[name="subject"]').val(data['subject']);
    $("input[name=sum_type_radio][value='" + data['poll_sum_type_id'] + "']").prop("checked", true);
    $('#poll_text').data('markdown').setContent(data['text']);
    updateTextAreaHeight();

    var password = data['private_password'];
    console.log(password);
    if (password != null) {
      $("input[name=public_radio][value='private']").prop("checked", true);
      $('input[name="private_password"]').val(data['private_password']);
      $('#private_password').removeClass('hide');
      $('#advanced_options').collapse('show');
    } else {
      $("input[name=public_radio][value='public']").prop("checked", true);
    }


  });
}
