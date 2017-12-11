$(document).ready(function () {

    $(".userSec").hide();
    $(".conSec").hide();
    var media = localStorage.getItem('media');

    if (media == null) {
        alert("please log in");
        location.href = ("/login");
    }
    var userType = localStorage.getItem('userType');
    var listvalues;
    var finalvalue;
    media = JSON.parse(media)
    if (userType == "user"){
        listvalues = localStorage.getItem('user');
        finalvalue = JSON.parse(listvalues);
        // console.log(finalvalue)
        $(".userSec").show();
        $(".conSec").hide();
        $("#submit").click(function (e) {
            e.preventDefault();
            console.log("submit")
            submitComment(media.type,media.name,finalvalue.userId)
        })

        getComment(media.type,media.name,finalvalue.userId)

    }
    if (userType == "contributor"){
        listvalues = localStorage.getItem("contributor");
        finalvalue = JSON.parse(listvalues)
        // console.log(finalvalue)
        $(".userSec").hide();
        $(".conSec").show();

    }
    finalvalue = JSON.parse(listvalues);

    var token = finalvalue.token;
    if (listvalues == null) {
        alert("please log in");
        location.href = ("/login");
    }
    if (token == null) {
        alert("please log in");
        location.href = ("/login");
    }

})

function getComment(type,name,userID) {
    var id;
    jQuery.ajax({
        url:"../api/comment/"+type+"/"+name,
        type: "GET",
    }).done(function(data){
        data.content.forEach(function (item) {
            var comment = "<p>"+item.content+" ";
            id = item.id;
            if(item.userId == userID){
                comment +="<button class='btn btn-warning deleteComment'>X</button></p><br>"
            }
            $('#comments').append(comment)
        })
        $('#comments').on('click','.deleteComment',function(){
            console.log("delete")
            jQuery.ajax ({
                url:  "../api/comment/"+id,
                type: "DELETE",
            }).done(function(data){
                console.log(data)
                location.reload(true);
            }).fail(function(data){
                $("#greeting").text("You might want to try it again");
            })
        })
    })
}


// if (type == 'tv'){
//     if(data.languages.length>1){
//         data.languages.forEach(function (item) {
//             var lang = "<p style='display:inline-block'>"+item+"</p> | ";
//             $('#lang').append(lang)
//         })
//     }else{
//         var lang = "<p style='display:inline-block'>"+data.languages[0]+"</p> | ";
//         $('#lang').append(lang)
//     }

function submitComment(type,name,userId){

        var url = "../api/comment";
        var getdata = JSON.stringify({userId:userId, content: $("#comment").val(),
            mediaType:type, mediaName:name,})

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
            alert("Submitted successfully")
            $("#comment").val("")
            location.reload(true);


        }).fail(function(data){
            $("#greeting").text("You might want to try it again");
        });

}