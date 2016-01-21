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
  $('#user_dropdown').text('User ' + getCookie('uid'));
}

function setupCreateEmptyPoll() {
  $('.create_empty_poll').click(function() {
    simplePost('create_empty_poll', null, null,
      function(pollAid) {
        delay(function() {
          window.location = 'edit_poll/' + pollAid;
        }, 1000);

      }, true, null, null);
  });
}
