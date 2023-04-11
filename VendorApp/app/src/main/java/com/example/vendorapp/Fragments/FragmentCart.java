package com.example.vendorapp.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.vendorapp.Adapter.CartAdapter;
import com.example.vendorapp.Cart.DatabaseHelper;
import com.example.vendorapp.Cart.DatabaseManager;
import com.example.vendorapp.CartModel;
import com.example.vendorapp.Client.OrderDetails2;
import com.example.vendorapp.Constants;
import com.example.vendorapp.R;
import com.example.vendorapp.typemodel.ShopModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class FragmentCart extends Fragment {
    private static final String TAG = "Cart Fragment";

    public int orderCountv = 0;
    public String fee2;
    //public String orderCount;
    public double delivfee;
    public Double fee1;

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
    DatabaseReference databaseReference;
    TextView name, email, fee;
    String shopPhone, shoplongitude, shoplatitude, sname1;
    public String sid1;
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

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(sid1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ShopModel shopModel = snapshot.getValue(ShopModel.class);

                if (shopModel != null) {
                    sname1 = shopModel.getName();
                    // name.setText(sname1);
                    email.setText(shopModel.getEmail());
                    fee.setText(shopModel.getDeliveryfee());
                    shopPhone = shopModel.getPhone();
                    shoplongitude = shopModel.getLongitude();
                    shoplatitude = shopModel.getLatitude();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

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
                sid1 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SHOP_ID));

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

        btnorder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Cursor cursor = databaseManager.fetch1(sname1);
                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(sid1).child("Orders");

                Query query = FirebaseDatabase.getInstance().getReference("Users").child(sid1).child("Orders")
                        .orderByChild("orderBy").equalTo(firebaseAuth.getUid()).limitToLast(1);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                fee2 = snapshot1.child("delivery").getValue(String.class);
                                //orderCount = snapshot1.child("orderCount").getValue(String.class);
                                fee1 = Double.valueOf(fee2);
                                //int orderCounti = Integer.parseInt(orderCount);
                                orderCountv+=1;
                                delivfee = fee1 + 100;


                                //order logic
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
                                        hashMap.put("delivery", "" + delivfee);
                                        hashMap.put("deliveryStatus", "unpaid");
                                        hashMap.put("orderCount", "" + orderCountv);
                                        hashMap.put("orderStatus", "In Progress");
                                        hashMap.put("orderBy", "" + firebaseAuth.getUid());
                                        hashMap.put("orderTo", "" + shopID);

                                        //    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(shopID).child("Orders");
                                        databaseReference.child(timestamp).setValue(hashMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
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

                                                        //send notification after placing order successfully
                                                        prepareNotificationMessage(timestamp);

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(Exception e) {

                                            }

                                        });


                                    } while (cursor.moveToNext());
                                } else {
                                    Toast.makeText(getContext(), "Failed To place Order......", Toast.LENGTH_SHORT).show();
                                }
                                //end logic
                            }
                        } else {
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
                                    final int i = 100;
                                    hashMap.put("delivery", "" + i);
                                    hashMap.put("deliveryStatus", "unpaid");
                                    int i1 = 1;
                                    hashMap.put("orderCount", "" + i1);
                                    hashMap.put("orderStatus", "In Progress");
                                    hashMap.put("orderBy", "" + firebaseAuth.getUid());
                                    hashMap.put("orderTo", "" + shopID);

                                    databaseReference.child(timestamp).setValue(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
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

                                                    //send notification after placing order successfully
                                                    prepareNotificationMessage(timestamp);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {

                                        }

                                    });


                                } while (cursor.moveToNext());
                            } else {
                                Toast.makeText(getContext(), "Failed To place Order......", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        //create node if node doesn't exist at all
                        if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
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
                                    final int i = 100;
                                    hashMap.put("delivery", "" + i);
                                    hashMap.put("deliveryStatus", "unpaid");
                                    int i1 = 1;
                                    hashMap.put("orderCount", "" + i1);
                                    hashMap.put("orderStatus", "In Progress");
                                    hashMap.put("orderBy", "" + firebaseAuth.getUid());
                                    hashMap.put("orderTo", "" + shopID);

                                    databaseReference.child(timestamp).setValue(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
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

                                                    //send notification after placing order successfully
                                                    prepareNotificationMessage(timestamp);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {

                                        }

                                    });


                                } while (cursor.moveToNext());
                            } else {
                                Toast.makeText(getContext(), "Failed To place Order......", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                progressDialog.dismiss();
            }
        });
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

    //notification code
    private void prepareNotificationMessage(String orderId) {

        //prepare data for notification
        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;//must be same as subscribed by user
        String NOTIFICATION_TITLE = "New Order " + orderId;
        String NOTIFICATION_MESSAGE = "Congratulations....! You have new order.";
        String NOTIFICATION_TYPE = "NewOrder";

        //prepare json (what to send and where to send)
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        try {
            //what to send
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid", firebaseAuth.getUid());//client uid
            notificationBodyJo.put("sellerUid", shopID);
            notificationBodyJo.put("orderId", orderId);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);
            //where to send
            notificationJo.put("to", NOTIFICATION_TOPIC); //to all those subscribed to this topic
            notificationJo.put("data", notificationBodyJo);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
        }
        sendFcmNotification(notificationJo, orderId);

    }

    private void sendFcmNotification(JSONObject notificationJo, String orderId) {
        //send volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send",
                notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //after sending fcm start order details activity
                Intent intent = new Intent(getContext(), OrderDetails2.class);
                intent.putExtra("shopID", shopID);
                intent.putExtra("orderID", orderId);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //failed sending fcm, still start order details activity
                Intent intent = new Intent(getContext(), OrderDetails2.class);
                intent.putExtra("shopID", shopID);
                intent.putExtra("orderID", orderId);
                startActivity(intent);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //put required headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" + Constants.FCM_KEY);

                return headers;
            }
        };

        //enque the volley request
        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }
}
