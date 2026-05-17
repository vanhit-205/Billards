package com.example.billards.Models;

public class Payment {
    private String id;
    private int table;
    private long time;
    private long timePlay;
    private double price;
    private String paymentMethod; // "cash" or "vnpay"
    private String status;        // "completed", "pending", "failed"
    private double tablePrice;
    private double foodPrice;

    public Payment() {
        // Required for Firestore
    }

    public Payment(String id, int table, long time, long timePlay, double price, String paymentMethod, String status) {
        this.id = id;
        this.table = table;
        this.time = time;
        this.timePlay = timePlay;
        this.price = price;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.tablePrice = 0.0;
        this.foodPrice = 0.0;
    }

    public Payment(String id, int table, long time, long timePlay, double price, String paymentMethod, String status, double tablePrice, double foodPrice) {
        this.id = id;
        this.table = table;
        this.time = time;
        this.timePlay = timePlay;
        this.price = price;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.tablePrice = tablePrice;
        this.foodPrice = foodPrice;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getTable() { return table; }
    public void setTable(int table) { this.table = table; }

    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }

    public long getTimePlay() { return timePlay; }
    public void setTimePlay(long timePlay) { this.timePlay = timePlay; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTablePrice() { return tablePrice; }
    public void setTablePrice(double tablePrice) { this.tablePrice = tablePrice; }

    public double getFoodPrice() { return foodPrice; }
    public void setFoodPrice(double foodPrice) { this.foodPrice = foodPrice; }
}
