package edu.cmu.sv.app17.models;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Movie {
     String id = null;
     String name;
     String genre;
     Number level;
     String contributorId;
//    List<String> genre
    public Movie(String name, String genre,
                 Number level, String contributorId) {
        this.name = name;
        this.genre = genre;
        this.level = level;
        this.contributorId = contributorId;
    }
    public void setId(String id) {
        this.id = id;
    }
}
