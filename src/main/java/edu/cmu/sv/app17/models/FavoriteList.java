package edu.cmu.sv.app17.models;

public class FavoriteList {

    String id = null;
    String userID;
    String movie;
    String tvShow;
    String book;
//    String audioBookID;

    public FavoriteList(String userID, String movie,
        String tvShow, String book){
        this.userID= userID;
        this.movie = movie;
        this.tvShow = tvShow;
        this.book = book;
    }
    public void setId(String id) {
        this.id = id;
    }
}
