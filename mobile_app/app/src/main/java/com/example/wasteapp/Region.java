package com.example.wasteapp;

public class Region {
    private String name;

    public Region(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}