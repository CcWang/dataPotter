package edu.cmu.sv.app17.models;
/*
* teacherName  string
* email     string
* password  string
* nativeLanguage    string
* phone     string
* gender    string
* */


public class Contributor {

    String id = null;
    String name, email, password, nativeLanguage, phone,gender;



    public Contributor(String name, String email, String password, String nativeLanguage,
                       String phone, String gender) {
        this.name = name;
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
