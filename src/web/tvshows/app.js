var offset = 0;
var count = 20;
var total = -1;

$(document).ready(function () {

    getTV();

    function getTV() {


        jQuery.ajax ({
            // url:  "/api/contributors/" + finalvalue.contributorId + "/tvshows?offset=" + offset + "&count="  + count,
            url:"/api/tvshows",
            type: "GET",
        })
            .done(function(data){
                $("#tvTable").find(".cloned").remove();
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

});
