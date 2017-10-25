$(function() {
    $("#bookRow").hide();

    $("#getbooks").click(function (e) {
        e.preventDefault();
        jQuery.ajax ({
            url: "/api/books/",
            type: "GET"
        })
            .done(function(data){
                console.log(data.content);
                data.content.forEach(function(item){
                    $( "#bookRow" ).clone().prop("id",item.id).appendTo( "#bookTable" );
                    $("#"+item.id).find("#id").text(item.id);
                    $("#"+item.id).find("#name").text(item.name);
                    $("#"+item.id).find("#genre").text(item.genre);
                    $("#"+item.id).find("#level").text(item.level);
                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
            })
            .fail(function(data){
                $("#bookTable").text("Sorry no books");
            })

    });
})