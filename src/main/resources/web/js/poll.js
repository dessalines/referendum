var pollTemplate = $('#poll_template').html();
var tagTemplate = $('#tag_template').html();
var candidatesTemplate = $('#candidates_template').html();
var commentsTemplate = $('#comments_template').html();
var recurse = $("#recurse").html();

var resultsData = null;

var pollAid = getLastUrlPath();

var activeTab = null;

var cMap = {};

$(document).ready(function() {
  setupAddCandidateBtn();
  setupAddCandidateForm();

  setupActiveTabEvents();

  setupPoll();
  setupCandidates();
  setupResults();

  setupPollForm();

  setupEditRedirect();

  setupComments();

  setupPollTags();

  setupTagSearch();

  setupClearPollTags();





});



function setupActiveTabEvents() {

  // First do a check to set the correct one
  activeTab = '#' + $("ul#main_tab_list li.active").attr('name');

  console.log(activeTab);

  $(window).unbind('scroll');
  if (activeTab == '#discuss_tab') {
    // setupCommentsWindowScrolling();
  } else if (activeTab == '#vote_tab') {
    setupCandidatesWindowScrolling();
  }

  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    activeTab = $(e.target).attr('href');

    console.log(activeTab);

    $(window).unbind('scroll');
    if (activeTab == '#discuss_tab') {
      // setupCommentsWindowScrolling();
    } else if (activeTab == '#vote_tab') {
      setupCandidatesWindowScrolling();
    }
  });
}

function setupClearPollTags() {

  $('#clear_poll_tags').unbind('click').click(function() {
    simplePost('clear_tags/' + pollAid, null, null,
      function() {
        setupPollTags();
      }, null, null, null);
  });

}

function setupTagSearch() {

  $('input[name=tag_id]').val('');

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

  tagList.initialize();

  var typeAhead = $('#tag_search_box .typeahead').typeahead({
    hint: true,
    highlight: true,
    minLength: 3,
  }, {
    name: 'tag_list',
    // displayKey: 'search_tag',
    source: tagList,
    display: 'name'
  }).bind('typeahead:selected', function(e, data, name) {
    // console.log(e);
    console.log(data);
    // console.log(name);

    // console.log(searchId);
    // $('#search_id').val(searchId);

    $('#tag_form input[name=tag_id]').val(data['id']);

    $('#tag_form').submit();
  }).bind('typeahead:render', function(e) {

    // Don't select the first one by default
    // $('#tag_form').parent().find('.tt-selectable:first').addClass('tt-cursor');

  });

  // $('[name=search_input]').focus();

  // $('.tt-input').focus();


  $("#tag_form").submit(function(event) {
    var formData = $("#tag_form").serializeArray();

    hideKeyboard($('#tag_form [name=search_input]'));

    // var classList = document.getElementsByName('creators_list').className.split(/\s+/);
    // console.log(classList);
    console.log(formData);

    var tagName = formData[0].value;

    // Save it into a different input field for some reason
    $('#tag_form input[name=tag_name]').val(tagName);


    // This removes the left tab considered active, so that it can be reshown with 
    // updated data
    $('li.active a[data-toggle="tab"]').parent().removeClass('active');

    if (tagName.length > 3 && tagName.length < 150) {
      // Save the poll tag
      standardFormPost('save_poll_tag', '#tag_form', null, null, function() {

        // refetch the poll tags
        setupPollTags();
        $('#tag_form input[name=tag_id]').val('');

        tagList.initialize();

      }, null, null, function() {
        $('#tag_form input[name=tag_id]').val('');
      });
    } else {
      toastr.error('Tag must be between 3 and 150 characters');
    }

    // console.log(searchString);


    event.preventDefault();
  });
}


function setupPollTags() {
  getJson('get_poll_tags/' + pollAid).done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, tagTemplate, '#tag_div');
  });
}

function setupComments() {
  getJson('get_comments/' + pollAid).done(function(e) {
    var data = JSON.parse(replaceNewlines(e));
    console.log(data);
    fillMustacheWithJson(data, commentsTemplate, '#comments_div', {
      "recurse": recurse
    });

    initializeAllCommentVotes(data);
  });
}

function setupEditRedirect() {
  if (window.location.hash == '#edit') {
    $('#edit_poll_div').removeClass('hide');
    $('#poll_div').addClass('hide');
  }
}





var candidatesStartIndex = 0;
var candidatesBrowsePageSize = 15;
var candidatesRecordCount = 1000;


function setupCandidatesWindowScrolling() {

  $(window).unbind('scroll');
  $(window).scroll(function() {
    if ($(window).scrollTop() + $(window).height() > $(document).height() - 100) {
      $(window).unbind('scroll');
      console.log('near bottom');
      candidatesStartIndex += candidatesBrowsePageSize;
      setupCandidates();
    }
  });
}


function setupCandidates() {

  var keepFetching = (candidatesStartIndex <= candidatesRecordCount);

  console.log(keepFetching);

  if (keepFetching) {

    getJson('get_poll_candidates/' + pollAid + '/' + candidatesBrowsePageSize + '/' + candidatesStartIndex).done(function(e) {
      var data = JSON.parse(replaceNewlines(e));
      console.log(data);
      candidatesRecordCount = data['record_count'];
      addToCandidateMap(data);


      if (candidatesStartIndex == 0) {
        $('#candidates_div').empty();
        // $('#candidates_div').empty();
        fillMustacheWithJson(data, candidatesTemplate, '#candidates_div');
      } else {
        // appending version
        fillMustacheWithJson(data, candidatesTemplate, '#candidates_div', null, true);
      }

      // Only set it up if you're on that current tab:
      if (activeTab == '#vote_tab') {
        setupCandidatesWindowScrolling();
      }

      initializeAllRangeVotes();
      setupCandidateBtns(data);
      setupDeleteCandidateBtn();
      setupEditCandidateBtn(data);
    });
  }
}

function setupPoll() {
  getJson('get_poll/' + pollAid).done(function(e) {

    if (e == 'incorrect_password') {
      toastr.error('This poll is private');
      delay(function() {
        window.location = '/private_poll/' + pollAid;
      }, 1000);
    }

    var data = JSON.parse(replaceNewlines(e));
    console.log(data);

    // If it's a passworded poll, and that password is in the cookie

    if (data['full_user_only'] == 1 && getCookie('username') === undefined) {
      console.log('full user only = ' + data['full_user_only']);
      toastr.error('This poll is for users only');
      delay(function() {
        window.location = '/';
      }, 1000);
    } else {

      fillMustacheWithJson(data, pollTemplate, '#poll_div');
      $('#comment_top_form input[name=discussion_id]').attr('value', data['discussion_id']);
    }

    // Setup the edit button, if its the right user
    if (getCookie('uid') == data['user_id']) {
      $('#edit_poll_btn').removeClass('hide');
      $('#delete_poll_btn').removeClass('hide');

      setupEditPollBtn();
      setupDeletePollBtn();
      fillPollForm(data);

    }
  });
}



function setupCandidateBtns(data) {

  console.log(cMap);

  $('.user_candidate_options_btns').each(function(i, obj) {
    var candidateId = obj.id.split('_').slice(-1)[0];

    if (getCookie('uid') == cMap[candidateId]['user_id']) {

      $('#' + obj.id).removeClass('hide');
      $('#' + obj.id).removeClass('hide');
    }
  })
}

function addToCandidateMap(data) {
  console.log(data);
  data.records.forEach(function(e) {
    var cId = e['id'];
    cMap[cId] = e;
  });
}

function setupDeleteCandidateBtn() {
  $('.delete_candidate_btn').unbind('click').click(function() {

    // get this id
    var cId = this.id.split('_').slice(-1)[0];
    console.log(cId);

    $('#delete_candidate_btn' + '_' + cId).text('Are you sure?');
    $('#delete_candidate_sure_yes_btn' + '_' + cId).removeClass('hide');
    $('#delete_candidate_sure_no_btn' + '_' + cId).removeClass('hide');
  });

  $('.delete_candidate_sure_no_btn').unbind('click').click(function() {
    var cId = this.id.split('_').slice(-1)[0];
    $('#delete_candidate_btn' + '_' + cId).text('Delete');
    $('#delete_candidate_sure_yes_btn' + '_' + cId).addClass('hide');
    $('#delete_candidate_sure_no_btn' + '_' + cId).addClass('hide');
  });

  $('.delete_candidate_sure_yes_btn').unbind('click').click(function() {
    var cId = this.id.split('_').slice(-1)[0];
    simplePost('delete_candidate/' + cId, null, null,
      function() {
        candidatesStartIndex = 0;
        setupCandidates();
        setupResults();
      }, null, null, null);
  });

}

function setupDeletePollBtn() {
  $('#delete_poll_btn').unbind('click').click(function() {
    $('#delete_poll_btn').text('Are you sure?');
    $('#delete_sure_yes_btn').removeClass('hide');
    $('#delete_sure_no_btn').removeClass('hide');
  });

  $('#delete_sure_no_btn').unbind('click').click(function() {
    $('#delete_poll_btn').text('Delete');
    $('#delete_sure_yes_btn').addClass('hide');
    $('#delete_sure_no_btn').addClass('hide');
  });

  $('#delete_sure_yes_btn').unbind('click').click(function() {
    simplePost('delete_poll/' + pollAid, null, null,
      function() {
        delay(function() {
          window.location = '/';
        }, 1000);
      }, null, null, null);
  });




}

function setupEditCandidateBtn(data) {


  $('.edit_candidate_btn').unbind('click').click(function() {
    var cId = this.id.split('_').slice(-1)[0];
    fillCandidateForm(cMap[cId]);

    $('#add_a_candidate').addClass('hide');
    $('#candidate_form').removeClass('hide');
    $("#edit_candidate_text").get(0).scrollIntoView();

    setTimeout("$('#candidate_subject').focus();", 0);
    updateTextAreaHeight();
  });


}

function fillCandidateForm(obj) {
  console.log(obj);
  $('input[name="candidate_id"]').val(obj['id']);
  $('#candidate_subject').val(obj['subject']);
  $('#edit_candidate_text').data('markdown').setContent(obj['text']);
}

function setupEditPollBtn() {

  $('#edit_poll_btn').unbind('click').click(function() {
    $('#edit_poll_div').removeClass('hide');
    $('#poll_div').addClass('hide');
    // updateTextAreaHeight();

  });
}


function setupAddCandidateBtn() {
  $('#add_a_candidate').unbind('click').click(function() {
    $(this).addClass('hide');

    // Resets the edit one if necessary
    $('input[name="candidate_id"]').val('');
    $('#candidate_subject').val('');
    $('#edit_candidate_text').data('markdown').setContent('');
    $('#candidate_form').removeClass('hide');

    setTimeout("$('#candidate_subject').focus();", 0);
    updateTextAreaHeight();
  });
}

function setupAddCandidateForm() {
  $('input[name="poll_id"]').val(pollAid);

  var candidateForm = '#candidate_form';
  $(candidateForm).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('save_candidate', candidateForm, null, null, function() {
        candidatesStartIndex = 0;
        setupCandidates();
        setupResults();
        $('#candidate_form').addClass('hide');
        $('#add_a_candidate').removeClass('hide');

        // Resets the edit one if necessary
        $('input[name="candidate_id"]').val('');
        $('#candidate_subject').val('');
        $('#edit_candidate_text').data('markdown').setContent('');

      }, null, null);
    });

  $('.candidate_save_cancel_btn').unbind('click').click(function() {
    $('#candidate_form').addClass('hide');
    $('#add_a_candidate').removeClass('hide');
  });


}





function setupResults() {
  getJson('get_poll_results/' + pollAid).done(function(e) {
    resultsData = JSON.parse(e);
    console.log(resultsData);

    graphResults();

  });
}




function graphResults() {

  var data = resultsData.results;

  var margin = {
      top: 0,
      right: 30,
      bottom: 30,
      left: 5
    },
    width = 420,
    barHeight = 20,
    height = barHeight * data.length;

  var w = window,
    d = document,
    e = d.documentElement,
    g = d.getElementsByTagName('body')[0],
    x = (w.innerWidth || e.clientWidth || g.clientWidth) * 0.87,
    y = w.innerHeight || e.clientHeight || g.clientHeight;

  var xScale = d3.scale.linear()
    .domain([0, d3.max(data, function(d) {
      return d.score / 10;
    })])
    .range([0, x]);

  var xAxis = d3.svg.axis()
    .scale(xScale)
    .orient("bottom");

  d3.select("g").remove();
  var chart = d3.select(".chart")
    // .attr("width", width + margin.left + margin.right)
    // .attr("height", height + marsgin.top + margin.bottom)
    .attr("width", x + margin.right)
    .attr("height", y + margin.top + margin.bottom)
    .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");


  var colors = d3.scale.category20();

  var bar = chart.selectAll("g")
    .data(data)
    .enter().append("g")
    .attr("data-toggle", "popover")
    .attr("data-placement", "auto")
    .attr("data-container", "body")
    .attr("data-trigger", "hover")
    .attr("title", function(d) {
      return d.candidate_obj.subject + ' <small>' + 'score: ' + scoreFix(d) + ' | votes: ' + d.count + '</small>';
    })
    .attr("data-content", function(d) {
      return markdown.toHTML(replaceNewlines(d.candidate_obj.text, true));
    })
    .attr("data-html", true)
    .attr("transform", function(d, i) {
      return "translate(0," + i * barHeight + ")";
    })
    .style({
      fill: randomColor
    });

  chart.append("g")
    .attr("class", "x axis")
    .attr("transform", "translate(0," + height + ")")
    .call(xAxis);

  chart.append("text")
    .attr("transform", "translate(" + (x / 2) + " ," + (height + 30) + ")")
    .style("text-anchor", "middle")
    .text("Score");

  bar.append("rect")
    .attr("width", function(d) {
      return xScale(scoreFix(d));
    })
    .attr("height", barHeight - 1);

  bar.append("text")
    .attr("x", function(d) {
      return xScale(scoreFix(d)) - 3;
    })
    .attr("y", barHeight / 2)
    .attr("dy", ".35em")
    .text(function(d) {
      return scoreFix(d, true);
    });

  $('[data-toggle="popover"]').popover();

}


// Necessary for d3 to re-render
window.onresize = graphResults;

function scoreFix(d, text) {
  var adjScore = (+d.score / 10);
  if (adjScore < 0.2 && text === undefined) {
    adjScore = 0.2
  };

  return adjScore.toFixed(1);
}


function setupPollForm() {

  setTimeout("$('#poll_form [name=subject]').focus();", 0);


  var pollForm = '#poll_form';

  // Setting the vars
  $(pollForm).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('save_poll', pollForm, null, null, function() {
        setupPoll();
        $('#edit_poll_div').addClass('hide');
        $('#poll_div').removeClass('hide');
      }, null, null);
    });

  $(pollForm + ' input').on('change', function() {
    var radioSelected = $(pollForm + " input[type='radio']:checked").val();

    if (radioSelected == 'private') {
      $('#private_password').removeClass('hide');
    } else {
      $('#private_password').addClass('hide');
    }
  });

  $('.poll_save_cancel_btn').unbind('click').click(function() {
    $('#edit_poll_div').addClass('hide');
    $('#poll_div').removeClass('hide');
  });

}

function fillPollForm(data) {

  console.log(data);

  $('input[name="poll_id"]').val(pollAid);
  $('input[name="subject"]').val(data['subject']);
  $("input[name=sum_type_radio][value='" + data['poll_sum_type_id'] + "']").prop("checked", true);
  $('#edit_poll_text').data('markdown').setContent(data['text']);
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



}
