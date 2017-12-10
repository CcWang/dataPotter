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
        // console.log(listvalues)
        $(".userSec").show();
        $(".conSec").hide();
    }
    if (userType == "contributor"){
        listvalues = localStorage.getItem("contributor");
        finalvalue = JSON.parse(listvalues)
        console.log(finalvalue)
        $(".userSec").hide();
        $(".conSec").show();
        getMediaLevel(media.type, finalvalue.contributorId,media["name"]);
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
    $("#user").text(finalvalue.username);
    getMedia(media.type, media["name"]);


})

function getMedia(type,name) {
    jQuery.ajax ({
        url: "../api/themoviedb/" +type+"/"+name,
        type: "GET",
        data: JSON.stringify({}),
        dataType: "json",
        contentType: "application/json; charset=utf-8"
    }).done(function(data){
        var data = JSON.parse(data.content)
        console.log(data)
        var imageUrl = "https://image.tmdb.org/t/p/w300/"+data.poster_path;
        var overview = "<p>"+data.overview+"</p>"
        $("#img").attr('src', imageUrl);
        $("#overview").html(overview);
        $('#name').text(name);
        $('#rate').html("Rate: "+ data.vote_average);
        $('#year').html("Year:" + data.release_date);
        data.genres.forEach(function (item) {
            var genre = "<p style='display:inline-block'>"+item.name+"</p> | ";
            $('#genre').append(genre)
        })

        data.spoken_languages.forEach(function (item) {
            var lang = "<p style='display:inline-block'>"+item.name+"</p> | ";
            $('#lang').append(lang)
        })

    }).fail(function(data){
        $("#greeting").text("You might want to try it again");
    })
}

function getMediaLevel(type, id,name) {
    jQuery.ajax({
        url:"../api/"+type+"/levels/"+id+"/"+name,
        type: "GET",
        data: JSON.stringify({}),
        dataType: "json",
        contentType: "application/json; charset=utf-8"
    }).done(function(data){
        data = data.content;
        console.log("getMediaLevel",data)
        $("#numCon").text(data.size);
        $("#avgLev").text(data.avgLev+".");
        $("#yourLev").text(data.indLev +'.');
    })

}