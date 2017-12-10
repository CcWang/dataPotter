$(function(){
    $('#loading').hide();
    $("#logIn").click(function (e) {
        e.preventDefault();
        $('#loading').show();
        $("#greeting").text("Loading.....");
        $("form").hide();
        var url = ""
        var userType = ""
        if( $('#user').is(":checked") && !$("#contributor").is(":checked")){
            url = "/api/sessions";
            userType = "User";
        }else if ( !$('#user').is(":checked") && $("#contributor").is(":checked")){
            url = "/api/contributorSession";
            userType = "Contributor";
        }else{
            alert("Please choose one!");
            // $('#loading').hide();
            location.href=("/login");

        }



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
                // localStorage.clear();
                localStorage.setItem('user', JSON.stringify(data.content));
                localStorage.setItem('userType', "user");
                location.href=("/users");

                // window.location.replace("http://localhost:8080/users/");
            }
            if (userType == "Contributor"){
                $("#greeting").text(data.content.name);
                // localStorage.clear();
                localStorage.setItem('contributor', JSON.stringify(data.content));
                // localStorage.setItem('userType', JSON.stringify({"usertype":"contributor"}));
                localStorage.setItem('userType', "contributor");
                location.href = ("/contributors");
                // window.location.replace("http://localhost:8080/contributors/");
            }

        }).fail(function(data){
            alert("Sorry, we cannot find you based on your email address and password combination. You might want to try it again");
            location.href=("/login");

        })
    });
    $("#signUpPage").click(function (e) {
        console.log("click signup")
        e.preventDefault();
        location.href=("/signup");


    })
})