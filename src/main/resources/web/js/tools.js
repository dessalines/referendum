// the digital ocean service
var localSparkService = "http://127.0.0.1:4567/"







var sparkService = localSparkService;

function getJson(shortUrl) {
  var url = sparkService + shortUrl; // the script where you handle the form input.
  return $.ajax({
    type: "GET",
    url: url,
    xhrFields: {
      withCredentials: true
    },
    // data: seriesData, 
    success: function(data, status, xhr) {
      // console.log(data);
      // var jsonObj = JSON.parse(data);
      // JSON.useDateParser();
      // var jsonObj = jQuery.parseJSON(data);
      // JSON.useDateParser();
      // var jsonObj = JSON.parse(data);
      // $('[data-spy="scroll"]').each(function() {
      //     $(this).scrollspy('refresh');
      // });
    },
    error: function(request, status, error) {

      toastr.error(request.responseText);
    }
  });
}

function postJson(shortUrl) {
  var url = sparkService + shortUrl // the script where you handle the form input.
  return $.ajax({
    type: "POST",
    url: url,
    xhrFields: {
      withCredentials: true
    },
    // data: seriesData, 
    success: function(data, status, xhr) {
      // console.log(data);
      // var jsonObj = JSON.parse(data);
      // JSON.useDateParser();
      // var jsonObj = jQuery.parseJSON(data);
      // JSON.useDateParser();
      // var jsonObj = JSON.parse(data);
      // $('[data-spy="scroll"]').each(function() {
      //     $(this).scrollspy('refresh');
      // });
    },
    error: function(request, status, error) {

      toastr.error(request.responseText);
    }
  });
}

function scrollSpy() {
  $('body').scrollspy({
    target: '.bs-docs-sidebar',
    offset: 70
  });
  // $('#main').scrollspy('refresh');
  //    $('[data-spy="scroll"]').each(function () {
  //   var $spy = $(this).scrollspy('refresh')
  // });
}

function fillMustacheWithJson(data, templateHtml, divId) {

  // $.extend(data, standardDateFormatObj);

  $.extend(data, m2htmlObj);

  Mustache.parse(templateHtml); // optional, speeds up future uses
  var rendered = Mustache.render(templateHtml, data);
  $(divId).html(rendered);

  // console.log(rendered);

}

function fillMustacheFromJson(url, templateHtml, divId) {
  //         $.tablesorter.addParser({ 
  //           id: 'my_date_column', 
  //           is: function(s) { 
  //       // return false so this parser is not auto detected 
  //       return false; 
  //     }, 
  //     format: function(s) { 
  //       console.log(s);
  //       var timeInMillis = new Date.parse(s);

  //       // var date = new Date(parseInt(s));
  //       return date;         
  //     }, 
  //   // set type, either numeric or text 
  //   type: 'numeric' 
  // });

  var url = sparkService + url // the script where you handle the form input.
  return $.ajax({
    type: "POST",
    url: url,
    xhrFields: {
      withCredentials: true
    },
    // data: seriesData, 
    success: function(data, status, xhr) {


      // var jsonObj = JSON.parse(data);
      // JSON.useDateParser();
      var jsonObj = jQuery.parseJSON(data);
      // JSON.useDateParser();
      // var jsonObj = JSON.parseWithDate(data);

      $.extend(jsonObj, standardDateFormatObj);
      Mustache.parse(templateHtml); // optional, speeds up future uses
      var rendered = Mustache.render(templateHtml, jsonObj);
      $(divId).html(rendered);

      // console.log(data);
      // console.log(jsonObj);
      // console.log(templateHtml);
      // console.log(rendered);


    },
    error: function(request, status, error) {

      toastr.error(request.responseText);
    }
  });

}

function loggedIn() {
  var sessionId = getCookie("auth");

  console.log('session id = ' + sessionId);

  var ret = !(sessionId === undefined) ? true : false;

  return ret;

}

function logOut() {
  delete_cookie('auth');
  delete_cookie('uid');
}


function getCookies() {
  var c = document.cookie,
    v = 0,
    cookies = {};
  if (document.cookie.match(/^\s*\$Version=(?:"1"|1);\s*(.*)/)) {
    c = RegExp.$1;
    v = 1;
  }
  if (v === 0) {
    c.split(/[,;]/).map(function(cookie) {
      var parts = cookie.split(/=/, 2),
        name = decodeURIComponent(parts[0].trimLeft()),
        value = parts.length > 1 ? decodeURIComponent(parts[1].trimRight()) : null;
      cookies[name] = value;
    });
  } else {
    c.match(/(?:^|\s+)([!#$%&'*+\-.0-9A-Z^`a-z|~]+)=([!#$%&'*+\-.0-9A-Z^`a-z|~]*|"(?:[\x20-\x7E\x80\xFF]|\\[\x00-\x7F])*")(?=\s*[,;]|$)/g).map(function($0, $1) {
      var name = $0,
        value = $1.charAt(0) === '"' ? $1.substr(1, -1).replace(/\\(.)/g, "$1") : $1;
      cookies[name] = value;
    });
  }
  return cookies;
}

function getCookie(name) {
  var cookie = getCookies()[name];
  if (cookie != null) {
    return cookie.replace(/"/g, "");
  } else {
    return cookie;
  }

}

function createCookie(name, value, days) {
  if (days) {
    var date = new Date();
    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
    var expires = "; expires=" + date.toGMTString();
  } else var expires = "";
  document.cookie = name + "=" + value + expires + "; path=/";
}

function delete_cookie(name) {
  document.cookie = name + '=; path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT;';

}

function getUrlPathArray() {
  return window.location.pathname.split('/');
}

function getLastUrlPath() {
  return getUrlPathArray().slice(-1)[0];

}

function standardFormPost(shortUrl, formId, modalId, reload, successFunctions, noToast, clearForm) {
  // !!!!!!They must have names unfortunately
  // An optional arg
  modalId = (typeof modalId === "undefined") ? "defaultValue" : modalId;

  reload = (typeof reload === "undefined") ? false : reload;

  noToast = (typeof noToast === "undefined") ? false : noToast;

  clearForm = (typeof clearForm === "undefined") ? false : clearForm;



  // serializes the form's elements.
  var formData = $(formId).serializeArray();
  // console.log(formData);

  var btn = $("[type=submit]", formId);

  // Loading
  btn.button('loading');

  var url = sparkService + shortUrl; // the script where you handle the form input.
  // console.log(url);
  $.ajax({
    type: "POST",
    url: url,
    xhrFields: {
      withCredentials: true
    },
    data: formData,
    success: function(data, status, xhr) {

      // console.log('posted the data');
      xhr.getResponseHeader('Set-Cookie');
      // document.cookie="authenticated_session_id=" + data + 
      // "; expires=" + expireTimeString(60*60); // 1 hour (field is in seconds)
      // Hide the modal, reset the form, show successful

      // $(formId)[0].reset();
      $(modalId).modal('hide');
      // console.log(modalId);
      if (!noToast) {
        toastr.success(data);
      }
      if (successFunctions != null) {
        successFunctions(data);
      }
      if (reload) {
        // refresh the page, too much info has now changed
        window.setTimeout(function() {
          location.reload();
        }, 3000);
      }

      btn.button('reset');
      if (clearForm) {
        $(formId)[0].reset();
      }
      // console.log(document.cookie);
      return data;

    },
    error: function(request, status, error) {
      if (request.responseText != null) {
        toastr.error(request.responseText);
      } else {
        toastr.error("Couldn't find endpoint " + url);

      }
      btn.button('reset');
    }
  });

  // if (event != null) {
  // event.preventDefault();
  // }
  return false;



  // event.preventDefault();
}

function simplePost(shortUrl, postData, reload, successFunctions, noToast, external, btnId) {


  // !!!!!!They must have names unfortunately
  // An optional arg
  modalId = (typeof modalId === "undefined") ? "defaultValue" : modalId;

  reload = (typeof reload === "undefined") ? false : reload;

  noToast = (typeof noToast === "undefined") ? false : noToast;
  external = (typeof external === "undefined") ? false : external;

  btnId = (typeof btnId === "undefined") ? false : btnId;

  var url;
  if (external) {
    url = externalSparkService + shortUrl;
  } else {
    url = sparkService + shortUrl;
  }

  // var btn = $("[type=submit]");
  // var btn = $(this).closest(".btn");
  var btn = $(btnId);

  // Loading
  btn.button('loading');

  // console.log(url);
  $.ajax({
    type: "POST",
    url: url,
    xhrFields: {
      withCredentials: true
    },
    data: postData,
    success: function(data, status, xhr) {

      // console.log('posted the data');
      xhr.getResponseHeader('Set-Cookie');
      // document.cookie="authenticated_session_id=" + data + 
      // "; expires=" + expireTimeString(60*60); // 1 hour (field is in seconds)
      // Hide the modal, reset the form, show successful

      // $(formId)[0].reset();

      // console.log(modalId);
      if (!noToast) {
        toastr.success(data);
      }
      if (successFunctions != null) {
        successFunctions(data);
      }
      btn.button('reset');
      if (reload) {
        // refresh the page, too much info has now changed
        window.setTimeout(function() {
          location.reload();
        }, 3000);
      }



      // console.log(document.cookie);
      return data;

    },
    error: function(request, status, error) {
      if (!noToast) {
        toastr.error(request.responseText);
      }
      btn.button('reset');
    }
  });


  return false;



  // event.preventDefault();


}


var standardDateFormatObj = {
  "dateformat": function() {
    return function(text, render) {
      var t = render(text);
      var date = new Date(parseInt(t) * 1000);
      // console.log(t);
      // return date.customFormat("#YYYY#/#MM#/#DD# #hh#:#mm# #AMPM#")
      return date.customFormat("#YYYY#/#MM#/#DD#")
    }
  }
};

var m2htmlObj = {
  "m2html": function() {
    return function(text, render) {
      var t = render(text);
      return markdown.toHTML(t);
    }
  }
}


Date.prototype.customFormat = function(formatString) {
  var YYYY, YY, MMMM, MMM, MM, M, DDDD, DDD, DD, D, hhh, hh, h, mm, m, ss, s, ampm, AMPM, dMod, th;
  var dateObject = this;
  YY = ((YYYY = dateObject.getFullYear()) + "").slice(-2);
  MM = (M = dateObject.getMonth() + 1) < 10 ? ('0' + M) : M;
  MMM = (MMMM = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"][M - 1]).substring(0, 3);
  DD = (D = dateObject.getDate()) < 10 ? ('0' + D) : D;
  DDD = (DDDD = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"][dateObject.getDay()]).substring(0, 3);
  th = (D >= 10 && D <= 20) ? 'th' : ((dMod = D % 10) == 1) ? 'st' : (dMod == 2) ? 'nd' : (dMod == 3) ? 'rd' : 'th';
  formatString = formatString.replace("#YYYY#", YYYY).replace("#YY#", YY).replace("#MMMM#", MMMM).replace("#MMM#", MMM).replace("#MM#", MM).replace("#M#", M).replace("#DDDD#", DDDD).replace("#DDD#", DDD).replace("#DD#", DD).replace("#D#", D).replace("#th#", th);

  h = (hhh = dateObject.getHours());
  if (h == 0) h = 24;
  if (h > 12) h -= 12;
  hh = h < 10 ? ('0' + h) : h;
  AMPM = (ampm = hhh < 12 ? 'am' : 'pm').toUpperCase();
  mm = (m = dateObject.getMinutes()) < 10 ? ('0' + m) : m;
  ss = (s = dateObject.getSeconds()) < 10 ? ('0' + s) : s;
  return formatString.replace("#hhh#", hhh).replace("#hh#", hh).replace("#h#", h).replace("#mm#", mm).replace("#m#", m).replace("#ss#", ss).replace("#s#", s).replace("#ampm#", ampm).replace("#AMPM#", AMPM);
}


// Auto resizing textareas

function updateTextAreaHeight() {
  $('textarea').each(function() {
    this.setAttribute('style', 'height:' + (this.scrollHeight) + 'px;overflow-y:hidden;');
  }).on('input', function() {
    this.style.height = 'auto';
    this.style.height = (this.scrollHeight) + 'px';
  });
}

updateTextAreaHeight();

$.fn.sort_select_box = function() {
  // Get options from select box
  var my_options = $("#" + this.attr('id') + ' option');
  var selected = $("#" + this.attr('id')).val();
  // sort alphabetically
  my_options.sort(function(a, b) {
      if (a.text > b.text) return 1;
      else if (a.text < b.text) return -1;
      else return 0
    })
    //replace with sorted my_options;
  $(this).empty().append(my_options);

  // clearing any selections
  $("#" + this.attr('id')).val(selected);
}

function setupToolTips() {
  $('[data-toggle="tooltip"]').tooltip({
    container: 'body'
  });
}

var delay = (function() {
  var timer = 0;
  return function(callback, ms) {
    clearTimeout(timer);
    timer = setTimeout(callback, ms);
  };
})();

function replaceNewlines(e) {
  return e.replace(/--lb--/g, "\\n");
}

function replaceNewlinesV2(e) {
  return e.replace(/--lb--/g, "<br>");
}
