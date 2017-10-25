package edu.cmu.sv.app17.models;


public class Book {
    String id = null;
    String name;
    String genre;
    Number level;
    String contributorId;

    public Book(String name,
                String genre, Number level, String teacherId) {
        this.name = name;
        this.genre = genre;
        this.level = level;
        this.contributorId = teacherId;
    }

    public void setId(String id) {
        this.id = id;
    }
}
