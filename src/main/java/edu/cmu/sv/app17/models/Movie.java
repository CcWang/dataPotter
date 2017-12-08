package edu.cmu.sv.app17.models;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Movie {
     String id = null;
     String name;
     String genre;
     Number level;
     String contributorId;
     Number movieid;
//     Date created;

//    List<String> genre
    public Movie(String name, String genre,
                 Number level, String contributorId, Number movieid) {
        this.name = name;
        this.genre = genre;
        this.level = level;
        this.contributorId = contributorId;
        this.movieid = movieid;

    }
    public void setId(String id) {
        this.id = id;
    }
}
