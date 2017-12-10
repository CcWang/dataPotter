$(document).ready(function () {

    $('.logout').click(function () {
        localStorage.clear();
        location.href=("/login");
    })
    $('.dashboard').click(function () {
        console.log('da')
        var userType = localStorage.getItem('userType');
        if (userType == "user"){
            location.href=("/users");

        }
        if (userType == "contributor"){
            location.href=("/contributors");

        }
    })
})