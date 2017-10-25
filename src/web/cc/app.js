$( document ).ready(function() {
    console.log( "go!" );
    $("#load_button").click(function(e){
        e.preventDefault();
        $("#loading").show();
        $("#load_button").hide();
        $.ajax({
            url:"/api/users",
            type:"GET"
        }).done(function(data){
            console.log(data.content);
            data.content.forEach(function (user) {
                var gender = user.gender.charAt(0).toUpperCase() + user.gender.slice(1);
                var dob = new Intl.DateTimeFormat().format(user.birthday);
                var userID = user.id;
                var html = "<tr><td>"+user.username+"</td><td>"+user.email+"</td><td>"+user.nativeLanguage+"</td><td>"
                +user.englishLevel+"</td><td>"+user.phone+"</td><td>"+gender+"</td><td>"+dob+"</td><td><button type='button' id="+userID+" class='btn btn-primary btn-sm'>see</button></td></td>";
                // console.log(html);
                $('#user_list tr:last').after(html);
                // $("tbody").appendTo(html);
                $("#loading").hide();
                })
            $("#user_list").show();
            $(".btn-sm").click(function (e) {
                e.preventDefault();
                // console.log(this.id);
                getUserLan(this.id);

            })

            }).fail(function () {
                alert("Sorry No Users Yet");

        })

    });

    $("#movies").click(function(e){
       e.preventDefault();
       console.log("movies button click")
        $.ajax({
            // url:"https://api.themoviedb.org/3/movie/550?api_key=664f8054c78de425d08aba35e84e6a11",
            url:"/api/movies",
            type:"GET"
        }).done(function (data) {
            console.log(data)
        })
    });


});

var getUserLan = function (userId) {
    $.ajax({
        url: "/api/users/" + userId + "/langs",
        type: "GET"
    })
        .done(function (data) {
            // console.log(data.content[0])
            data = data.content[0];
            var msg = "Hello, here are your level scores - movies: " + data.movies_level + " TV shows: " + data.tvshows_level
                + " Books: " + data.books_level + " Audio Books: " + data.audioBooks_level + "."
            alert(msg);

        });
};