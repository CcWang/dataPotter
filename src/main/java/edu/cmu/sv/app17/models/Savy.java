package edu.cmu.sv.app17.models;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Savy {
    String id = null;
    String question;
    String answer01;
    String answer02;
    String answer03;
    String answer04;
    Number answer01count;
    Number answer02count;
    Number answer03count;
    Number answer04count;
//     Date created;

    //    List<String> genre
    public Savy(String question, String answer01, String answer02, String answer03, String answer04,
                 Number answer01count, Number answer02count, Number answer03count, Number answer04count) {
        this.question = question;
        this.answer01 = answer01;
        this.answer02 = answer02;
        this.answer03 = answer03;
        this.answer04 = answer04;
        this.answer01count = answer01count;
        this.answer02count = answer02count;
        this.answer03count = answer03count;
        this.answer04count = answer04count;

    }
    public void setId(String id) {
        this.id = id;
    }
}
