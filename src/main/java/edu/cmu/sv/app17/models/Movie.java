package edu.cmu.sv.app17.models;


import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Movie {
     String id = null;
     String name;
//     String genre;
     ArrayList<String> genre;
//     HashMap<String, String> level;
    org.bson.Document level;
//     String level;
     String contributorId;

    public Movie(String name, ArrayList<String> genre,
                 org.bson.Document level, String contributorId) {
        this.name = name;
        this.genre = genre;
        this.level = level;
        this.contributorId = contributorId;
    }
    public void setId(String id) {
        this.id = id;
    }
}
