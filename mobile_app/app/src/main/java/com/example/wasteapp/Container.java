package com.example.wasteapp;

public class Container {
    private double distance;
    private int fillLevel;

    public Container(double distance, int fillLevel) {
        this.distance = distance;
        this.fillLevel = fillLevel;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getFillLevel() {
        return fillLevel;
    }

    public void setFillLevel(int fillLevel) {
        this.fillLevel = fillLevel;
    }

    @Override
    public String toString() {
        return "Distance: " + distance + "km, Fill Level: " + fillLevel + "%";
    }
}
