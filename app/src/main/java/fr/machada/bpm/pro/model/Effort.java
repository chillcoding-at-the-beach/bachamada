package fr.machada.bpm.pro.model;

public enum Effort {
    GURU(0), WALKING(1), INTERVAL(2), EXERCISE(3);
    private int value;

    private Effort(int value) {
        this.value = value;
    }

    public int getInt() {
        return value;
    }
};   
