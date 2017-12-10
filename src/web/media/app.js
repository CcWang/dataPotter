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
        console.log(finalvalue)
        $(".userSec").show();
        $(".conSec").hide();
        if (media.type == "movies" || media.type=="books") {
            checkFav(media.type, finalvalue.userId, media["name"],finalvalue.token);

        }else{
            checkFav('tv', finalvalue.userId, media["name"],finalvalue.token);
        }


    }
    if (userType == "contributor"){
        listvalues = localStorage.getItem("contributor");
        finalvalue = JSON.parse(listvalues)
        $(".userSec").hide();
        $(".conSec").show();
        if (media.type == "movies" || media.type=="books"){
            getMediaLevel(media.type, finalvalue.contributorId,media["name"]);
        }else if( media.type == "tv"){
            getMediaLevel("tvshows", finalvalue.contributorId,media["name"]);
        }

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
    if (media.type == "movies"){
        getMedia(media.type, media["name"]);
    }else if( media.type == "tv"){
        getMedia(media.type, media["name"]);
    } else{
        getBook(media["name"])
    }




})

function getMedia(type,name) {

    jQuery.ajax ({
        url: "../api/themoviedb/" +type+"/"+name,
        type: "GET",
        data: null,
        dataType: "json",
        contentType: "application/json; charset=utf-8"
    }).done(function(data){
        var data = JSON.parse(data.content)
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
        if (type == 'tv'){
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
        console.log("fail")
        $("#greeting").text("You might want to try it again");
    })
}

function getMediaLevel(type, id,name) {
    var u ="../api/"+type+"/levels/"+id+"/"+name;
    console.log(u)
    jQuery.ajax({
        url:"../api/"+type+"/levels/"+id+"/"+name,
        type: "GET",
        data: null,
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
        data: null,
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

function checkFav(type, id, name,token) {
    console.log(type,id,name)
    console.log("../api/favoriteLists/check/"+type+"/"+id+"/"+name)
    //check/{type}/{userId}/{name}
    jQuery.ajax({
        url:"../api/favoriteLists/check/"+type+"/"+id+"/"+name,
        type:"GET",
        data: null,
        dataType: "json",
        contentType: "application/json; charset=utf-8"
    }).done(function(data){
        data = data.content;
        console.log(data)

        if (data){
            $('#inFav').show();
            $('#notInFav').hide();
        }else{
            $('#inFav').hide();
            $('#notInFav').show();

        }
        $('.userSec').on('click',"#inFav", function () {

        //    if data.fav, true, remove from favlist
            if(data){

                jQuery.ajax({
                    url:"../api/favoriteLists/"+data.favID,
                    type:"DELETE",
                    data: null,
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    beforeSend:function (xhr) {
                        xhr.setRequestHeader("Authorization", token);

                    }
                })
                    .done(function (data) {
                        alert("You have remove "+name+" from favorites list.");
                        checkFav(type, id, name)

                    })
                    .fail(function (data) {
                        alert("Try again later");
                    })
            }




        })

        if(data == null){
            $('.userSec').on('click',"#notInFav", function () {


                    //    if data.fav, false, add to favlist
                jQuery.ajax({
                    url:"../api/favoriteLists/",
                    type:"POST",
                    data: JSON.stringify({type:type, media: name, userId:id}),
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    beforeSend:function (xhr) {
                        xhr.setRequestHeader("Authorization", token);

                    }

                }).done(function(data){
                    alert("Your have added "+name+" in your favorite list.");
                    checkFav(type, id, name)

                }).fail(function(data){
                    alert("please try again");
                })
            })

        }


    })
}
