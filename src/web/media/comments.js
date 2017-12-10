$(document).ready(function () {

    $(".userSec").hide();
    $(".conSec").hide();
    var media = localStorage.getItem('media');

    if (media == null) {
        alert("please log in");
        location.href = ("/login");
    }
    var userType = localStorage.getItem('userType');
    var listvalues;
    var finalvalue;
    media = JSON.parse(media)
    if (userType == "user"){
        listvalues = localStorage.getItem('user');
        finalvalue = JSON.parse(listvalues);
        // console.log(finalvalue)
        $(".userSec").show();
        $(".conSec").hide();
        submitComment(media.type,media.name,finalvalue.userId)

    }
    if (userType == "contributor"){
        listvalues = localStorage.getItem("contributor");
        finalvalue = JSON.parse(listvalues)
        // console.log(finalvalue)
        $(".userSec").hide();
        $(".conSec").show();

    }
    finalvalue = JSON.parse(listvalues);

    var token = finalvalue.token;
    if (listvalues == null) {
        alert("please log in");
        location.href = ("/login");
    }
    if (token == null) {
        alert("please log in");
        location.href = ("/login");
    }





})

function getComment(userId) {
    console.log(userId)
    jQuery.ajax({
        url:"../api" + finalvalue.mediaType + finalvalue.userId + "/name",
        data: null,
        dataType: "json",
        contentType: "application/json; charset=utf-8"
    }).done(function(data){
        data = data.content;
        console.log(data)
        $('#userId').text(data.userID);
        $('#comment').text(data.content);

    })
}

function submitComment(type,name,userId){
    $("#submit").click(function (e) {
        e.preventDefault();
        console.log("submit")
        var url = "../api/comment";
        var getdata = JSON.stringify({userId:userId, content: $("#comment").val(),
                                      mediaType:type, mediaName:name,})

        console.log(getdata);
        console.log(url)

        jQuery.ajax ({
            url:  url,
            type: "POST",
            data: getdata,
            dataType: "json",
            contentType: "application/json; charset=utf-8"

        }).done(function(data){
            console.log(data)
            alert("Submitted successfully")
            $("#comment").val("")


        }).fail(function(data){
            $("#greeting").text("You might want to try it again");
        })
    });
}