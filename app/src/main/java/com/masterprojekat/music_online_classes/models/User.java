package com.masterprojekat.music_online_classes.models;

public class User {
    private String name;
    private String surname;
    private String username;
    private String password;
    private String date;
    private String email;
    private String phoneNumber;
    private String type;
    private String education;
    private String expertise;
    private String accountStatus;

    public User() {
        this.name = "";
        this.surname = "";
        this.username = "";
        this.password = "";
        this.date = "";
        this.email = "";
        this.phoneNumber = "";
        this.type = "";
        this.education = "";
        this.expertise = "";
        this.accountStatus = "";
    }

    public User(String name, String surname, String username, String password, String date, String email,String phoneNumber, String type, String education, String expertise, String accountStatus) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.date = date;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.education = education;
        this.expertise = expertise;
        this.accountStatus = accountStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}
