$(function(){
    $("#btn").click(function (e) {
        var t = document.querySelector('input[name=question1]:checked').value;
        var l;
        if ($("#level").val().length > 0) l = parseInt($("#level").val()); else l = 0;
        jQuery.ajax ({
            url:  "../api/search/?type="+parseInt(t)+"&name="+$("#name").val()+"&level="+l,
            type: "GET",
            dataType: "json",
            contentType: "application/json; charset=utf-8"
        }).done(function(data){
            console.log(data)
            data.content.forEach(function(item){
                $( "#movieRow" ).clone().prop("id",item.id).appendTo( "#searchTable" );
                $("#"+item.id).find("#oName").text(item.name);
                $("#"+item.id).find("#oGenre").text(item.genre);
                $("#"+item.id).find("#oLevel").text(item.level);
                $("#"+item.id).prop("class","cloned");
                $("#"+item.id).show();
            });
        }).fail(function(data){
            $("#score").text("You might want to try it again");
        })
    });
})