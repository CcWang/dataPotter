var offset = 0;
var count = 10;
var total = 100;

$(document).ready(function () {

    getBooks();

    $("#next").click(function(e){
        e.preventDefault();
        console.log(offset,count)
        if (offset+count < total) {
            offset = offset+count;
            console.log(offset,count)
            getBooks("name");
        }
    })

    $("#previous").click(function(e){
        e.preventDefault();
        console.log("Cliked")
        if (offset-count >= 0) {
            offset = offset-count;
            getBooks("name");

        }
    });
    $("#sortG").click(function(e){
        e.preventDefault();
        getBooks("genre");
    })

    $("#sortN").click(function(e){
        e.preventDefault();
        getBooks("name");
    })

    function getBooks(sort_term) {
        jQuery.ajax ({
            // url:  "/api/contributors/" + finalvalue.contributorId + "/books?offset=" + offset + "&count="  + count,
            url:"/api/books?sort="+sort_term+"&offset="+offset + "&count=" +count,
            type: "GET",
        })
            .done(function(data){

                // console.log("here"+data.content);
                $("#bookTable").find(".cloned").remove();
                $("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
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
                console.log("here");
                $("#bookList").text("Sorry no books");
            })

    }

    $(document).on('click',"#bName", function () {
        var name = $(this).text();


        localStorage.setItem('media', JSON.stringify({"type":"books", "name":name}));
        location.href=("/media");
        //
    })
});
