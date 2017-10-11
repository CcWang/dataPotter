package edu.cmu.sv.app17.models;

public class FavoriteList {

    String id = null;
    String userID;
    String movieID;
    String tvShowID;
    String bookID;
    String audioBookID;

    public FavoriteList(String userID, String movieID,
        String tvShowID, String bookID, String audioBookID){
        this.userID= userID;
        this.movieID = movieID;
        this.tvShowID = tvShowID;
        this.bookID = bookID;
        this.audioBookID = audioBookID;
    }
    public void setId(String id) {
        this.id = id;
    }
}
