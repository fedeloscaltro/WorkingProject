package com.gibbo.salvatore;

/**
 * Created by stefano.scaltriti on 25/02/2018.
 */

public class User extends Object{
    public String name, email, psswd, age;

    public User(String name, String email, String psswd, String age) {
        this.name = name;
        this.email = email;
        this.psswd = psswd;
        this.age = age;
    }

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
}
