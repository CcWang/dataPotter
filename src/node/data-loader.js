var MongoClient = require('mongodb').MongoClient;

var dbConnection = null;

var request= require('request');

var userID = [];
var contributorID=[];

function getDbConnection(callback){
    MongoClient.connect("mongodb://localhost/dataPotter", function(err, db){
        if(err){
            console.log("Unable to connect to Mongodb");
        }else{
            dbConnection = db;
            callback();
        }
    });
};

function closeConnection() {
    if (dbConnection)
        dbConnection.close();

}

getDbConnection(function(){
    dbConnection.dropDatabase(function(err,doc){
        if (err)
            console.log("Could not drop database");
        else
            addUser();
            addContributor();
            console.log(contributorID)
            addMovie();
            addBook();

    });
});


function addUser() {
    var u = [{
        "username":    "John",
        "email":        "john@malkovich.com",
        "password": "3355691",
        "nativeLanguage": "English",
        "englishLevel":7,
        "phone":"0000000000",
        "gender":"male",
        "birthday":new Date(1990, 10, 9)
    },
        {
        "username": "Tyrion",
            "email": "tyrion@lannister.com",
            "password": "33hu5691",
            "nativeLanguage": "Chinese",
            "englishLevel": 4,
            "phone": "0010000000",
            "gender": "female",
            "birthday": new Date(1989, 5, 21)
        },

        {
            "username": "Sansa",
            "email": "sansa@stack.com",
            "password": "33hu5691",
            "nativeLanguage": "Chinese",
            "englishLevel": 4,
            "phone": "0010000000",
            "gender": "female",
            "birthday": new Date(1990, 12, 19)
        },
        {
            "username": "Arya",
            "email": "arya@stack.com",
            "password": "33hu5691",
            "nativeLanguage": "Italian",
            "englishLevel": 7,
            "phone": "0010000000",
            "gender": "female",
            "birthday": new Date(1995, 8, 19)
        }];
    var users = dbConnection.collection('users');
    for (var i = 0; i<u.length;i++ ){
        users.insertOne(u[i], function (err, doc) {
            if (err){
                console.log("Could not add user "+i)
            }else{
                userID[i] = doc.ops[0]._id.toString();
                addlanguageLevelUser(doc.ops[0]._id.toString());
            }

        })
    }
}


function addContributor() {
    var cc = [{
        "name": "Alex",
        "email": "alex@malkovich.com",
        "password": "121212",
        "nativeLanguage": "English",
        "phone": "0000000000",
        "gender": "male"
    },
        {
            "name": "Ben",
            "email": "ben@malkovich.com",
            "password": "343434",
            "nativeLanguage": "French",
            "phone": "1111111111",
            "gender": "female"
        },
        {
            "name": "Jon",
            "email": "jonn@malkovich.com",
            "password": "0029343434",
            "nativeLanguage": "English",
            "phone": "10974134111",
            "gender": "male"
        },
        {
            "name": "Arya",
            "email": "arya@malkovich.com",
            "password": "34324jj434",
            "nativeLanguage": "French",
            "phone": "111098111",
            "gender": "female"
        },
        {
            "name": "Smith",
            "email": "smith@malkovich.com",
            "password": "34al324jj434",
            "nativeLanguage": "English",
            "phone": "988098111",
            "gender": "male"
        }];
    var contributors = dbConnection.collection('contributors');
    for (var i=0; i<cc.length; i++){
        contributors.insertOne(cc[i],function(err,doc){
            if(err){
                console.log("could not add contributor"+i);
            }else{
                contributorID[i] = doc.ops[0]._id.toString();
                var page = i+1;
                var url="https://api.themoviedb.org/3/discover/movie?api_key=664f8054c78de425d08aba35e84e6a11&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page="+page.toString();
                var urltv="https://api.themoviedb.org/3/discover/tv?api_key=664f8054c78de425d08aba35e84e6a11&language=en-US&sort_by=popularity.desc&page="+page.toString();
                console.log(url)
                addMovie(url, contributorID[i]);
                addBookstoContributor(doc.ops[0]._id.toString(),100);
                addTvshows(urltv,contributorID[i])

            }
        })

    };

}


function addMovie(cId) {
    var m = [{
        "name": "Harry Potter and the Sorcer Stone",
        "genre": ["Adventure","Fantasy"],
        "level": ["avg","7","wordsLevel","9","speed","5"],
        "contributorId":cId
    },
        {
            "name": "Shrek",
            "genre": ["Adventure","Fantasy"],
            "level": ["avg","8","wordsLevel","6","speed","7"],
            "contributorId":cId
        },
        {
            "name": "X-Men (2000)",
            "genre": ["Action","Adventure"],
            "level": ["avg","10","wordsLevel","9","speed","5"],
            "contributorId":cId
        },
        {
            "name": "Black Swan",
            "genre": ["Drama","Thriller"],
            "level": ["avg","6","wordsLevel","9","speed","9"],
            "contributorId":cId
        }];
    var movies = dbConnection.collection('movie');
    for (var i = 0; i < m.length; i++){
        movies.insertOne(m[i],function (err,doc) {
            if (err){
                console.log("Could not add movie " + i);
            }else{
                addWatchList(userID[i],doc.ops[0]._id.toString());

            }

        })
    }


}

function addWatchList(userID, movieID) {
    var m = [{
        "userID": userID,
        "movieID": movieID,
        "tvShowID": null,
        "bookID": null,
        "audiobookID": null
    }];
    var watchLists = dbConnection.collection('watchList');
    watchLists.insertOne(m[0]);
}

function addBook() {
    var bb = [{
        "name": "Great Expectations",
        "genre": "Adventure",
        "level": 9
    }, {
        "name": "Snoopy",
        "genre": "Comedy",
        "level": 2
    },{

        "name":"The Fault in Our Stars",
        "genre":"Young adult fiction",
        "level":4
    },{
        "name":"Gone Girl",
        "genre":"Thriller",
        "level":5
    }];
    var books = dbConnection.collection('books');
    for (var i=0; i< bb.length; i++){
        books.insertOne(bb[i],function (err,doc) {
            if(err){
                console.log("Could not add book"+i);
            }else{
                addFavoriteList(userID[i],doc.ops[0]._id.toString())
            }

        })
    }

}


nameList = ['AA','BB','CC','DD','EE','FF','GG','HH','II','JJ','KK'];
genreList = ['Science fiction','Drama','Action and Adventure','Romance','Mystery','Horror'];

function addBookstoContributor(contributorId,count) {
    sequence = Array(count);
    console.log("sequence",sequence);
    var c = [];
    for (i=0;i<count;i++){
        console.log("Trying")
        var name = nameList[Math.floor(Math.random() * nameList.length)];
        var genre = genreList[Math.floor(Math.random() * genreList.length)];
        var level = Number(Math.floor(Math.random()*10));

        c.push ({
            name: name,
            genre: genre,
            level: level,
            contributorId: contributorId
        });

    }

    c.forEach(function(book){
        var books = dbConnection.collection('books');
        books.insertOne(book);
    })

}

setTimeout(closeConnection,5000);




function addFavoriteList(userID, bookID) {
    var ff = [{
        "userID": userID,
        "movieID": null,
        "tvShowID": null,
        "bookID": bookID,
        "audiobookID": null
    }];
    ff.forEach(function(WatchList){
        var favoriteLists = dbConnection.collection('favoriteLists');
        favoriteLists.insertOne(WatchList);
    })
}

// function addCarstoDriver0(driverId) {
//     c = [{
//         "make": "Ford",
//         "model": "Fiesta",
//         "year": 2011,
//         "size": "Compact",
//         "odometer": 34523,
//         "color": "red",
//         "driverId": driverId
//     }]
// }


function addlanguageLevelUser(userID) {

    var ll = [{
        "movies_level" : 8,
        "tvshows_level" : 10,
        "books_level" : 1,
        "audioBooks_level" : 7,
        "usersId" : userID
    },{
        "movies_level" : 6,
        "tvshows_level" : 2,
        "books_level" : 1,
        "audioBooks_level" : 7,
        "usersId" : userID
    },{
        "movies_level" : 9,
        "tvshows_level" : 9,
        "books_level" : 9,
        "audioBooks_level" : 7,
        "usersId" : userID
    },{
        "movies_level" : 10,
        "tvshows_level" : 10,
        "books_level" : 8,
        "audioBooks_level" : 10,
        "usersId" : userID
    }];
    var num = Math.floor(Math.random()*ll.length);
    console.log(num);

    var langLevel = dbConnection.collection('langs');
    langLevel.insertOne(ll[num], function(err,doc){
        if (err){
            console.log("Could not add driver 1");
        }

    })

}


function funcOne(url) {
    request.get(url, function(err, res, body) {
        if (!err && res.statusCode === 200) {
            funcTwo(body, function(err, output) {
                console.log(err, output);
            });
        }
    });
}
function funcTwo(input, callback) {
    // process input
    console.log(input)
}
var getMovie = "https://api.themoviedb.org/3/movie/550?api_key=664f8054c78de425d08aba35e84e6a11"
var gerneURL = "https://api.themoviedb.org/3/genre/movie/list?api_key=664f8054c78de425d08aba35e84e6a11&language=en-US"
// getGerneMap(gerneURL);
function getGerneMap(url){
    request.get(url,function(err,res,body){
        if(!err && res.statusCode === 200){
           cb(body)
        }
    })
}

function cb (a) {

   var g = JSON.parse(a);
   g = g["genres"];
   // var gerneMap={};
    for(var i=0; i<g.length; i++){
       gerneMap[g[i]["id"]]=g[i]["name"]
    }
    // console.log(gerneMap);
    return gerneMap;
    
}

function addMovie(url,id){
    request.get(url,function(err,res,body){
        if(!err && res.statusCode === 200){
            movies(body,id);
        }
    })

}
var gerneMap = {}
getGerneMap(gerneURL);
function movies (a,id) {
    var gmap={ '12': 'Adventure',
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
    var g = JSON.parse(a);
    g= g["results"]
    var movieColletction = dbConnection.collection('movie');
    for(var i=0; i<g.length;i++){
        // console.log(g[i]['genre_ids'])
        var name = g[i]['title'];
        var genre=""
        for (var j=0;j<g[i]['genre_ids'].length;j++){
            genre +=(gmap[g[i]['genre_ids'][j]])+" "
        }
        var avg = Math.floor(Math.random()*10)+1
        var wordL = Math.floor(Math.random()*10)+1
        var speed = Math.floor(Math.random()*10)+1
        var level="avg: "+avg+", wordsLevel: "+ wordL+", speed: "+speed
        // console.log(id)
        var m = {
                "name": name,
                "genre": genre,
                "level": level,
                "contributorId":id
            };

        movieColletction.insertOne(m,function (err,doc) {
            if (err){
                console.log("Could not add movie " + i);
            }else{
                // addWatchList(userID[i],doc.ops[0]._id.toString());

            }

        })
    }

}


//add tvshows
function addTvshows(url,id){
    request.get(url,function(err,res,body){
        if(!err && res.statusCode === 200){
            tvs(body,id);
        }
    })

}

function tvs (a,id) {
    var gmap={ '12': 'Adventure',
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
    var g = JSON.parse(a);
    g= g["results"]
    var tvColletction = dbConnection.collection('tvshow');
    for(var i=0; i<g.length;i++){
        // console.log(g[i]['genre_ids'])
        var name = g[i]['name'];
        var genre = ""
        for (var j=0;j<g[i]['genre_ids'].length;j++){
            genre+= gmap[g[i]['genre_ids'][j]]+" "
        }
        var avg = Math.floor(Math.random()*10)+1
        var wordL = Math.floor(Math.random()*10)+1
        var speed = Math.floor(Math.random()*10)+1
        var level="avg: "+avg+", wordsLevel: "+ wordL+", speed: "+speed
        // console.log(id)
        var tv = {
            "name": name,
            "genre": genre,
            "level": level,
            "contributorId":id
        };

        tvColletction.insertOne(tv,function (err,doc) {
            if (err){
                console.log("Could not add tv " + i);
            }else{
                // addWatchList(userID[i],doc.ops[0]._id.toString());

            }

        })
    }

}

setTimeout(closeConnection,5000);