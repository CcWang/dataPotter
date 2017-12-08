$(document).ready(function () {
    var listvalues = localStorage.getItem('user');
    var media = localStorage.getItem('media');
    media = JSON.parse(media)
    console.log(typeof(media))
    console.log(media.type)
    console.log(media.name)
    if (listvalues == null) {
        alert("please log in");
        location.href = ("/login");
    }

    var finalvalue = JSON.parse(listvalues);
    var token = finalvalue.token;
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
        $('#rate').html("Rate: "+ data.vote_average)
    }).fail(function(data){
        $("#greeting").text("You might want to try it again");
    })
}