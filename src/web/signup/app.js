$(function(){

    $("#advSignUp").click(function (e) {
        e.preventDefault();
        var url = "/api/contributors"
        var getdata = JSON.stringify({name:$("#name").val(), email: $("#email").val(), nativeLanguage:$("#nativeLanguage").val(),
                phone:$("#phone").val(),gender:$("#gender").val(),
                password: $("#pwd").val()})
        console.log(getdata);
        console.log(url)

        jQuery.ajax ({
            url:  url,
            type: "POST",
            data: getdata,
            dataType: "json",
            contentType: "application/json; charset=utf-8"

         }).done(function(data){
            console.log(data)
         alert("Sign up successful. Please login.")
         location.href = ("/login");

         }).fail(function(data){
            $("#greeting").text("You might want to try it again");
        })
    });



    $("#leaSignUp").click(function (e) {
        e.preventDefault();
        var url = "/api/users"
        var getdata = JSON.stringify({username:$("#name").val(), email: $("#email").val(), nativeLanguage:$("#nativeLanguage").val(),
            phone:$("#phone").val(),gender:$("#gender").val(),password: $("#pwd").val(),englishLevel: $("#englishLevel").val(),
            birthday: $("#birthday").val()})
        console.log(getdata);
        console.log(url)
        jQuery.ajax ({
            url:  url,
            type: "POST",
            data: getdata,
            dataType: "json",
            contentType: "application/json; charset=utf-8"

        }).done(function(data){
            console.log(data)
            alert("Sign up successful. Please login.")
            location.href = ("/login");

        }).fail(function(data){
            $("#greeting").text("You might want to try it again");
        })
    });








})