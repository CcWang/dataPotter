$(function() {
    var offset = 20;
    var count = 30;
    var total = -1;
    $("#editForm").hide();

    var listvalues = localStorage.getItem('user');
    if (listvalues == null) {
        alert("please log in");
        location.href = ("/login");
    }

    var finalvalue = JSON.parse(listvalues);
    var token = finalvalue.token;
    if (token == null) {
        alert("please log in");
        location.href = ("/login");
    }


    var url = "/api/users/" + finalvalue.userId;
    console.log(finalvalue)
    jQuery.ajax({
        url: url,
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", token);
        }
    }).done(function (data) {
        $(".name").text(data.content.username);
        $("#email").text(data.content.email);
        $("#changeEmail").val(data.content.email);
        $("#changePwd").val(data.content.password);
        $("#lan").text(data.content.nativeLanguage);
        $("#changeLan").val(data.content.nativeLanguage);
        $("#changeCell").val(data.content.phone);
        $("#cell").text(data.content.phone);
        $("#birthday").text(data.content.username);
        $("#lanLevel").text(data.content.englishLevel);
        $("#changeEng").val(data.content.englishLevel);
        console.log(data.content)


        if (data.content.gender == "female") {
            var img = document.createElement("IMG");
            img.src = "../contributors/images/female.png";
            $('#gender').html(img);
            // $('<img>',{id:'gender',src:'/images/female.jpeg'})
        }


        if (data.content.gender == "male") {
            // console.log(content.gender)
            var img = document.createElement("IMG");
            img.src = "../contributors/images/male.png";
            $('#gender').html(img);
        }



    }).fail(function (data) {
        alert("fail to get information")
    });

    $("#edit").click(function (e) {
        e.preventDefault();
        $("#editForm").toggle();
    })

    $("#save").click(function (e) {
        e.preventDefault();
        console.log("save: ",token);
        var data = JSON.stringify({email:$("#changeEmail").val(), nativeLanguage: $("#changeLan").val(),
            phone:$("#changeCell").val(), password:$("#changePwd").val(),englishLevel:parseInt($("#changeEng").val())});
        console.log(data);
        console.log(typeof(data));
        jQuery.ajax ({
            url: url,
            type: "PATCH",
            data: data,
            dataType: "json",
            contentType: "application/json; charset=utf-8"
            // beforeSend:function (xhr) {
            //     xhr.setRequestHeader ("Authorization", token);
            // }
        }).done(function(data){
            alert("Your information has been updated");
            location.reload()
        }).fail(function(data){
            $("#greeting").text("You might want to try it again");
        })
    });
})
