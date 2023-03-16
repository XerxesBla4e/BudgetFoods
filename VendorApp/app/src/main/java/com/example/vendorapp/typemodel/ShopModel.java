package com.example.vendorapp.typemodel;

public class ShopModel {
    String uid;
    String email;
    String name;
    String phone;
    String address;
    String city;
    String state;
    String country;
    String district;
    String market;
    String deliveryfee;
    String latitude;
    String longitude;
    String timestamp;
    String accounttype;
    String online;
    String image;


    public ShopModel() {
    }

    public ShopModel(String uid, String email, String name, String phone, String address, String city, String state, String country, String district, String market, String deliveryfee, String latitude, String longitude, String timestamp, String accounttype, String online, String image) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.district = district;
        this.market = market;
        this.deliveryfee = deliveryfee;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.accounttype = accounttype;
        this.online = online;
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getDeliveryfee() {
        return deliveryfee;
    }

    public void setDeliveryfee(String deliveryfee) {
        this.deliveryfee = deliveryfee;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAccounttype() {
        return accounttype;
    }

    public void setAccounttype(String accounttype) {
        this.accounttype = accounttype;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
