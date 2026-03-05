package com.example.billards.Models;

public class BillardTable {
    public String id;
    public boolean isPlaying;
    public int number;
    public long startTime; // Khai báo kiểu long

    public BillardTable() { }

    public String getId() {
        return id;
    }

    public int getnumber() {
        return number;
    }

    public boolean getisPlaying() {
        return isPlaying;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
