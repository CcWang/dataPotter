$( document ).ready(function() {
    console.log( "go!" );
    var contributorId;
    var contributor;
    var offset = 0;
    var count = 20;
    var total = -1;
    $("#load_button").click(function(e){
        e.preventDefault();
        $("#loading").show();
        $("#load_button").hide();
        // $("#p").hide();
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

    $("#contributor").click(function(e){
       e.preventDefault();

        $.ajax({
            url:"/api/contributors",
            type:"GET"
        }).done(function (data) {
            var i=Math.floor(Math.random()*data.content.length);
            console.log(i)
            contributor=data.content[i].name;
            contributorId = data.content[i].id;
            $("#cname").text("Hello "+contributor+"! Here are all the movies you have added.");


        })
    });

    $("#getMovies").click(function(e){
        e.preventDefault();
        // console.log( contributorId, contributor);
        getMovies();

    })
    $("#next").click(function(e){
        e.preventDefault();
        console.log(offset,count)
        if (offset+count < total) {
            offset = offset+count;
            console.log(offset,count)
            getMovies();
        }
    })

    $("#previous").click(function(e){
        e.preventDefault();
        console.log("Cliked")
        if (offset-count >= 0) {
            offset = offset-count;
            getMovies();

        }
    })
    $("#sortG").click(function(e){
        e.preventDefault();
        getMovies("genre");
    })

    $("#sortN").click(function(e){
        e.preventDefault();
        getMovies("name");
    })

    var getMovies = function (sort_term) {
        //?sort=genre&count=30&offset=20
        var getUrl;
        if (sort_term){
            getUrl = "/api/contributors/"+ contributorId+"/movies?sort="+sort_term+"&offset="+offset + "&count=" +count;
        }else{
            getUrl = "/api/contributors/"+ contributorId+"/movies?offset="+offset+"&count=" +count;
        }
        $.ajax({
            url:getUrl,
            type:"GET"
        }).done(function (data) {
            console.log(data.content);

            total = data.metadata.total;
            $("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
            $("#movieTable").find(".cloned").remove();
            data.content.forEach(function(item){
                $( "#movieRow" ).clone().prop("id",item.id).appendTo( "#movieTable" );
                $("#"+item.id).find("#id").text(item.id);
                $("#"+item.id).find("#name").text(item.name);
                $("#"+item.id).find("#genre").text(item.genre);
                $("#"+item.id).find("#level").text(item.level);
                $("#"+item.id).find("#contributorId").text(item.contributorId);
                $("#"+item.id).prop("class","cloned");
                // $("#"+item.id).show();
            });


        })

    }





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
