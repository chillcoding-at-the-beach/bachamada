package fr.machada.bpm.pro.model;

import java.io.Serializable;

public class RegisteredBpm implements Serializable {

    private int mId = 0;
    private int mValue = 0;
    private long mDate = 0;
    private int mEffort = 0;
    private int mHow = 0;


    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public int getValue() {
        return mValue;
    }

    public long getDate() {
        return mDate;
    }

    public int getEffort() {
        return mEffort;
    }

    public int getHow() {
        return mHow;
    }

    public void setValue(int val) {
        mValue = val;
    }

    public void setDate(long date) {
        mDate = date;
    }

    public void setEffort(int effort) {
        mEffort = effort;
    }

    public void setHow(int how) {
        mHow = how;
    }


}
