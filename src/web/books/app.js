var offset = 0;
var count = 20;
var total = -1;

$(document).ready(function () {

    getBooks();


    function getBooks() {


        jQuery.ajax ({
            // url:  "/api/contributors/" + finalvalue.contributorId + "/books?offset=" + offset + "&count="  + count,
            url:"/api/books",
            type: "GET",
        })
            .done(function(data){
                $("#bookTable").find(".cloned").remove();
                data.content.forEach(function(item){
                    $( "#bookRow" ).clone().prop("id",item.id).appendTo( "#bookTable" );
                    $("#"+item.id).find("#bName").text(item.name);
                    $("#"+item.id).find("#bgenre").text(item.genre);
                    $("#"+item.id).find("#blevel").text(item.level);
                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
            })
            .fail(function(data){
                $("#bookList").text("Sorry no tvs");
            })

    }

});
