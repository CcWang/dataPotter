var MongoClient = require('mongodb').MongoClient;

var dbConnection = null;

var lockCount = 0;

var userID=[];
var contributorId=[];

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
    for (var i = 0; i < cc.length; i++) {
        contributors.insertOne(cc[i], function (err, doc) {
            if (err) {
                console.log("could not add contributor" + i);
            }
            else {
                contributorId[i] = doc.ops[0]._id.toString();
                addBookstoContributor(doc.ops[0]._id.toString(), 100);

            }
        })
    }
};

function addMovie() {
    var m = [{
        "name": "Harry Potter and the Sorcer Stone",
        "genre": ["Adventure","Fantasy"],
        "level": ["avg","7","wordsLevel","9","speed","5"]
    },
        {
            "name": "Shrek",
            "genre": ["Adventure","Fantasy"],
            "level": ["avg","8","wordsLevel","6","speed","7"]
        },
        {
            "name": "X-Men (2000)",
            "genre": ["Action","Adventure"],
            "level": ["avg","10","wordsLevel","9","speed","5"]
        },
        {
            "name": "Black Swan",
            "genre": ["Drama","Thriller"],
            "level": ["avg","6","wordsLevel","9","speed","9"]
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

// function addBook() {
//     var bb = [{
//         "name": "Great Expectations",
//         "genre": "Adventure",
//         "level": 9,
//         "contributorId":
//
//     }, {
//         "name": "Snoopy",
//         "genre": "Comedy",
//         "level": 2
//     },{
//
//         "name":"The Fault in Our Stars",
//         "genre":"Young adult fiction",
//         "level":4
//     },{
//         "name":"Gone Girl",
//         "genre":"Thriller",
//         "level":5
//     }];
//     var books = dbConnection.collection('books');
//     for (var i=0; i< bb.length; i++){
//         books.insertOne(bb[i],function (err,doc) {
//             if(err){
//                 console.log("Could not add book"+i);
//             }else{
//                 addFavoriteList(userID[i],doc.ops[0]._id.toString())
//             }
//
//         })
//     }
//
// }


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

};


setTimeout(closeConnection,5000);