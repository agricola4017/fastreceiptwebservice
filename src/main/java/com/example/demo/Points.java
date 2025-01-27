package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Points {

    private int points;


    public Points(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}
