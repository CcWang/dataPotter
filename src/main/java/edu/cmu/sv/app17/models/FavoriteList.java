package edu.cmu.sv.app17.models;

public class FavoriteList {

    String id = null;
    String usersID;
    String movieID;
    String tvShowID;
    String bookID;
    String audioBookID;

    public FavoriteList(String usersID, String movieID,
        String tvShowID, String bookID, String audioBookID){
        this.usersID= usersID;
        this.movieID = movieID;
        this.tvShowID = tvShowID;
        this.bookID = bookID;
        this.audioBookID = audioBookID;
    }
    public void setId(String id) {
        this.id = id;
    }
}
