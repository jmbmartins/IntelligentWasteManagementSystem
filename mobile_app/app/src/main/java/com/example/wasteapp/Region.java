package com.example.wasteapp;

public class Region {
    private String name;
    private int id;

    public Region(String name) {
        this.name = name;
    }

    public Region(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}