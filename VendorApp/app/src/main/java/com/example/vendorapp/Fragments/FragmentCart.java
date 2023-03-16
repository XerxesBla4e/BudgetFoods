package com.example.vendorapp.Fragments;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.Adapter.CartAdapter;
import com.example.vendorapp.Cart.DatabaseHelper;
import com.example.vendorapp.Cart.DatabaseManager;
import com.example.vendorapp.CartModel;
import com.example.vendorapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class FragmentCart extends Fragment {
    private static final String TAG = "Cart Fragment";

    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    List<CartModel> cartModelList;
    DatabaseManager databaseManager;
    Button btnorder;
    TextView totalamt;
    public String shopID;
    public int totalPrices = 0;
    public String productID;
    public FirebaseAuth firebaseAuth;
    public ProgressDialog progressDialog;
    HashMap<String, Object> hashMap1;
    public String orderId;
    //final String timestamp = "" + System.currentTimeMillis();


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cartrecycler, container, false);

        progressDialog = new ProgressDialog(getContext());

        firebaseAuth = FirebaseAuth.getInstance();

        databaseManager = new DatabaseManager(getContext());
        try {
            databaseManager.open();
        } catch (SQLDataException e) {
            e.printStackTrace();
        }

        btnorder = (Button) view.findViewById(R.id.btnodr);
        totalamt = (TextView) view.findViewById(R.id.total);

        btnorder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Cursor cursor = databaseManager.fetch();

                if (cursor.moveToFirst()) {
                    do {
                        final String timestamp = "" + System.currentTimeMillis();

                        productID = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRODUCT_ID));
                        shopID = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SHOP_ID));

                        progressDialog.setMessage("Processing Order");
                        progressDialog.show();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("orderID", "" + timestamp);
                        hashMap.put("orderTime", "" + timestamp);
                        hashMap.put("cost", "" + totalPrices);
                        hashMap.put("orderStatus", "In Progress");
                        hashMap.put("orderBy", "" + firebaseAuth.getUid());
                        hashMap.put("orderTo", "" + shopID);


                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(shopID).child("Orders");
                        databaseReference.child(timestamp).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<java.lang.Void>() {
                                    @java.lang.Override
                                    public void onSuccess(java.lang.Void aVoid) {
                                        for (int i = 0; i < cartModelList.size(); i++) {
                                            String pid = cartModelList.get(i).getPRODUCT_ID();
                                            String name = cartModelList.get(i).getITEM();
                                            int total = cartModelList.get(i).getTOTAL_AMOUNT();
                                            int quantity = cartModelList.get(i).getQUANTITY();

                                            hashMap1 = new HashMap<>();
                                            hashMap1.put("pid", pid);
                                            hashMap1.put("name", name);
                                            hashMap1.put("total", total);
                                            hashMap1.put("quantity", quantity);

                                            databaseReference.child(timestamp).child("Items").setValue(hashMap1);
                                        }
                                        databaseManager.delete();
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Order Placed Successfully......", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @java.lang.Override
                            public void onFailure(@NonNull java.lang.Exception e) {

                            }

                        });

                    } while (cursor.moveToNext());

                } else {
                    Toast.makeText(getContext(), "Failed To place Order......", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //cart products show
        recyclerView = view.findViewById(R.id.cartable);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartModelList = new ArrayList<CartModel>();

        Cursor cursor = databaseManager.fetch();
        if (cursor.moveToFirst()) {
            do {
                int _id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID));
                String nme = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEM));
                int qty = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.QUANTITY));
                String img = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEM_IMAGE));
                int total1 = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TOTAL_AMOUNT));
                String pid1 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRODUCT_ID));
                String sid1 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SHOP_ID));

                cartModelList.add(new CartModel(_id, nme, qty, img, total1, pid1, sid1));

            } while (cursor.moveToNext());

            for (int i = 0; i < cartModelList.size(); i++) {
                totalPrices += cartModelList.get(i).getTOTAL_AMOUNT();
            }
            totalamt.setText("Total Amount:" + totalPrices + "");

            cartAdapter = new CartAdapter(getContext(), cartModelList);
            recyclerView.setAdapter(cartAdapter);
            cartAdapter.notifyDataSetChanged();

        } else {
            Toast.makeText(getContext(), "No Items In Cart", Toast.LENGTH_SHORT).show();
        }

        // btnorder.se
      /*   btnorder.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 // List<Orders> orderlist = new ArrayList<Orders>();
                 Cursor cursor = databaseManager.fetch();
                 if (cursor.moveToFirst()) {
                     do {
                         // String quantity = cursor.getString(cursor.getColumnIndex(DatabaseHelper.QUANTITY));
                         //String item = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEM));
                         //String total1 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TOTAL_AMOUNT));
                         productID = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRODUCT_ID));
                         shopID = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SHOP_ID));

                         // orderlist.add(new Orders(item, quantity, total1, productID));

                         //start
                         //end

                     }
                     while (cursor.moveToNext());*/

        return view;


    }
}
