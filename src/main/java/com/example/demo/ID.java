package com.example.demo;

//pojo for id
public class ID {
    private String id;

    public ID(String id) {
        this.id = id;
    }

    public ID(int id) {
        this.id = Integer.toString(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
