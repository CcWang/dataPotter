package edu.cmu.sv.app17.models;


public class Book {
    String id = null;
    String name;
    String genre;
    Number level;

    public Book(String name,
                String genre, Number level) {
        this.name = name;
        this.genre = genre;
        this.level = level;
    }

    public void setId(String id) {
        this.id = id;
    }
}
