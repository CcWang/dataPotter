package edu.cmu.sv.app17.models;


public class WatchList {
     String id = null;
     String userID, movieID, tvShowID, bookID, audiobookID;
    public WatchList(String userID, String movieID, String tvShowID,
                     String bookID, String audiobookID) {
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
