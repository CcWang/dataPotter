$(document).ready(function () {

    $('#sidebarCollapse').on('click', function () {
        $('#sidebar').toggleClass('active');
    });
    var offset = 0;
    var count = 20;
    var total = -1;
    $("#editForm").hide();
    // console.log(document.cookie)
    var listvalues = localStorage.getItem('contributor');
    //check if user has loged in
    if (listvalues == null){
        alert("please log in");
        window.location.replace("http://localhost:8080/login/");
    }
    var finalvalue = JSON.parse(listvalues);
    var token = finalvalue.token;
    //check if user has loged in
    if(token == null){
        alert("please log in");
        window.location.replace("http://localhost:8080/login/");
    }

    var url = "/api/contributors/"+finalvalue.contributorId;

    jQuery.ajax ({
        url:  url,
        type: "GET",
        beforeSend:function (xhr) {
            xhr.setRequestHeader ("Authorization", token);
        }
    }).done(function(data){
        $(".name").text(data.content.name);
        $("#email").text(data.content.email);
        $("#changeEmail").val(data.content.email);
        $("#changePwd").val(data.content.password);
        $("#lan").text(data.content.nativeLanguage);
        $("#changeLan").val(data.content.nativeLanguage);
        $("#changeCell").val(data.content.phone);
        $("#cell").text(data.content.phone);
        if (data.content.gender == "female"){
            var img = document.createElement("IMG");
            img.src = "images/female.png";
            $('#gender').html(img);
            // $('<img>',{id:'gender',src:'/images/female.jpeg'})
        }
        if (data.content.gender == "male"){
            // console.log(content.gender)
            var img = document.createElement("IMG");
            img.src = "images/male.png";
            $('#gender').html(img);
        }
        $('.tvs').hide();
        $('.books').hide();
        $('.movies').show();
        getMoview();

    }).fail(function(data){
        $("#greeting").text("You might want to try it again");

    })
    $("#edit").click(function (e) {
        e.preventDefault();
        $("#editForm").toggle();

    })
    $("#save").click(function (e) {
        e.preventDefault();
        jQuery.ajax ({
            url: url,
            type: "PATCH",
            data: JSON.stringify({email:$("#changeEmail").val(), nativeLanguage: $("#changeLan").val(), phone:$("#changeCell").val()}),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            beforeSend:function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        }).done(function(data){
            alert("Your information has been updated");
            location.reload()
        }).fail(function(data){
                $("#greeting").text("You might want to try it again");
                $("#getcars").hide();
        })
    });
    $(".showMovies").click(function (e) {
        console.log("clicked show Movies")
        e.preventDefault();
        $('.tvs').hide();
        $('.books').hide();
        $('.movies').show();
        getMoview();

    });


    $(".showTvs").click(function (e) {
        e.preventDefault();
        $('.tvs').show();
        $('.books').hide();
        $('.movies').hide();
        getTV();

    })

    $(".showBooks").click(function (e) {
        e.preventDefault();
        $('.tvs').hide();
        $('.books').show();
        $('.movies').hide();
        getBooks();

    })

    function getMoview() {
        console.log("hit get movie",finalvalue)

        jQuery.ajax ({
            // url:  "/api/contributors/" + finalvalue.contributorId + "/movies?offset=" + offset + "&count="  + count,
            url:"/api/contributors/" + finalvalue.contributorId + "/movies",
            type: "GET",
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                total = data.metadata.total;
                console.log(total);
                $("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
                $("#movieTable").find(".cloned").remove();
                data.content.forEach(function(item){
                    $( "#movieRow" ).clone().prop("id",item.id).appendTo( "#movieTable" );
                    $("#"+item.id).find("#mName").text(item.name);
                    $("#"+item.id).find("#genre").text(item.genre);
                    $("#"+item.id).find("#level").text(item.level);
                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
            })
            .fail(function(data){
                $("#movieList").text("Sorry no cars");
            })

    }

    function getTV() {


        jQuery.ajax ({
            // url:  "/api/contributors/" + finalvalue.contributorId + "/tvshows?offset=" + offset + "&count="  + count,
            url:"/api/contributors/" + finalvalue.contributorId + "/tvshows",
            type: "GET",
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                total = data.metadata.total;
                $("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
                $("#tvTable").find(".cloned").remove();
                data.content.forEach(function(item){
                    $( "#tvRow" ).clone().prop("id",item.id).appendTo( "#tvTable" );
                    $("#"+item.id).find("#tvName").text(item.name);
                    $("#"+item.id).find("#tvgenre").text(item.genre);
                    $("#"+item.id).find("#tvlevel").text(item.level);
                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
            })
            .fail(function(data){
                $("#tvList").text("Sorry no tvs");
            })

    }

    function getBooks() {


        jQuery.ajax ({
            // url:  "/api/contributors/" + finalvalue.contributorId + "/books?offset=" + offset + "&count="  + count,
            url:"/api/contributors/" + finalvalue.contributorId + "/books",
            type: "GET",
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                total = data.metadata.total;
                $("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
                $("#bookTable").find(".cloned").remove();
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
                $("#bookList").text("Sorry no tvs");
            })

    }

});
