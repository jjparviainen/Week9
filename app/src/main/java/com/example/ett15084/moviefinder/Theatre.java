package com.example.ett15084.moviefinder;

import java.util.ArrayList;

public class Theatre {

    String ID;
    String name;

    public Theatre(String n, String i){
        name = n;
        ID = i;

    }

    public String getID(){
        return ID;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return name;
    }
}
