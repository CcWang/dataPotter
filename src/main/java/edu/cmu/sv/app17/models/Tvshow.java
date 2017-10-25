package edu.cmu.sv.app17.models;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tvshow {
    String id = null;
    String name;
    List<String> genre;
    List<String> level;
    //     HashMap level;
    String contributorId;
    public Tvshow(String name, List<String> genre,
                 List<String> level, String contributorId) {
        this.name = name;
        this.genre = genre;
        this.level = level;
        this.contributorId = contributorId;
    }
    public void setId(String id) {
        this.id = id;
    }
}
