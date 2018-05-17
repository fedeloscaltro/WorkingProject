package com.gibbo.salvatore;

import java.util.ArrayList;

/**
 * Created by federico.scaltriti on 25/02/2018.
 */

public class User extends Object{
    public String name, email, psswd, age, gender, sede;

    public User(String name, String email, String psswd, String age, String gender) {
        this.gender = gender;
        this.name = name;
        this.email = email;
        this.psswd = psswd;
        this.age = age;
    }

    public User(String name, String email, String password, String sede){
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSede() { return sede; }

    public void setSede(String sede) { this.sede = sede; }

}
