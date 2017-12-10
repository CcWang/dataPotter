var MongoClient = require('mongodb').MongoClient;

var dbConnection = null;


var lockCount = 0;

var contributorId=[];

var request= require('request');

var userID = [];
var contributorID=[];
var savyID=[];


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
            addSavy();
            // addBook();
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
        "birthday":"1990-10-09"
    },
        {
        "username": "Tyrion",
            "email": "tyrion@lannister.com",
            "password": "33hu5691",
            "nativeLanguage": "Chinese",
            "englishLevel": 4,
            "phone": "0010000000",
            "gender": "female",
            "birthday": "1985-5-21"
        },

        {
            "username": "Sansa",
            "email": "sansa@stack.com",
            "password": "33hu5691",
            "nativeLanguage": "Chinese",
            "englishLevel": 4,
            "phone": "0010000000",
            "gender": "female",
            "birthday": "1989-11-08"
        },
        {
            "username": "Arya",
            "email": "arya@stack.com",
            "password": "33hu5691",
            "nativeLanguage": "Italian",
            "englishLevel": 7,
            "phone": "0010000000",
            "gender": "female",
            "birthday":"1995-8-19"
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
            "gender": "male"
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
                contributorId[i] = doc.ops[0]._id.toString();
                addBookstoContributor(doc.ops[0]._id.toString(), 100);

                // var url="https://api.themoviedb.org/3/discover/movie?api_key=664f8054c78de425d08aba35e84e6a11&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page="+page.toString();

                console.log(url)
                for(var j=0;j<6;j++){
                    var page = j+1;
                    var url="https://api.themoviedb.org/3/discover/movie?api_key=664f8054c78de425d08aba35e84e6a11&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page="+page.toString();
                    var urltv="https://api.themoviedb.org/3/discover/tv?api_key=664f8054c78de425d08aba35e84e6a11&language=en-US&sort_by=popularity.desc&page="+page.toString();
                    addMovie(url, contributorID[i]);
                    addTvshows(urltv,contributorID[i])
                }

                addBookstoContributor(doc.ops[0]._id.toString(),100);



            }
        })
    }
};

function addSavy() {
    var s = [{
            "question": "This is Question 1",
            "answer01": "Answer 1",
            "answer02": "Answer 2",
            "answer03": "Answer 3",
            "answer04": "Answer 4",
            "answer01count": 0,
            "answer02count": 0,
            "answer03count": 0,
            "answer04count": 0
        },

        {
            "question": "This is Question 2",
            "answer01": "Answer 11",
            "answer02": "Answer 22",
            "answer03": "Answer 33",
            "answer04": "Answer 44",
            "answer01count": 11,
            "answer02count": 22,
            "answer03count": 33,
            "answer04count": 44
        },

        {
            "question": "This is Question 3",
            "answer01": "Answer 101",
            "answer02": "Answer 202",
            "answer03": "Answer 303",
            "answer04": "Answer 404",
            "answer01count": 10,
            "answer02count": 20,
            "answer03count": 30,
            "answer04count": 40
        },
        {
            "question": "This is Question 4",
            "answer01": "Answer 111",
            "answer02": "Answer 222",
            "answer03": "Answer 333",
            "answer04": "Answer 444",
            "answer01count": 111,
            "answer02count": 222,
            "answer03count": 333,
            "answer04count": 444
        }];

    var savy = dbConnection.collection('savy');
    for (var i = 0; i<s.length;i++ ){
        savy.insertOne(s[i], function (err, doc) {
            if (err){
                console.log("Could not add savy "+i)
            }else{
                savyID[i] = doc.ops[0]._id.toString();
            }

        })
    }
}

function addMovie(cId) {
    var m = [{
        "name": "Harry Potter and the Sorcer Stone",
        "genre": ["Adventure","Fantasy"],
        "level": 7,
        "contributorId":cId
    },
        {
            "name": "Shrek",
            "genre": ["Adventure","Fantasy"],
            "level": 8,
            "contributorId":cId
        },
        {
            "name": "X-Men (2000)",
            "genre": ["Action","Adventure"],
            "level": 10,
            "contributorId":cId
        },
        {
            "name": "Black Swan",
            "genre": ["Drama","Thriller"],
            "level": 6,
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
    }];
    var watchLists = dbConnection.collection('watchList');
    watchLists.insertOne(m[0]);
}



nameList = ['AA','BB','CC','DD','EE','FF','GG','HH','II','JJ','KK'];
genreList = ['Science fiction','Drama','Action and Adventure','Romance','Mystery','Horror'];

function addBookstoContributor(contributorId,count) {
    console.log(contributorId);
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

};


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
        var level = Math.floor(Math.random()*10)+1
        // var wordL = Math.floor(Math.random()*10)+1
        // var speed = Math.floor(Math.random()*10)+1
        // var level="avg: "+avg+", wordsLevel: "+ wordL+", speed: "+speed
        // console.log(id)
        var m = {
                "name": name,
                "genre": genre,
                "level": level,
                "contributorId":id,
                "movieid":g[i]['id']
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
        var level = Math.floor(Math.random()*10)+1
        // var wordL = Math.floor(Math.random()*10)+1
        // var speed = Math.floor(Math.random()*10)+1
        // var level="avg: "+avg+", wordsLevel: "+ wordL+", speed: "+speed
        // console.log(id)
        var tv = {
            "name": name,
            "genre": genre,
            "level": level,
            "contributorId":id,
            "tvid":g[i]['id']
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