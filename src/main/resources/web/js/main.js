// var categoryId = getLastUrlPath();

var ballotTemplate = $('#ballot_template').html();
var ballotDiv = '#ballot_div';

$(document).ready(function() {
  setupPollForm();
  setupCandidateForm(4);

  fillBallot(4);

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
    $('#select_candidate_' + this.id).attr('newValue', '6');
  });
}


function setupSelects(data) {

  fillSelects(data);



  // Now if the selects are changed, resort, and make them distinct

  $(".select_candidate").on('change', function(e) {



    // console.log(e);
    console.log(this.id);
    // console.log(this);

    var changedId = this.id.split('_').slice(-1)[0];
    var newValue = this.value;
    var oldValue = $('#select_candidate_' + changedId).attr('oldValue');
    $('#select_candidate_' + changedId).attr('newValue', newValue);




    console.log('ov = ' + oldValue + ' cv = ' + newValue);

    // set that as the selected option
    // $('#select_candidate_' + changedId).val(newValue);
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
        console.log('got here');

        // loop over all the others and this old one back in.
        var option = $('<option></option>').attr("value", oldValue).text(oldValue);

        $(data).each(function() {
          if (id != changedId) {
            $('#select_candidate_' + id).append(option);
            // now sort them
          }
        });
      }

      // Sort the options correctly
      $('#select_candidate_' + id).sort_select_box();



    });



    // Sort the table
    sortTableCustom('#ballot_table');

  });



  // <option name="{{name}}" value="{{id}}">{{name}}</option>
}



function sortTableCustom(tableName) {

  var table = $(tableName);
  var id = table.attr('id');

  // Create a clone of the rows
  var rows = table.children('tbody').children('tr');


  rows.sort(function(a, b) {
    var a = parseInt($(a).find('td:nth-child(2) select :selected').text(), 10);
    var b = parseInt($(b).find('td:nth-child(2) select :selected').text(), 10);
    // console.log(a);
    // console.log(b);

    // setting the NaNs to end up at the bottom
    if (isNaN(a)) {
      a = 999999;
    }
    if (isNaN(b)) {
      b = 999999;
    }

    if (a == b) return 0;
    return a > b ? 1 : -1;
  });

  // Do the sorting
  for (var i = 0; i < rows.length; i++){
  	table.append(rows[i])
  }


  // console.log(rows);

  // // var sortedTbody = $('<tbody id="ballot_body"></tbody>');
  // // var oldTbody = document.getElementById("ballot_body");
  // // var sortedTbody = document.createElement('tbody');
  // var sortedTbody = $('<div></div>');

  // $(rows).each(function() {

  //   console.log(this);

  //   var select = $(this).find('select');
  //   select.val(select.newValue);

  //   console.log(select);
  //   console.log(select.newValue);

  //   sortedTbody.append(this);

  // });

  // console.log(sortedTbody);
  // console.log(allRows);
  // console.log(oldTbody);

  // oldTbody.parentNode.replaceChild(sortedTbody, oldTbody);

  // $(tableName + " tr").remove();


  // console.log(rows.html());
  // replace the rows with the sorted rows
  // $(this).find('tbody').empty();
  // console.log(document.getElementById("ballot_body").innerHTML);
  // console.log(sortedTbody.html());

  // document.getElementById("ballot_body").innerHTML = sortedTbody.html();


  // #ballot_table > tbody:nth-child(2)
  // $(rows).each(function() {
  //   console.log(this);
  //   $("#" + id + ' tr:last').after(this);
  // });


}
