var MongoClient = require('mongodb').MongoClient;

var dbConnection = null;

var lockCount = 0;



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
    });
});
/*
* username  string
* email     string
* password  string
* nativeLanguage    string
* englishLevel  string
* phone     string
* gender    string
* birthday Date
* */
function addUser() {
    u = [{
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
            "username":    "Shanshan",
            "email":        "ss@cmu.com",
            "password": "33hu5691",
            "nativeLanguage": "Chinese",
            "englishLevel":4,
            "phone":"0010000000",
            "gender":"female",
            "birthday":new Date(1995, 8, 19)
        }];
    var users = dbConnection.collection('users');
    users.insertOne(u[0], function(err,doc){
        if (err){
            console.log("Could not add driver 1");
        }
        else {
            addlanguageLevelUser0(doc.ops[0]._id.toString());
        }
    })
    users.insertOne(u[1], function(err,doc){
        if (err){
            console.log("Could not add driver 1");
        }
        else {
            addlanguageLevelUser1(doc.ops[0]._id.toString());
        }
    })
}
// need to finish
function addlanguageLevelUser0(userID) {
    ll = [{
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
    }];

    var langLevel = dbConnection.collection('langs');
    langLevel.insertOne(ll[0], function(err,doc){
        if (err){
            console.log("Could not add driver 1");
        }

    })

}

function addlanguageLevelUser1(userID) {
    ll = [{
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
    }];

    var langLevel = dbConnection.collection('langs');
    langLevel.insertOne(ll[1], function(err,doc){
        if (err){
            console.log("Could not add driver 1");
        }

    })

}

setTimeout(closeConnection,5000);