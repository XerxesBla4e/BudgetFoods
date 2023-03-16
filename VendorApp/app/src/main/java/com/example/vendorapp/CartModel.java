package com.example.vendorapp;

public class CartModel {
    public int ID;
    public String ITEM;
    public int QUANTITY;
    public String ITEM_IMAGE;
    public int TOTAL_AMOUNT;
    public String PRODUCT_ID;
    public String SHOP_ID;


    public CartModel() {

    }

    public CartModel(int ID, String ITEM, int QUANTITY, String ITEM_IMAGE, int TOTAL_AMOUNT, String PRODUCT_ID, String SHOP_ID) {
        this.ID = ID;
        this.ITEM = ITEM;
        this.QUANTITY = QUANTITY;
        this.ITEM_IMAGE = ITEM_IMAGE;
        this.TOTAL_AMOUNT = TOTAL_AMOUNT;
        this.PRODUCT_ID = PRODUCT_ID;
        this.SHOP_ID = SHOP_ID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getITEM() {
        return ITEM;
    }

    public void setITEM(String ITEM) {
        this.ITEM = ITEM;
    }

    public int getQUANTITY() {
        return QUANTITY;
    }

    public void setQUANTITY(int QUANTITY) {
        this.QUANTITY = QUANTITY;
    }

    public String getITEM_IMAGE() {
        return ITEM_IMAGE;
    }

    public void setITEM_IMAGE(String ITEM_IMAGE) {
        this.ITEM_IMAGE = ITEM_IMAGE;
    }

    public int getTOTAL_AMOUNT() {
        return TOTAL_AMOUNT;
    }

    public void setTOTAL_AMOUNT(int TOTAL_AMOUNT) {
        this.TOTAL_AMOUNT = TOTAL_AMOUNT;
    }

    public String getPRODUCT_ID() {
        return PRODUCT_ID;
    }

    public void setPRODUCT_ID(String PRODUCT_ID) {
        this.PRODUCT_ID = PRODUCT_ID;
    }

    public String getSHOP_ID() {
        return SHOP_ID;
    }

    public void setSHOP_ID(String SHOP_ID) {
        this.SHOP_ID = SHOP_ID;
    }
}