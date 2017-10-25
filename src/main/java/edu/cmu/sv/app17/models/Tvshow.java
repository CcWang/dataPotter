package edu.cmu.sv.app17.models;


public class Tvshow {
     String id = null;
     String name;
     String genre;
     Number level;
    public Tvshow(String name, String genre,
                  Number level) {
        this.name = name;
        this.genre = genre;
        this.level = level;
    }
    public void setId(String id) {
        this.id = id;
    }
}
