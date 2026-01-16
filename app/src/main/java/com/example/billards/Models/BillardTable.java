package com.example.billards.Models;

public class BillardTable {
    private String id;
    private int number;
    private long startTime;
    private boolean isPlay;
    public BillardTable(){}

    public String getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean isPlay() {
        return isPlay;
    }

}
