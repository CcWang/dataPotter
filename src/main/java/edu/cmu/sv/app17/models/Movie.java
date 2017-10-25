package edu.cmu.sv.app17.models;


import java.util.ArrayList;
import java.util.List;

public class Movie {
     String id = null;
     String name;
     ArrayList genre;
     Number level;
    public Movie(String name, ArrayList genre,
                 Number level) {
        this.name = name;
        this.genre = genre;
        this.level = level;
    }
    public void setId(String id) {
        this.id = id;
    }
}
