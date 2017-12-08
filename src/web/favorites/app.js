$(document).ready(function () {
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
    $("#user").text(finalvalue.username);

    getFavs(finalvalue.userId,token);
    $(document).on('click',".remove", function () {
        var favListId = $(this).next().val();
        jQuery.ajax({
            url:"../api/favoriteLists/"+favListId,
            type:"DELETE",
            beforeSend:function (xhr) {
                xhr.setRequestHeader("Authorization", token);

            }
        })
            .done(function (data) {
                alert("You have remove it from favorites list.");
                location.reload();

            })
            .fail(function (data) {
                alert("Try again later");
            })
    })
    $(document).on('click',".detail", function () {
        var name = $(this).next().val();
        var type =this.classList[this.classList.length-1];
    //    link to media page for individual media
        localStorage.setItem('media', JSON.stringify({"type":type, "name":name}));
        location.href=("/media");

    })
    $(document).on('click',".analysis", function () {
        var name = $(this).next().val();
        var type =this.classList[this.classList.length-1];
        //    link to media page for individual media
        localStorage.setItem('media', JSON.stringify({"type":type, "name":name}));
        location.href=("/scoreAnlysis");
    })
})

function getFavs(id,token) {
    var url = "../api/favoriteLists/getall/"+id;
    jQuery.ajax({
        url:url,
        type:"GET",
        beforeSend:function (xhr) {
            xhr.setRequestHeader("Authorization", token);

        }
    })
        .done(function (data) {
            var movie = data.content.movies;
            var book = data.content.book;
            var tvshow = data.content.tvshows;
            var movieNames = Object.keys(movie);
            var tvNames = Object.keys(tvshow);
            var bookNames = Object.keys(book);

            movieNames.forEach(function(item){
                var tableRow = '<tr> <th scope="row">*</th><td>';
                tableRow =tableRow+item+"</td><td>";
                tableRow = tableRow + '<button class="btn btn-sm btn-warning remove">Remove</button><input type = "hidden" value = '+movie[item]+'></td><td>';
                tableRow = tableRow + '<button class="btn btn-sm btn-primary detail movies">Detail</button><input type = "hidden" value = "'+item+'"></td><td>';
                tableRow = tableRow + '<button class="btn btn-sm btn-info analysis movies">Analysis</button><input type = "hidden" value = "'+item+'"></td><td>';

                $('#movieTable > tbody:last-child').append(tableRow);


            });
            tvNames.forEach(function(item){
                var tableRow = '<tr> <th scope="row">*</th><td>';
                tableRow =tableRow+item+"</td><td>";
                tableRow = tableRow + '<button class="btn btn-sm btn-warning remove">Remove</button><input type = "hidden" value = '+tvshow[item]+'></td><td>';
                tableRow = tableRow + '<button class="btn btn-sm btn-primary detail tvshows">Detail</button><input type = "hidden" value = "'+item+'"></td><td>';
                tableRow = tableRow + '<button class="btn btn-sm btn-info analysis tvshows">Analysis</button><input type = "hidden" value = "'+item+'"></td><td>';

                $('#tvTable > tbody:last-child').append(tableRow);


            });
            bookNames.forEach(function(item){
                var tableRow = '<tr> <th scope="row">*</th><td>';
                tableRow =tableRow+item+"</td><td>";
                tableRow = tableRow + '<button class="btn btn-sm btn-warning remove">Remove</button><input type = "hidden" value = '+book[item]+'></td><td>';
                tableRow = tableRow + '<button class="btn btn-sm btn-primary detail books">Detail</button><input type = "hidden" value = "'+item+'"></td><td>';
                tableRow = tableRow + '<button class="btn btn-sm btn-info analysis books">Analysis</button><input type = "hidden" value = "'+item+'"></td><td>';


                $('#bookTable > tbody:last-child').append(tableRow);


            });




        })
        .fail(function (data) {
            alert("soory, try again later");

        })
}

