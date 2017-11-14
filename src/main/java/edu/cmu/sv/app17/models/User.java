package edu.cmu.sv.app17.models;
/*
* username  string
* email     string
* password  string
* nativeLanguage    string
* englishLevel  Number
* phone     string
* gender    string
* birthday Date
* */
import java.util.Date;
public class User {

    String id = null;
    String username, email, password, nativeLanguage, phone,gender,birthday;
    Number englishLevel;



    public User(String username, String email, String password, String nativeLanguage,
                Number englishLevel, String phone, String gender, String birthday) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nativeLanguage = nativeLanguage;
        this.englishLevel = englishLevel;
        this.phone = phone;
        this.gender = gender;
        this.birthday = birthday;
    }
    public void setId(String id) {
        this.id = id;
    }
}