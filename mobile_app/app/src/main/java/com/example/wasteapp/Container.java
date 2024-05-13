package com.example.wasteapp;


public class Container {
    private int id;
    private double fillLevel;
    private String timestamp;
    private double distance;

    public Container(int id, double fillLevel, String timestamp, double distance) {
        this.id = id;
        this.fillLevel = fillLevel;
        this.timestamp = timestamp;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public double getFillLevel() {
        return fillLevel;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Container ID: " + id + ", Fill Level: " + fillLevel + ", Timestamp: " + timestamp + ", Distance: " + distance;
    }
}