$(function(){
    $('#loading').hide();
    $("#btn").click(function (e) {
        $('#loading').show();
        console.log("score");
        jQuery.ajax ({
            url:  "../api/score/?url="+$("#link").val(),
            type: "GET",
            dataType: "json",
            contentType: "application/json; charset=utf-8"
        }).done(function(data){
            $('#loading').hide();
            if (data.content[0]!=0) $("#total").text("Subtitle total word count: " + data.content[0]);
            if (data.content[1]!=0) $("#low").text("low frequency word count: " + data.content[1]);
            if (data.content[2]!=0) $("#high").text("high frequency word count: " + data.content[2]);
            if (data.content[3]!=0) $("#low_dup").text("low frequency word (excluding duplicates) count: " + data.content[3]);
            if (data.content[4]!=0) $("#high_dup").text("high frequency word (excluding duplicates) count: " + data.content[4]);
            if (data.content[0] == 0) $("#score").text ("Incorrect URL or Unsupported File");
            else if (data.content[5] == 1) $("#score").text("Recommended difficulty: Easy");
            else if (data.content[5] == 2) $("#score").text("Recommended difficulty: Normal");
            else if (data.content[5] == 3) $("#score").text("Recommended difficulty: Hard");

            console.log(data)
        }).fail(function(data){
            $("#score").text("You might want to try it again");
        })
    });
})