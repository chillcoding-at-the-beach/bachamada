package com.heartrate.model;

public enum How {
    HAPPY(0), COOL(1), TONGUE(2), SAD(3);
    private int value;

    private How(int value) {
        this.value = value;
    }

    public int getInt() {
        return value;
    }
};   
