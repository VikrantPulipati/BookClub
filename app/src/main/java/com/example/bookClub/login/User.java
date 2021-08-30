package com.example.bookClub.login;

public class User {

    String username;
    String password;

    boolean isRegistered;

    public User () {

    }

    public User (String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername () {
        return this.username;
    }

    public String getPassword () {
        return this.password;
    }

    public String toString () {
        return this.username + ", " + this.password;
    }

    public boolean getIsRegistered () {
        return this.isRegistered;
    }

    public void setIsRegistered (boolean b) {
        this.isRegistered = b;
    }
}
