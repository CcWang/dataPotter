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
            // var d = new Date();
            // d.setTime(d.getTime() + (1*24*60*60*1000));
            // var expires = "expires="+ d.toUTCString();

            token = data.content.token;

            if (userType == "User"){
                $("#greeting").text("User: "+data.content.username);
                // document.cookie =  "name = "+data.content.username+"; token=" + token + ";"+ expires + ";path=/";
                localStorage.setItem('user', JSON.stringify(data.content));
                window.location.replace("http://localhost:8080/users/");
            }
            if (userType == "Contributor"){
                $("#greeting").text(data.content.name);
                localStorage.setItem('contributor', JSON.stringify(data.content));
                window.location.replace("http://localhost:8080/contributors/");
            }

        }).fail(function(data){
            $("#greeting").text("You might want to try it again");

        })
    });
})