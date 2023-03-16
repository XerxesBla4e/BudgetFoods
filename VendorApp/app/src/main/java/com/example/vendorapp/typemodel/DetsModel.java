package com.example.vendorapp.typemodel;

public class DetsModel {

    String name;
    String pid;
    int quantity;
    int total;

    public DetsModel() {
    }

    public DetsModel(String name, String pid, int quantity, int total) {
        this.name = name;
        this.pid = pid;
        this.quantity = quantity;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
