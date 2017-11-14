$(function(){

    $("#signUp").click(function (e) {
        e.preventDefault();
        var url = ""
        var userType = ""
        if( $('#user').is(":checked") && !$("#contributor").is(":checked")){
            url = "/api/users";
            userType = "User";
        }else if ( !$('#user').is(":checked") && $("#contributor").is(":checked")){
            url = "/api/contributors";
            userType = "Contributor";
        }else{
            alert("Please choose one!")
        }

        console.log(url)

        jQuery.ajax ({
            url:  url,
            type: "POST",
            //先做一个user能用的，就是紫色的地方用username
            data: JSON.stringify({name:$("#name").val(), email: $("#email").val(), nativeLanguage:$("#nativeLanguage").val(),
                                  phone:$("#phone").val(),gender:$("#gender").val(),
                                  password: $("#pwd").val()}),
            // englishLevel:$("#englishLevel").val(),
            // birthday:$("#birthday").val(),

            //     item.getString("name"),
            // item.getString("email"),
            // item.getString("password"),
            // item.getString("nativeLanguage"),
            // item.getString("phone"),
            // item.getString("gender")

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
                location.href=("/users");

                // window.location.replace("http://localhost:8080/users/");
            }
            if (userType == "Contributor"){
                $("#greeting").text(data.content.name);
                localStorage.setItem('contributor', JSON.stringify(data.content));
                location.href = ("/contributors");
                // window.location.replace("http://localhost:8080/contributors/");
            }

        }).fail(function(data){
            $("#greeting").text("You might want to try it again");

        })
    });
})