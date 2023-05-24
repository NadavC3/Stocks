package com.example.stock2;

public class StockData {
    private double price;
    private double change;

    public StockData(double price, double change) {
        this.price = price;
        this.change = change;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }
}
