package com.example.adityapandey.applogin.model;

import com.orm.SugarRecord;

public class User extends SugarRecord {
    public Login userid;
    public String name;
    public int sports,research,tech,entertainment;
    public String startdate,enddate;

    public User(){

    }

    public User(Login user, String name, boolean sports, boolean research, boolean tech, boolean entertainment, String startdate, String enddate){
        this.userid = user;
        this.name = name;
        this.sports = sports?1:0;
        this.research = research?1:0;
        this.tech = tech?1:0;
        this.entertainment = entertainment?1:0;
        this.startdate = startdate;
        this.enddate = enddate;
    }
}
