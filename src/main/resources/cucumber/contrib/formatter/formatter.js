$(document).ready(function() {

  //
  var toc = $("<ul></ul>");
  $("section.feature").each(function() {
    var feature = $(this);
    
    var subtoc = $("<ul></ul>");
    feature.find("section.scenario").each(function() {
        var scenario = $(this),
            h2  = scenario.find("h2"),
            shr = $("<a></a>").attr("href", "#" + h2.attr("id")).text(h2.text());
        
        var sli = $("<li></li>");
        sli.append(shr);
        subtoc.append(sli);
    });
    
    var h1 = feature.find("h1"),
        hr = $("<a></a>").attr("href", "#" + h1.attr("id")).text(h1.text()),
        li = $("<li></li>");
        
    li.append(hr);
    li.append(subtoc);
    toc.append(li);
  });
  
  $(".cucumber-report .header").append(toc);

  // pretty table
  $("table.data-table").addClass("table").addClass("table-striped").addClass("table-condensed");
  
  // uri
  $(".uri").addClass("muted");

  // Step status
  $("span.step-status").each(function(index) {
    var element = $(this);
    element//.addClass("label")
           .html("&nbsp;");
             
    if(element.hasClass("passed")) {
      element//.addClass("label-success")
             .append("<img src='images/ok-icon.png' alt='passed'>");
             //.append("<i class='icon-ok-sign icon-white' title='success'></li>");
    }
    else if(element.hasClass("undefined")) {
      element//.addClass("label-warning")
             .append("<img src='images/unknown-icon.png' alt='undefined'>");
             //.append("<i class='icon-question-sign icon-white' title='undefined'></li>");
    }
    else if(element.hasClass("pending")) {
      element//.addClass("label-warning")
             .append("<img src='images/unknown-icon.png' alt='pending'>");
             //.append("<i class='icon-question-sign icon-white' title='pending'></li>");
    }
    else if(element.hasClass("skipped")) {
      element//.addClass("label-warning")
             .append("<img src='images/skipped-icon.png' alt='skipped'>");
             //.append("<i class='icon-ban-circle icon-white' title='skipped'></li>");
    }
    else if(element.hasClass("failed")) {
      element//.addClass("label-important")
             .append("<img src='images/ko-icon.png' alt='failed'>");
             //.append("<i class='icon-remove-sign icon-white' title='failed'></li>");
    }
  });

});
