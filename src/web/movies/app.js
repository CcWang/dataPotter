var offset = 0;
var count = 20;
var total = -1;
$(document).ready(function () {
    console.log( "go!" );

    getMoview();

    function getMoview() {
        //console.log("hit get movie",finalvalue)

        jQuery.ajax ({
            // url:  "/api/contributors/" + finalvalue.contributorId + "/movies?offset=" + offset + "&count="  + count,
            url: "/api/movies/",
            type: "GET"
        })
            .done(function(data){
                //total = data.metadata.total;
                //console.log(total);
                //$("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
                $("#movieTable").find(".cloned").remove();
                data.content.forEach(function(item){
                    $( "#movieRow" ).clone().prop("id",item.id).appendTo( "#movieTable" );
                    $("#"+item.id).find("#mName").text(item.name);
                    $("#"+item.id).find("#genre").text(item.genre);
                    $("#"+item.id).find("#level").text(item.level);
                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
            })
            .fail(function(data){
                $("#movieList").text("Sorry no cars");
            })

    }

});
