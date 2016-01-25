$(document).ready(function() {

  getUser();
  setupCreateEmptyPoll();
});


function getUser() {
  if (getCookie('uid') == null) {
    console.log('cookie was undefined');
    getJson('get_user').done(function() {
      setUserInfo();
    });
  } else {
    setUserInfo();
  }


}

function setUserInfo() {
  var ui = 'User ' + getCookie('uid') + ' <span class="caret"></span>';
  $('#user_dropdown').html(ui);
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
