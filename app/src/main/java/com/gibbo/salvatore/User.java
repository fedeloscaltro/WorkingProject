package com.gibbo.salvatore;

/**
 * Created by federico.scaltriti on 25/02/2018.
 */

public class User extends Object{
    private String name, email, psswd, birthday, gender, sede;

    User(String name, String email, String psswd, String birthday, String gender) {
        this.gender = gender;
        this.name = name;
        this.email = email;
        this.psswd = psswd;
        this.birthday = birthday;
    }

    User(String name, String email, String password, String sede){
        this.name = name;
        this.email = email;
        this.psswd = password;
        this.sede = sede;
    }

    public User(){

    }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPsswd() {
        return psswd;
    }

    public void setPsswd(String psswd) {
        this.psswd = psswd;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String age) {
        this.birthday = age;
    }

    public String getSede() { return sede; }

    public void setSede(String sede) { this.sede = sede; }

}
