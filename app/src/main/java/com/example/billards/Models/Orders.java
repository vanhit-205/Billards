package com.example.billards.Models;

public class Orders {
    private int tableID;
    private int price;
    private int quantity;

    public Orders(int tableID, int price, int quantity) {
        this.tableID = tableID;
        this.price = price;
        this.quantity = quantity;
    }

    public int getTableID() {
        return tableID;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getItemTotal() {
        return price * quantity;
    }
}
