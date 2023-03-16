package com.example.vendorapp.Cart;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "Carttable7";
    static final int DB_VERSION = 5;

    static final String TABLE_NAME = "Products";
    public static final String ID = "_id";
    public static final String ITEM = "item";
    public static final String QUANTITY = "quantity";
    public static final String ITEM_IMAGE = "image_url";
    public static final String TOTAL_AMOUNT = "total";
    public static final String PRODUCT_ID = "ProductId";
    public static final String SHOP_ID = "ShopId";
    public static final String SHOP_NAME = "ShopName";

    static final String CREATE_DB_QUERY = "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PRODUCT_ID + " TEXT NOT NULL, " +
            ITEM + " TEXT NOT NULL, " + QUANTITY + " INTEGER NOT NULL, " + TOTAL_AMOUNT + " INTEGER NOT NULL, " + ITEM_IMAGE + " TEXT , "
            + SHOP_ID + " TEXT NOT NULL, " + SHOP_NAME + " TEXT NOT NULL " + ");";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
