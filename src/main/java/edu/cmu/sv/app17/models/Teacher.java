package edu.cmu.sv.app17.models;
/*
* teacherName  string
* email     string
* password  string
* nativeLanguage    string
* phone     string
* gender    string
* year of teaching experience  Number
* open to accept new student Boolean
* */
import java.util.Date;

public class Teacher {

    String id = null;
    String teacherName, email, password, nativeLanguage, phone,gender;
    Number exp;
    Boolean newStudent;


    public Teacher(String teacherName, String email, String password, String nativeLanguage,
                   String phone, String gender, Number exp, Boolean newStudent) {
        this.teacherName = teacherName;
        this.email = email;
        this.password = password;
        this.nativeLanguage = nativeLanguage;
        this.phone = phone;
        this.gender = gender;
        this.exp = exp;
        this.newStudent = newStudent;
    }
    public void setId(String id) {
        this.id = id;
    }
}
