package edu.cmu.sv.app17.models;


public class WatchList {
     String id = null;
     Number userID, movieID, tvShowID, bookID, audiobookID;
    public WatchList(Number userID, Number movieID, Number tvShowID,
                     Number bookID, Number audiobookID) {
        this.userID = userID;
        this.movieID = movieID;
        this.tvShowID = tvShowID;
        this.bookID = bookID;
        this.audiobookID = audiobookID;
    }
    public void setId(String id) {
        this.id = id;
    }
}
