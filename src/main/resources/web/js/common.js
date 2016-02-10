$(document).ready(function() {

  getAuth();
  setupCreateEmptyPoll();
  setupLoginForm();
  setupSearchBar();
});

function setupSearchBar() {
  // $('input[name=tag_id]').val('');

  var tagUrl = sparkService + 'tag_search/%QUERY';
  var tagList = new Bloodhound({
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    datumTokenizer: Bloodhound.tokenizers.whitespace,
    // prefetch: '../data/films/post_1960.json',
    remote: {
      url: tagUrl,
      wildcard: '%QUERY'
    }
  });

  var pollUrl = sparkService + 'poll_search/%QUERY';
  var pollList = new Bloodhound({
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    datumTokenizer: Bloodhound.tokenizers.whitespace,
    // prefetch: '../data/films/post_1960.json',
    remote: {
      url: pollUrl,
      wildcard: '%QUERY'
    }
  });

  tagList.initialize();
  pollList.initialize();

  var typeAhead = $('#search_box .typeahead').typeahead({
    hint: true,
    highlight: true,
    minLength: 3,
  }, {
    name: 'tag_list',
    displayKey: 'name',
    source: tagList,
    templates: {
      header: '<h3 class="search-set">Tags</h3>',
      suggestion: function(context) {
        return Mustache.render('<div>{{{name}}} </div>', context);
      }
    }
  }, {
    name: 'poll_list',
    displayKey: 'subject',
    source: pollList,
    templates: {
      header: '<h3 class="search-set">Polls</h3>',
      suggestion: function(context) {
        return Mustache.render('<div>{{{subject}}} </div>', context);
      }
    }
  }).bind('typeahead:selected', function(e, data, name) {
    // console.log(e);
    console.log(data);
    // console.log(name);

    // Setting the search info
    $('#search_id').val(data['id']);
    if (data.hasOwnProperty('subject')) {
      $('#search_type').val('poll');
    } else {
      $('#search_type').val('type');
    }
    

    // $('input[name=tag_id]').val(data['id']);

    $('#search_form').submit();
  }).bind('typeahead:render', function(e) {

    // Don't select the first one by default
    $('#search_form').parent().find('.tt-selectable:first').addClass('tt-cursor');

  });

  // $('[name=search_input]').focus();

  // $('.tt-input').focus();
  setTimeout("$('[name=search_input]').focus();", 0);

    setTimeout("$('[name=search_input]').focus();", 0);


  $("#search_form").submit(function(event) {
    var formData = $("#search_form").serializeArray();

    hideKeyboard($('[name=search_input]'));

    console.log(formData);

    var searchId = formData[0].value;
    var searchType = formData[1].value;

    if (searchType == 'poll') {
      window.location = '/poll/' + searchId;
    } else {
      window.location = '/tag/' + searchId;
    }

    event.preventDefault();
  });
}


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
          window.location = '/poll/' + pollAid + '#edit';
        }, 1000);

      }, true, null, null);
  });
}
