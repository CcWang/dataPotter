$(function(){
    $("#logIn").click(function (e) {
        e.preventDefault();
        var url = ""
        var userType = ""
        if( $('#user').is(":checked") && !$("#contributor").is(":checked")){
            url = "/api/sessions";
            userType = "User";
        }else if ( !$('#user').is(":checked") && $("#contributor").is(":checked")){
            url = "/api/contributorSession";
            userType = "Contributor";
        }else{
            alert("Please choose one!")
        }

        console.log(url)

        jQuery.ajax ({
            url:  url,
            type: "POST",
            data: JSON.stringify({email:$("#email").val(), password: $("#pwd").val()}),
            dataType: "json",
            contentType: "application/json; charset=utf-8"
        }).done(function(data){
            console.log(data)
            if (userType == "User"){
                $("#greeting").text("User: "+data.content.username);
            }
            if (userType == "Contributor"){
                $("#greeting").text(data.content.name);
            }
            // $("#getcars").show();
            // $("#carTable").find(".cloned").remove();
            token = data.content.token;
            userId = data.content.userId;
        }).fail(function(data){
            $("#greeting").text("You might want to try it again");

        })
    });
})