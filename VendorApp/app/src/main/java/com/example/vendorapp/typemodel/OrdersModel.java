package com.example.vendorapp.typemodel;

public class OrdersModel {
    String orderID;
    String orderTime;
    String cost;
    String orderStatus;
    String orderBy;
    String orderTo;
    String pid;
    String name;
    String total;
    String quantity;

    public OrdersModel() {
    }

    public OrdersModel(String orderID, String orderTime, String cost, String orderStatus, String orderBy, String orderTo, String pid, String name, String total, String quantity) {
        this.orderID = orderID;
        this.orderTime = orderTime;
        this.cost = cost;
        this.orderStatus = orderStatus;
        this.orderBy = orderBy;
        this.orderTo = orderTo;
        this.pid = pid;
        this.name = name;
        this.total = total;
        this.quantity = quantity;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderTo() {
        return orderTo;
    }

    public void setOrderTo(String orderTo) {
        this.orderTo = orderTo;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
