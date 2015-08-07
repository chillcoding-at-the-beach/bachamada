package com.heartrate.model;

public enum How {
    HAPPY(0), NEUTRAL(1), SAD(2);
    private int value;

    private How(int value) {
        this.value = value;
    }

    public int getInt() {
        return value;
    }
};   
