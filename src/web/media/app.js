$(document).ready(function () {
    var listvalues = localStorage.getItem('user');
    var media = localStorage.getItem('media');
    console.log(media)
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


})

