var offset = 0;
var count = 20;
var total = 300;
$(document).ready(function () {
    console.log( "go!" );

    getMoview();

    $("#next").click(function(e){
        e.preventDefault();
        console.log(offset,count)
        if (offset+count < total) {
            offset = offset+count;
            console.log(offset,count)
            getMoview();
        }
    })

    $("#previous").click(function(e){
        e.preventDefault();
        console.log("Cliked")
        if (offset-count >= 0) {
            offset = offset-count;
            getMoview();

        }
    });
    $("#sortG").click(function(e){
        e.preventDefault();
        getMoview("genre");
    })

    $("#sortN").click(function(e){
        e.preventDefault();
        getMoview("name");
    })

    function getMoview(sort_term) {
        //console.log("hit get movie",finalvalue)

        jQuery.ajax ({
            // url:  "/api/contributors/" + finalvalue.contributorId + "/movies?offset=" + offset + "&count="  + count,
            url: "/api/movies?sort="+sort_term+"&offset="+offset + "&count=" +count,
            type: "GET"
        })
            .done(function(data){

                $("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
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
    $(document).on('click',"#mName", function () {
        console.log("clicked")
        var name = $(this).text();

        localStorage.setItem('media', JSON.stringify({"type":"movies", "name":name}));
        location.href=("/media")
    })

});
