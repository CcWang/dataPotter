$(document).ready(function () {
    console.log("this is main")
    $('.logout').click(function () {
        localStorage.clear();
        location.href=("/login");
    })
})