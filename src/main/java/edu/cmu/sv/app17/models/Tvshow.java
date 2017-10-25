package edu.cmu.sv.app17.models;



import java.util.List;

public class Tvshow {
    String id = null;
    String name;
    String genre;
    String level;
    String contributorId;
    public Tvshow(String name, String genre,
                 String level, String contributorId) {
        this.name = name;
        this.genre = genre;
        this.level = level;
        this.contributorId = contributorId;
    }
    public void setId(String id) {
        this.id = id;
    }
}
