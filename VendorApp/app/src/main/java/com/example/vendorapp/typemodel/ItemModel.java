package com.example.vendorapp.typemodel;

public class ItemModel {
    String itemname;
    String itemdesc;
    String price;
    String pid;
    String discount;
    String discountdescr;
    String discountPrice;
    String quantity;
    Boolean discountavailable;
    String timestamp;
    String uid;
    String image;

    public ItemModel() {
    }

    public ItemModel(String itemname, String itemdesc, String price, String pid, String discount, String discountdescr, String discountPrice, String quantity, Boolean discountavailable, String timestamp, String uid, String image) {
        this.itemname = itemname;
        this.itemdesc = itemdesc;
        this.price = price;
        this.pid = pid;
        this.discount = discount;
        this.discountdescr = discountdescr;
        this.discountPrice = discountPrice;
        this.quantity = quantity;
        this.discountavailable = discountavailable;
        this.timestamp = timestamp;
        this.uid = uid;
        this.image = image;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getItemdesc() {
        return itemdesc;
    }

    public void setItemdesc(String itemdesc) {
        this.itemdesc = itemdesc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscountdescr() {
        return discountdescr;
    }

    public void setDiscountdescr(String discountdescr) {
        this.discountdescr = discountdescr;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Boolean getDiscountavailable() {
        return discountavailable;
    }

    public void setDiscountavailable(Boolean discountavailable) {
        this.discountavailable = discountavailable;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
