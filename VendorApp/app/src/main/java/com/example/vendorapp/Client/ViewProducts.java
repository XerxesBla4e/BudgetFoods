package com.example.vendorapp.Client;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.vendorapp.Adapter.CartAdapter;
import com.example.vendorapp.Adapter.ItemAdapter;
import com.example.vendorapp.Adapter.RetrieveAdapter;
import com.example.vendorapp.Cart.DatabaseHelper;
import com.example.vendorapp.Cart.DatabaseManager;
import com.example.vendorapp.CartModel;
import com.example.vendorapp.Constants;
import com.example.vendorapp.R;
import com.example.vendorapp.typemodel.ItemModel;
import com.example.vendorapp.typemodel.ShopModel;
import com.example.vendorapp.typemodel.UserDetsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewProducts extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference databaseReference;
    //String shopID;
    TextView name, email, fee, opclse, cartcount;
    ImageView call, find, cart, back;
    String shopPhone, shoplongitude, shoplatitude, clientlongitude, clientlatitude;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String Id1;
    public String sname1;
    public String productID, shopID;

    public int count3;

    RecyclerView recyclerView;
    RetrieveAdapter retrieveAdapter;

    //Declare popup views
    public DatabaseManager databaseManager;
    public ProgressDialog progressDialog;
    public RecyclerView recyclerView1;
    public CartAdapter cartAdapter;
    public List<CartModel> cartModelList;
    public int totalPrices = 0;
    public HashMap<String, Object> hashMap1;
    //end declaration of popup views

    //logictest
    public int totalv = 0;
    public int orderCountv = 0;
    public String fee2;
    public String orderCount;
    public double delivfee;
    public Double fee1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);

        hooks();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        Id1 = user.getUid();

        call.setOnClickListener(this);
        find.setOnClickListener(this);
        cart.setOnClickListener(this);
        back.setOnClickListener(this);

        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar2);
        //  setSupportActionBar(toolbar);
        //  toolbar.setTitle("Products");

        if (getIntent() != null) {
            shopID = getIntent().getStringExtra("Shopid");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(shopID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ShopModel shopModel = snapshot.getValue(ShopModel.class);

                if (shopModel != null) {
                    sname1 = shopModel.getName();
                    name.setText(sname1);
                    email.setText(shopModel.getEmail());
                    fee.setText(shopModel.getDeliveryfee());
                    shopPhone = shopModel.getPhone();
                    shoplongitude = shopModel.getLongitude();
                    shoplatitude = shopModel.getLatitude();

                    if (shopModel.getOnline().equals("true")) {
                        opclse.setVisibility(View.GONE);
                    } else {
                        //opclse.setVisibility(View.VISIBLE);
                        // opclse.setText("open");
                        // opclse.setBackgroundColor(getBaseContext().getResources().getColor(R.color.green_pie));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ItemModel> options =
                new FirebaseRecyclerOptions.Builder<ItemModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Users").child(shopID).child("Products"), ItemModel.class)
                        .build();

        retrieveAdapter = new RetrieveAdapter(getApplicationContext(), options);
        recyclerView.setAdapter(retrieveAdapter);
        retrieveAdapter.notifyDataSetChanged();

        FirebaseDatabase.getInstance().getReference("Users").child(Id1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserDetsModel userDetsModel = snapshot.getValue(UserDetsModel.class);
                if (userDetsModel != null) {
                    clientlongitude = userDetsModel.getLongitude();
                    clientlatitude = userDetsModel.getLatitude();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //initialize popup views
        progressDialog = new ProgressDialog(ViewProducts.this);

        databaseManager = new DatabaseManager(getApplicationContext());
        try {
            databaseManager.open();
        } catch (SQLDataException e) {
            e.printStackTrace();
        }

    }


    private void hooks() {
        name = findViewById(R.id.name1);
        email = findViewById(R.id.email1);
        fee = findViewById(R.id.delfee);
        opclse = findViewById(R.id.openclse);
        recyclerView = findViewById(R.id.prods1);
        call = findViewById(R.id.phone2);
        find = findViewById(R.id.find2);
        cart = findViewById(R.id.cart);
        back = findViewById(R.id.bk4);
        cartcount = findViewById(R.id.counter);
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        retrieveAdapter.stopListening();
    }
/*
    public boolean onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.search2);

        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone2:
                dialPhone();
                break;
            case R.id.find2:
                openMap();
                break;
            case R.id.cart:
                viewCart();
                break;
            case R.id.bk4:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void viewCart() {
        final Dialog dialog = new Dialog(ViewProducts.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.cartpopup);

        TextView totalamt;
        Button order;

        order = dialog.findViewById(R.id.btnodr);
        totalamt = dialog.findViewById(R.id.total);

        //show view cart
        recyclerView1 = dialog.findViewById(R.id.cartable);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        cartModelList = new ArrayList<>();

        Cursor cursor1 = databaseManager.fetch1(sname1);

        if (cursor1.moveToFirst()) {
            do {
                int _id = cursor1.getInt(cursor1.getColumnIndex(DatabaseHelper.ID));
                String nme = cursor1.getString(cursor1.getColumnIndex(DatabaseHelper.ITEM));
                int qty = cursor1.getInt(cursor1.getColumnIndex(DatabaseHelper.QUANTITY));
                String img = cursor1.getString(cursor1.getColumnIndex(DatabaseHelper.ITEM_IMAGE));
                int total1 = cursor1.getInt(cursor1.getColumnIndex(DatabaseHelper.TOTAL_AMOUNT));
                String pid1 = cursor1.getString(cursor1.getColumnIndex(DatabaseHelper.PRODUCT_ID));
                String sid1 = cursor1.getString(cursor1.getColumnIndex(DatabaseHelper.SHOP_ID));

                cartModelList.add(new CartModel(_id, nme, qty, img, total1, pid1, sid1));
            } while (cursor1.moveToNext());

            for (int i = 0; i < cartModelList.size(); i++) {
                totalPrices += cartModelList.get(i).getTOTAL_AMOUNT();
            }
            //set number of items in cart
            count3 = cartModelList.size();
            cartcount.setText(count3 + "");
            totalamt.setText("Total Amount:" + totalPrices + "");

            cartAdapter = new CartAdapter(getApplicationContext(), cartModelList);
            recyclerView1.setAdapter(cartAdapter);
            cartAdapter.notifyDataSetChanged();

        } else {
            Toast.makeText(getApplicationContext(), "No Items In Cart", Toast.LENGTH_SHORT).show();
        }
        //end view cart

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = databaseManager.fetch1(sname1);
                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(shopID).child("Orders");

                Query query = FirebaseDatabase.getInstance().getReference("Users").child(shopID).child("Orders")
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
                                orderCountv += 1;
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
                                                        Toast.makeText(getApplicationContext(), "Order Placed Successfully......", Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(getApplicationContext(), "Failed To place Order......", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(getApplicationContext(), "Order Placed Successfully......", Toast.LENGTH_SHORT).show();

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
                                Toast.makeText(getApplicationContext(), "Failed To place Order......", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(getApplicationContext(), "Order Placed Successfully......", Toast.LENGTH_SHORT).show();

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
                                Toast.makeText(getApplicationContext(), "Failed To place Order......", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openMap() {
        String address2 = "https://www.google.com/maps/search/?api=1&query=" + clientlatitude + "," + clientlongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address2));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(shopPhone))));
        Toast.makeText(getApplicationContext(), "" + shopPhone, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(getApplicationContext(), OrderDetails2.class);
                intent.putExtra("shopID", shopID);
                intent.putExtra("orderID", orderId);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //failed sending fcm, still start order details activity
                Intent intent = new Intent(getApplicationContext(), OrderDetails2.class);
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
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}