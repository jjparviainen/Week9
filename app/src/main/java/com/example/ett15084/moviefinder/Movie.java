package com.example.ett15084.moviefinder;

public class Movie {

    String name;
    String time;

    public Movie (String n, String i){
        name = n;
        time = i;

    }

    public String getTime(){
        return time;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return name;
    }

}
