package com.example.adityapandey.applogin.model;

import com.orm.SugarRecord;

public class Login extends SugarRecord {
    public int id;
    public String email,password;

    public Login(){

    }

    public Login(String email, String password){
        this.email = email;
        this.password = password;
    }
}
