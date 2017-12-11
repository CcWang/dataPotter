$(document).ready(function () {

    var gmap = {
        'ad': 'Adventure',
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
        'tv': 'TV Movie'
    };


    var listvalues = localStorage.getItem('contributor');
    //check if user has loged in
    if (listvalues == null) {
        alert("please log in");
        // window.location.replace("http://localhost:8080/login/");
        location.href = ('/login');
    }
    var finalvalue = JSON.parse(listvalues);
    var token = finalvalue.token;
    //check if user has loged in
    if (token == null) {
        alert("please log in");
        // window.location.replace("http://localhost:8080/login/");
        location.href = ('/login');
    }



})




    //movies next, previous, sort



    //tv next, previous, sort





