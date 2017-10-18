$( document ).ready(function() {
    console.log( "ready" );
    $("#button").click(function(e){
        e.preventDefault();
        $("button").hide();
        $.ajax({
            url:"/api/movies",
            type:"GET"
        }).done(function(data){
            console.log(data.content);
            data.content.forEach(function (movie) {

                var html = "<tr><td>"+movie.name+"</td><td>"+movie.genre+"</td><td>"+movie.level+"</td></tr>";
                // console.log(html);
                $('#movie_list tr:last').after(html);

            })
            $("#movie_list").show();

        }).fail(function () {
            alert("Sorry No Users Yet");

        })

    });


});
