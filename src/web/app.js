$(function() {
    var contributorId = "59f0527403bd32510df994f5";
    var offset = 20;
    var count = 30;
    var total = -1;

$(function() {
    $("#bookRow").hide();
    /*
    $("#getbooks").click(function (e) {
        e.preventDefault();
        jQuery.ajax ({
            url: "/api/books/",
            type: "GET"
        })
            .done(function(data){
                // console.log(data.content);
                data.content.forEach(function(item){
                    // console.log(item)
                    // console.log(item.contributorId)
                    $( "#bookRow" ).clone().prop("id",item.id).appendTo( "#bookTable" );
                    $("#"+item.id).find("#id").text(item.id);
                    $("#"+item.id).find("#name").text(item.name);
                    $("#"+item.id).find("#genre").text(item.genre);
                    $("#"+item.id).find("#level").text(item.level);
                    $("#"+item.id).find("#contributorId").text(item.contributorId);
                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
            })
            .fail(function(data){
                $("#bookTable").text("Sorry no books");
            })

    });
    */

    $("#getbooks").click(function (e) {
        e.preventDefault();
        loadBooks();
    });

    $("#next").click(function(e){
        e.preventDefault();
        if (offset+count < total) {
            offset = offset+count;
            loadBooks();
        }
    })

    $("#previous").click(function(e){
        e.preventDefault();
        console.log("Cliked")
        if (offset-count >= 0) {
            offset = offset-count;
            loadBooks();

        }
    })


    function loadBooks() {
        jQuery.ajax ({
            url: "/api/contributors/" + contributorId + "/books?offset=" + offset + "&count=" + count + "&sort=name",
            type: "GET",
        })
            .done(function(data){
                console.log(data);
                total = data.metadata.total;
                $("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
                $("#bookTable").find(".cloned").remove();
                data.content.forEach(function(item){
                    $( "#bookRow" ).clone().prop("id",item.id).appendTo( "#bookTable" );
                    $("#"+item.id).find("#id").text(item.id);
                    $("#"+item.id).find("#name").text(item.name);
                    $("#"+item.id).find("#genre").text(item.genre);
                    $("#"+item.id).find("#level").text(item.level);
                    $("#"+item.id).find("#contributorId").text(item.contributorId);
                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
            })
            .fail(function(data){
                $("#booklist").text("Sorry no books");
            })

    }
})})