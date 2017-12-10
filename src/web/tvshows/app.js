var offset = 0;
var count = 20;
var total = 300;

$(document).ready(function () {

    getTV();
    $("#next").click(function(e){
        e.preventDefault();
        console.log(offset,count)
        if (offset+count < total) {
            offset = offset+count;
            console.log(offset,count)
            getTV("name");
        }
    })

    $("#previous").click(function(e){
        e.preventDefault();
        console.log("Cliked")
        if (offset-count >= 0) {
            offset = offset-count;
            getTV("name");

        }
    });
    $("#sortG").click(function(e){
        e.preventDefault();
        getTV("genre");
    })

    $("#sortN").click(function(e){
        e.preventDefault();
        getTV("name");
    })

    function getTV(sort_term) {


        jQuery.ajax ({
            // url:  "/api/contributors/" + finalvalue.contributorId + "/tvshows?offset=" + offset + "&count="  + count,
            url:"/api/tvshows?sort="+sort_term+"&offset="+offset + "&count=" +count,
            type: "GET",
        })
            .done(function(data){
                $("#tvTable").find(".cloned").remove();
                $("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
                data.content.forEach(function(item){
                    $( "#tvRow" ).clone().prop("id",item.id).appendTo( "#tvTable" );
                    $("#"+item.id).find("#tvName").text(item.name);
                    $("#"+item.id).find("#tvgenre").text(item.genre);
                    $("#"+item.id).find("#tvlevel").text(item.level);
                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
            })
            .fail(function(data){
                $("#tvList").text("Sorry no tvs");
            })

    }
    $(document).on('click',"#tvName", function () {
        var name = $(this).text();


        localStorage.setItem('media', JSON.stringify({"type":"tv", "name":name}));
        location.href=("/media");
        //
    })

});
