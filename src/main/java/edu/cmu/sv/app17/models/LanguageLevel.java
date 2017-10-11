package edu.cmu.sv.app17.models;

public class LanguageLevel {

    String id = null;
    String usersId;
    Number movies_level, tvshows_level, books_level, audioBooks_level;

    public LanguageLevel(String usersId, Number movies_level, Number tvshows_level, Number books_level,
                         Number audioBooks_level) {
        this.usersId = usersId;
        this.movies_level = movies_level;
        this.tvshows_level = tvshows_level;
        this.books_level = books_level;
        this.audioBooks_level = audioBooks_level;
    }
    public void setId(String id) {
        this.id = id;
    }
}
