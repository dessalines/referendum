// var categoryId = getLastUrlPath();

var ballotTemplate = $('#ballot_template').html();
var ballotDiv = '#ballot_form';

$(document).ready(function() {
  setupPollForm();
  setupCandidateForm(4);

  fillBallot(4);
  setupBallotForm(4);

});


function setupPollForm() {
  var pollForm = '#poll_form';
  $(pollForm).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('create_poll', pollForm, null, null, null, null, null);
    });
}

function setupCandidateForm(pollId) {
  var candidateForm = '#candidate_form';
  $(candidateForm).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('create_candidate/' + pollId, candidateForm, null, null, null, null, null);
    });
}

function setupBallotForm(pollId) {
  var ballotForm = '#ballot_form';
  $(ballotForm).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      var formData = $(ballotForm).serializeArray();
      console.log(formData);
      standardFormPost('create_ballot/' + pollId, ballotForm, null, null, null, null, null);
    });
}


function fillBallot(pollId) {
  getJson('get_poll_candidates/' + pollId).done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, ballotTemplate, ballotDiv);
    setupSelects(data);
  });
}


function fillSelects(data) {
  var blankRank = $('<option></option>').attr("value", "").text("---");

  $('.select_candidate').empty().append(blankRank);

  for (var i = 1; i <= data.length; i++) {
    var option = $('<option></option>').attr("value", i).text(i);
    $('.select_candidate').append(option);

  }

  // select the correct data, and fill the old value
  // TODO
  $(data).each(function() {
    console.log(this);
    $('#select_candidate_' + this.id).attr('oldValue', '');
    $('#select_candidate_' + this.id).attr('newValue', '');
  });
}


function setupSelects(data) {

  fillSelects(data);

  setupRankButton(data);

  setupRankNextButton(data);

}

function setupRankButton(data) {

  // Now if the selects are changed, resort, and make them distinct

  $(".select_candidate").on('change', function(e) {

    // console.log(e);
    // console.log(this.id);
    // console.log(this);

    var changedId = this.id.split('_').slice(-1)[0];
    var newValue = this.value;
    var oldValue = $('#select_candidate_' + changedId).attr('oldValue');
    $('#select_candidate_' + changedId).attr('newValue', newValue);


    // console.log('ov = ' + oldValue + ' cv = ' + newValue);

    // set that as the selected option
    $('#select_candidate_' + changedId).attr('oldValue', newValue);

    // Loop over all the other selects, and remove that option
    $(data).each(function() {
      var id = this.id;


      // If its changed to a number, remove that number from others
      if (newValue != "" && id != changedId) {
        $("#select_candidate_" + id + " option[value='" + newValue + "']").remove();
        // $('#select_candidate_' + id).sort_select_box();
      }


      if (oldValue != "") {

        // loop over all the others and this old one back in.
        var option = $('<option></option>').attr("value", oldValue).text(oldValue);

        $(data).each(function() {
          if (id != changedId) {
            $('#select_candidate_' + id).append(option);
          }
        });
      }

      // Sort the options correctly
      $('#select_candidate_' + id).sort_select_box();

    });

    // Sort the table
    sortTableCustom('#ballot_table');

  });


}

var cRank = 1;

function setupRankNextButton(data) {
  $('.rank_next_candidate').click(function() {
    var id = this.id.split('_').splice(-1)[0];
    var rank = $(this).attr('rank');
    var rankNext = $(this);

    // console.log(id);
    console.log(rank);



    if (rank > 0) {
      $(this).addClass('btn-default');
      $(this).removeClass('btn-success');
      var oldRank = $(this).attr('rank');
      $(this).attr('rank', 0);
      $('#select_candidate_' + id).val('').change();
      // change all with a greater rank, 
      // move them down one

      var rows = $('#ballot_table').children('tbody').children('tr');

      var difference = cRank - oldRank - 1;
      console.log('difference = ' + difference);

      var maxRank = 0;
      $(rows).each(function() {
        // console.log(this);

        var rowRank = parseInt($(this).find('td:nth-child(2) select :selected').text());
        var rowId = $(this).find('td:nth-child(1) button').attr('id').split('_').slice(-1)[0];
        // console.log(rowRank);
        // console.log(rowId);

        var newRowRank = parseInt(rowRank);

        // Set those values
        if (rowRank > rank) {

          newRowRank--;
          // console.log('Setting rank for id = ' + this.id + ' rank = ' + newRowRank);
          $(this).find('td:nth-child(1) button').attr('rank', newRowRank);
          $(this).find('td:nth-child(2) select').val(newRowRank).change();
          // $(this).find('td:nth-child(2) select').val(newRowRank);

        } else {
          $('#rank_next_candidate_' + id).attr('rank', 0);
        }

        // Set the new max cRank
        if (maxRank < newRowRank) {
          maxRank = newRowRank;
        }

        cRank = maxRank + 1;


        // console.log(maxRank);
        // console.log(cRank);

      });





    }

    // Set the val for the select to the cRank
    else {
      $(this).removeClass('btn-default');
      $(this).addClass('btn-success');
      $(this).attr('rank', cRank);
      $('#select_candidate_' + id).val(cRank++).change();
    }

  });
}


function sortTableCustom(tableName) {

  var table = $(tableName);
  var id = table.attr('id');

  // Fetch the rows
  var rows = table.children('tbody').children('tr');


  rows.sort(function(a, b) {
    var a = parseInt($(a).find('td:nth-child(2) select :selected').text(), 10);
    var b = parseInt($(b).find('td:nth-child(2) select :selected').text(), 10);

    // setting the NaNs to end up at the bottom
    if (isNaN(a)) {
      a = Number.MAX_VALUE;
    }
    if (isNaN(b)) {
      b = Number.MAX_VALUE;
    }

    if (a == b) return 0;
    return a > b ? 1 : -1;
  });

  // Do the sorting
  for (var i = 0; i < rows.length; i++) {
    table.append(rows[i]);
  }

}
