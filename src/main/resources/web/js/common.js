$(document).ready(function() {

  getAuth();
  setupCreateEmptyPoll();
  setupLoginForm();
});


function getAuth() {
  if (getCookie('auth') == null) {

    deleteCookie('auth');
    deleteCookie('uid');
    deleteCookie('username');
    console.log('cookie was undefined');
    getJson('get_user').done(function() {
      // setUserInfo();

    });
  } else {

    if (getCookie('username') != null) {
      showLoggedIn();
    }
    
  }


}

function showLoggedIn() {
  $('#login_modal').modal('hide');
  $('.logged-out').addClass('hide');
  $('.logged-in').removeClass('hide');
  $('#user_dropdown').html(getCookie('username') + ' <span class="caret"></span>');
}

function setupLoginForm() {

  var loginForm = '#login_form';
  $(loginForm).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('login', loginForm, null, null, function() {

        showLoggedIn();
      }, null, null);
    });

  var signupForm = '#signup_form';
  $(signupForm).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('signup', signupForm, null, null, function() {
        $('#login_modal').modal('hide');
        showLoggedIn();
      }, null, null);
    });

}


function setupCreateEmptyPoll() {
  $('.create_empty_poll').click(function() {
    simplePost('create_empty_poll', null, null,
      function(pollAid) {
        delay(function() {
          window.location = 'poll/' + pollAid + '#edit';
        }, 1000);

      }, true, null, null);
  });
}
