package edu.cmu.sv.app17.models;



import java.util.List;

public class Tvshow {
    String id = null;
    String name;
    String genre;
    Number level;
    String contributorId;
    Number tvid;
    public Tvshow(String name, String genre,
                 Number level, String contributorId, Number tvid) {
        this.name = name;
        this.genre = genre;
        this.level = level;
        this.contributorId = contributorId;
        this.tvid = tvid;
    }
    public void setId(String id) {
        this.id = id;
    }
}
