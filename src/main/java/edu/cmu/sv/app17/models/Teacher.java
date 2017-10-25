package edu.cmu.sv.app17.models;
/*
* teacherName  string
* email     string
* password  string
* nativeLanguage    string
* phone     string
* gender    string
* */


public class Teacher {

    String id = null;
    String teacherName, email, password, nativeLanguage, phone,gender;



    public Teacher(String teacherName, String email, String password, String nativeLanguage,
                   String phone, String gender) {
        this.teacherName = teacherName;
        this.email = email;
        this.password = password;
        this.nativeLanguage = nativeLanguage;
        this.phone = phone;
        this.gender = gender;
    }
    public void setId(String id) {
        this.id = id;
    }
}
