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
    if (media.type == "movies" || media.type == "tvshows"){
        getMedia(media.type, media["name"]);
    }else{
        getBook(media["name"])
    }




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
        if(type == 'movies'){
            $('#year').html("Year:" + data.release_date);
            if(data.spoken_languages.length>1){
                data.spoken_languages.forEach(function (item) {
                    var lang = "<p style='display:inline-block'>"+item.name+"</p> | ";
                    $('#lang').append(lang)
                })
            }else{
                var lang = "<p style='display:inline-block'>"+data.spoken_languages.name+"</p> | ";
                $('#lang').append(lang)
            }
        }
        if (type == 'tvshows'){
            if(data.languages.length>1){
                data.languages.forEach(function (item) {
                    var lang = "<p style='display:inline-block'>"+item+"</p> | ";
                    $('#lang').append(lang)
                })
            }else{
                var lang = "<p style='display:inline-block'>"+data.languages[0]+"</p> | ";
                $('#lang').append(lang)
            }

            if (data.seasons.length>1){
                data.seasons.forEach(function (item) {
                    var season = "<p style='display:inline-block'>"+item.air_date+"</p> | ";
                    $('#season').append(season)
                })
            }else{
                var season = "<p style='display:inline-block'>"+data.seasons.air_date+"</p> | ";
                $('#season').append(season)
            }

        }
       if(data.genres.length>1){
           data.genres.forEach(function (item) {
               var genre = "<p style='display:inline-block'>"+item.name+"</p> | ";
               $('#genre').append(genre)
           })
       }else{
           var genre = "<p style='display:inline-block'>"+data.genres[0].name+"</p> | ";
           $('#genre').append(genre)
       }




    }).fail(function(data){
        $("#greeting").text("You might want to try it again");
    })
}

function getMediaLevel(type, id,name) {
    console.log(type);
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

function getBook(name) {
    jQuery.ajax({
        url:"../api/books/bookOne/"+name,
        type: "GET",
        data: JSON.stringify({}),
        dataType: "json",
        contentType: "application/json; charset=utf-8"
    }).done(function(data){
        data = data.content;
        console.log(data)
        $('#name').text(data.name);
        $('#genre').text(data.genre);
        $('#lang').append("<p> English </p>");

    })
}