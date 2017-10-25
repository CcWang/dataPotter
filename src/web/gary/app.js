$( document ).ready(function() {
    console.log( "ready!" );
    var contributorId;
    var contributor;
    var offset = 0;
    var count = 20;
    var total = -1;

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
            $("#cname").text("Welcome "+contributor+"! Here are all the TVshows you have contributed.");


        })
    });

    $("#getTvshows").click(function(e){
        e.preventDefault();
        // console.log( contributorId, contributor);
        Tvshows();

    })
    $("#next").click(function(e){
        e.preventDefault();
        console.log(offset,count)
        if (offset+count < total) {
            offset = offset+count;
            console.log(offset,count)
            Tvshows();
        }
    })

    $("#previous").click(function(e){
        e.preventDefault();
        console.log("Cliked")
        if (offset-count >= 0) {
            offset = offset-count;
            Tvshows();

        }
    })
    $("#sortG").click(function(e){
        e.preventDefault();
        Tvshows("genre");
    })

    $("#sortN").click(function(e){
        e.preventDefault();
        Tvshows("name");
    })

    var Tvshows = function (sort_term) {
        var getUrl;
        if (sort_term){
            getUrl = "/api/contributors/"+ contributorId+"/tvshows?sort="+sort_term+"&offset="+offset + "&count=" +count;
        }else{
            getUrl = "/api/contributors/"+ contributorId+"/tvshows?offset="+offset+"&count=" +count;
        }
        $.ajax({
            url:getUrl,
            type:"GET"
        }).done(function (data) {
            console.log(data.content);

            total = data.metadata.total;
            $("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
            $("#tvshowsTable").find(".cloned").remove();
            data.content.forEach(function(item){
                $( "#tvshowsRow" ).clone().prop("id",item.id).appendTo( "#tvshowsTable" );
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
