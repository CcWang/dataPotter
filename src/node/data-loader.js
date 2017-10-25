var MongoClient = require('mongodb').MongoClient;

var dbConnection = null;



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
    // users.insertOne(u[0], function(err,doc){
    //     if (err){
    //         console.log("Could not add user 0");
    //     }
    //     else {
    //         userID0 =doc.ops[0]._id.toString();
    //         addlanguageLevelUser0(doc.ops[0]._id.toString());
    //     }
    // })
    // users.insertOne(u[1], function(err,doc){
    //     if (err){
    //         console.log("Could not add driver 1");
    //     }
    //     else {
    //         userID1 =doc.ops[0]._id.toString();
    //         addlanguageLevelUser1(doc.ops[0]._id.toString());
    //
    //     }
    // })
}

function addTeacher() {
    tt = [{
        "teacherName":    "Alex",
        "email":        "alex@malkovich.com",
        "password": "121212",
        "nativeLanguage": "English",
        "phone":"0000000000",
        "gender":"male",
        "exp":7,
        "newStudent":true
    },
        {
            "teacherName":    "Ben",
            "email":        "ben@malkovich.com",
            "password": "343434",
            "nativeLanguage": "French",
            "phone":"1111111111",
            "gender":"female",
            "exp":9,
            "newStudent":false
        }];
    var teachers = dbConnection.collection('teachers');
    teachers.insertOne(tt[0], function(err,doc){
        if (err){
            console.log("Could not add teacher 1");
        }
        else {
            teacherID0 =doc.ops[0]._id.toString();
            addbookTeacher0(doc.ops[0]._id.toString());
        }
    })
    users.insertOne(tt[1], function(err,doc){
        if (err){
            console.log("Could not add teacher 2");
        }
        else {
            teacherID1 =doc.ops[0]._id.toString();
            addbookTeacher1(doc.ops[0]._id.toString());

        }
    })
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
        }];
    var contributors = dbConnection.collection('contributors');
    for (var i=0; i<length(cc); i++){
        contributors.insertOne(cc[i],function(err,doc){
            if(err){
                console.log("could not add contributor"+i);
            }else{

            }
        })
    }
    contributors.insertOne(tt[0], function (err, doc) {
        if (err) {
            console.log("Could not add contributor");
        }
        else {
            contributorID[i] = doc.ops[0]._id.toString();


        }
    });
}


function addMovie() {
    var m = [{
        "name": "Harry Potter and the Sorcer Stone",
        "genre": "Adventure/Fantasy",
        "level": 6
    },
        {
            "name": "Shrek",
            "genre": "Adventure/Fantasy",
            "level": 4
        },
        {
            "name": "X-Men (2000)",
            "genre": "Action/Adventure",
            "level": 2
        },
        {
            "name": "Black Swan",
            "genre": "Drama/Thriller ",
            "level": 6
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

// need to finish
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


setTimeout(closeConnection,5000);