$(document).ready(function () {

    var gmap={ 'ad': 'Adventure',
        'fan': 'Fantasy',
        'an': 'Animation',
        'dr': 'Drama',
        'ho': 'Horror',
        'ac': 'Action',
        'co': 'Comedy',
        'hi': 'History',
        'we': 'Western',
        'th': 'Thriller',
        'cr': 'Crime',
        'do': 'Documentary',
        'sc': 'Science Fiction',
        'my': 'Mystery',
        'mu': 'Music',
        'ro': 'Romance',
        'fam': 'Family',
        'wa': 'War',
        'tv': 'TV Movie' };
    var genresMaps={ '12': 'Adventure',
        '14': 'Fantasy',
        '16': 'Animation',
        '18': 'Drama',
        '27': 'Horror',
        '28': 'Action',
        '35': 'Comedy',
        '36': 'History',
        '37': 'Western',
        '53': 'Thriller',
        '80': 'Crime',
        '99': 'Documentary',
        '878': 'Science Fiction',
        '9648': 'Mystery',
        '10402': 'Music',
        '10749': 'Romance',
        '10751': 'Family',
        '10752': 'War',
        '10770': 'TV Movie' };

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
        // window.location.replace("http://localhost:8080/login/");
        location.href=('/login');
    }
    var finalvalue = JSON.parse(listvalues);
    var token = finalvalue.token;
    //check if user has loged in
    if(token == null){
        alert("please log in");
        // window.location.replace("http://localhost:8080/login/");
        location.href=('/login');
    }

    var url = "/api/contributors/"+finalvalue.contributorId;
    getUser(token);

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
            getUser(token)
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
        getMovies();

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

    });
    //movies next, previous, sort

    $("#mn").click(function(e){
        e.preventDefault();
       console.log("click mn")
        if (offset+count < total) {
            offset = offset+count;
                getMovies();
        }

    })

    $("#mp").click(function(e){
        e.preventDefault();
        console.log("Cliked")
        if (offset-count >= 0) {
            offset = offset-count;

                getMovies();

        }
    })
    $("#sortGMovie").click(function(e){
        e.preventDefault();

        getMovies("genre");


    })

    $("#sortNMovie").click(function(e){
        e.preventDefault();
        getMovies("name");


    })
    //tv next, previous, sort

    $("#ntv").click(function(e){
        e.preventDefault();
        console.log("click mn")
        if (offset+count < total) {
            offset = offset+count;
            getTV();
        }

    })

    $("#ptv").click(function(e){
        e.preventDefault();
        console.log("Cliked")
        if (offset-count >= 0) {
            offset = offset-count;

            getTV();

        }
    })
    $("#sortGTv").click(function(e){
        e.preventDefault();

        getTV("genre");


    })

    $("#sortNTv").click(function(e){
        e.preventDefault();
        getTV("name");


    })

    //books next, previous, sort

    $("#nBook").click(function(e){
        e.preventDefault();
        console.log("click mn")
        if (offset+count < total) {
            offset = offset+count;
            getBooks();
        }

    })

    $("#pBook").click(function(e){
        e.preventDefault();
        console.log("Cliked")
        if (offset-count >= 0) {
            offset = offset-count;

            getBooks();

        }
    })
    $("#sortGBook").click(function(e){
        e.preventDefault();

        getBooks("genre");


    })

    $("#sortNBook").click(function(e){
        e.preventDefault();
        getBooks("name");


    })
    function getUser(token) {
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
            getMovies();

        }).fail(function(data){
            $("#greeting").text("You might want to try it again");

        })
    }
    function getMovies(sort_term) {
        var getUrl;
        if (sort_term){
            getUrl = "/api/contributors/"+ finalvalue.contributorId+"/movies?sort="+sort_term+"&offset="+offset + "&count=" +count;
        }else{
            getUrl = "/api/contributors/"+ finalvalue.contributorId+"/movies?offset="+offset+"&count=" +count;
        }

        jQuery.ajax ({
            // url:  "/api/contributors/" + finalvalue.contributorId + "/movies?offset=" + offset + "&count="  + count,
            // url:"/api/contributors/" + finalvalue.contributorId + "/movies",
            url:getUrl,
            type: "GET",
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                total = data.metadata.total;
                console.log(total);
                $("#mpage").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
                $("#movieTable").find(".cloned").remove();
                data.content.forEach(function(item){

                    $( "#movieRow" ).clone().prop("id",item.id).appendTo( "#movieTable" );
                    $("#"+item.id).find("#mName").text(item.name);
                    $("#"+item.id).find("#genre").text(item.genre);
                    $("#"+item.id).find("#level").text(item.level);
                    $("#"+item.id).find("#action").append("<button class='btn btn-info add'>⬆</button>   <button class='btn btn-info minus'>⬇</button>     <button class='btn btn-info delete'>❌</button>");
                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();

                });
            })
            .fail(function(data){
                $("#movieList").text("Sorry no movies");
            })

    }

    function getTV(sort_term) {
        var getUrl;
        if (sort_term){
            getUrl = "/api/contributors/"+ finalvalue.contributorId+"/tvshows?sort="+sort_term+"&offset="+offset + "&count=" +count;
        }else{
            getUrl = "/api/contributors/"+ finalvalue.contributorId+"/tvshows?offset="+offset+"&count=" +count;
        }


        jQuery.ajax ({
            // url:  "/api/contributors/" + finalvalue.contributorId + "/tvshows?offset=" + offset + "&count="  + count,
            // url:"/api/contributors/" + finalvalue.contributorId + "/tvshows",
            url:getUrl,
            type: "GET",
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                total = data.metadata.total;
                $("#pagetv").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
                $("#tvTable").find(".cloned").remove();
                data.content.forEach(function(item){
                    $( "#tvRow" ).clone().prop("id",item.id).appendTo( "#tvTable" );
                    $("#"+item.id).find("#tvName").text(item.name);
                    $("#"+item.id).find("#tvgenre").text(item.genre);
                    $("#"+item.id).find("#tvlevel").text(item.level);
                    $("#"+item.id).find("#tvaction").append("<button class='btn btn-info add'>⬆</button>   <button class='btn btn-info minus'>⬇</button>     <button class='btn btn-info delete'>❌</button>");

                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
            })
            .fail(function(data){
                $("#tvList").text("Sorry no tvs");
            })

    }

    function getBooks(sort_term) {

        var getUrl;
        if (sort_term){
            getUrl = "/api/contributors/"+ finalvalue.contributorId+"/books?sort="+sort_term+"&offset="+offset + "&count=" +count;
        }else{
            getUrl = "/api/contributors/"+ finalvalue.contributorId+"/books?offset="+offset+"&count=" +count;
        }
        jQuery.ajax ({
            // url:  "/api/contributors/" + finalvalue.contributorId + "/books?offset=" + offset + "&count="  + count,
            // url:"/api/contributors/" + finalvalue.contributorId + "/books",
            url:getUrl,
            type: "GET",
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                total = data.metadata.total;
                $("#pageBook").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
                $("#bookTable").find(".cloned").remove();
                data.content.forEach(function(item){
                    $( "#bookRow" ).clone().prop("id",item.id).appendTo( "#bookTable" );
                    $("#"+item.id).find("#bName").text(item.name);
                    $("#"+item.id).find("#bgenre").text(item.genre);
                    $("#"+item.id).find("#blevel").text(item.level);
                    $("#"+item.id).find("#baction").append("<button class='btn btn-info add'>⬆</button>   <button class='btn btn-info minus'>⬇</button>     <button class='btn btn-info delete'>❌</button>");

                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
            })
            .fail(function(data){
                $("#bookList").text("Sorry no tvs");
            })

    }
    $(document).on('click', '.add', function() {
        var id = $(this).closest("tr")[0].id;
        // console.log(id);
        var type = $(this).closest("table")[0].classList[2];
        if (type == "movies"){
            var cLevel = $(this).parent().parent().find('#level').text();

        }else if(type == "tvshows"){
            var cLevel = $(this).parent().parent().find('#tvlevel').text();

        }else if(type == 'books'){
            var cLevel = $(this).parent().parent().find('#blevel').text();
        }
        console.log(cLevel)
        if(cLevel <10) {
            update(id, type,cLevel,'add');
        }else{
            alert("10 is the highest level");
        }


    });
    $(document).on('click', '.minus', function() {
        var id = $(this).closest("tr")[0].id;
        var type = $(this).closest("table")[0].classList[2];
        if (type == "movies"){
            var cLevel = $(this).parent().parent().find('#level').text();

        }else if(type == "tvshows"){
            var cLevel = $(this).parent().parent().find('#tvlevel').text();

        }else if(type == 'books'){
            var cLevel = $(this).parent().parent().find('#blevel').text();
        }
        console.log(cLevel)
        if(cLevel >0) {
            update(id, type,cLevel,'minus');
        }else{
            alert("0 is the lowest level.");
        }


    });
    $(document).on('click', '.delete', function() {
        var id = $(this).closest("tr")[0].id;

        var type = $(this).closest("table")[0].classList[2];
        if (type == "movies"){
            var name = $(this).parent().parent().find('#mName').text();

        }else if(type == "tvshows"){
            var name = $(this).parent().parent().find('#tvName').text();

        }else if(type == 'books'){
            var name = $(this).parent().parent().find('#bName').text();

        }



        if (confirm('Are you sure you want to delete '+name+"?")) {
            // Save it!
            deleteMedia(id,type);
        }


    });

    function update(id, type,level,cal) {
        var l;
        if (cal == 'add'){
            l = parseInt(level)+1;
        }
        if(cal == 'minus'){
            l = parseInt(level)-1;
        }
        console.log(type, level, cal)
        jQuery.ajax ({
            url: "../api/"+type+"/"+id,
            type: "PATCH",
            data: JSON.stringify({level:l}),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            beforeSend:function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        }).done(function(data){
            alert("Your information has been updated");
            if (type == "movies"){
                getMovies();
            }
            if (type == "tvshows"){
                getTV();
            }
            if (type = 'books'){
                getBooks();
            }
        }).fail(function(data){
            $("#greeting").text("You might want to try it again");
        })

    }

    function deleteMedia(id,type){
        jQuery.ajax ({
            url: "../api/"+type+"/" +finalvalue.contributorId+"/"+id,
            type: "DELETE",
            data: null,
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            beforeSend:function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        }).done(function(data){
            alert("deleted!");
            if (type == "movies"){
                getMovies();
            }
            if (type == "tvshows"){
                getTV();
            }
            if (type = 'books'){
                getBooks();
            }
        }).fail(function(data){
            $("#greeting").text("You might want to try it again");
        })
    }

    //check genre values:
    $('.genres').on("keyup",function () {
        var g = this.value;
        g=g.toLowerCase();


        if(g.length >1 && !g.startsWith("fa")){
            if (gmap[g]){
                this.value = (gmap[g])
                return;
            }else{
                alert("No such genre, please try again");
                this.value = "";
                return;
            }
        }else if(g.length >2 && g.startsWith("fa")){
            if (gmap[g]){
                this.value = (gmap[g])
                return
            }else{
                alert("No such genre, please try again");
                this.value = "";
                return;
            }
        }



    });

    
    $(document).on('click',"#mName", function () {
        console.log("clicked")
        var name = $(this).text();

        localStorage.setItem('media', JSON.stringify({"type":"movies", "name":name}));
        location.href=("/media")
    })
    $(document).on('click',"#tvName", function () {
        var name = $(this).text();


        localStorage.setItem('media', JSON.stringify({"type":"tv", "name":name}));
        location.href=("/media");
        //
    })
    $(document).on('click',"#bName", function () {
        var name = $(this).text();


        localStorage.setItem('media', JSON.stringify({"type":"books", "name":name}));
        location.href=("/media");
        //
    })

//    add new movie/tv
    $(".addNew").click(function (e) {
        e.preventDefault();
        console.log("addnew")
        var type =  $(this).closest("tr")[0].classList[0];
        var name = $("."+type+"newName").val();
        var dl = parseInt($("."+type+"dl").val());
        if(dl<0){
            alert("the level should be greater than 0, please re-enter");
            return;

        }
        if(dl>10){
            alert("the level should be less than 11, please re-enter");
            return;
        }
        //use ExternalAPIInterface.java to find the movie in that name
        if (type == "movies" || type =="tvshows"){
            jQuery.ajax({
                url:"../api/themoviedb/find/"+type+"/"+name,
                type:"GET",
                data:null,
                dataType:"json",
                contentType:"application/json; charset=utf-8"
                
            }).done(function (data) {
                data = JSON.parse(data.content);
                data = data["results"];
                var stop = false;
                var idx = 0;

                function showdialog(data, idx) {
                    if (idx < data.length) {
                        if (type == "movies") {
                            var mediaName = data[idx].title;
                            var mid = "movieid";
                        } else {
                            var mediaName = data[idx].name;
                            var mid = "tvid";
                        }
                        var poster = data[idx].poster_path;

                        $('#askingName').html("Do you want to add: " + mediaName + " ?");
                        $('#askingImg').attr("src", "https://image.tmdb.org/t/p/w300/" + poster);
                        //style="background: cadetblue

                        $('#dialog-confirm').attr("background-color","cadetblue");
                        $('#dialog-confirm').dialog({
                            resizeable: false,
                            height: "auto",
                            width: "auto",
                            modal: true,
                            buttons: {
                                "Yes": function () {
                                    var movieInfo = data[idx];
                                    var genre = "";

                                    for (var j=0;j<movieInfo['genre_ids'].length;j++){
                                        genre+= genresMaps[movieInfo['genre_ids'][j]]+" "
                                    }

                                    var d = {name:mediaName, genre:genre, level:dl};
                                    d[mid] = data[idx].id
                                    var mdata = JSON.stringify(d);

                                    addToDb(finalvalue.contributorId,mdata,token,type);
                                    $(this).dialog("close");
                                },
                                "Next": function () {
                                    $(this).dialog("close");
                                    showdialog(data, idx+1);
                                },
                                "Cancel": function () {
                                    $(this).dialog("close");
                                },


                            }
                        });
                    }
                }

                if (data.length > 0) {
                    showdialog(data, idx);
                }


            })
        }else{
            var genre = $("."+type+"genres").val();
            var data = JSON.stringify({name:name, genre:genre, level:dl});
            addToDb(finalvalue.contributorId,data,token,type);

        }





    });


function addToDb(conId,data,token,type) {

    jQuery.ajax ({
        url: "../api/"+type+"/create/" +conId,
        type: "POST",
        data: data,
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        beforeSend:function (xhr) {
            xhr.setRequestHeader ("Authorization", token);
        }
    }).done(function(data){
        alert("success!");
        $("."+type+"newName").val("");
        $("."+type+"genres").val("");
        $("."+type+"dl").val(parseInt(0));
        if (type == "movies"){
            getMovies();
        }
        if (type == "tvshows"){
            getTV();
        }
        if (type = 'books'){
            getBooks();
        }
    }).fail(function(data){
        alert("sorry, try again!");
    })

}
});
