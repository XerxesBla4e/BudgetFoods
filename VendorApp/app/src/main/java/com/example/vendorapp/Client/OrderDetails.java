package com.example.vendorapp.Client;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.vendorapp.Adapter.ItemsAdapter;
import com.example.vendorapp.Constants;
import com.example.vendorapp.R;
import com.example.vendorapp.typemodel.DetsModel;
import com.example.vendorapp.typemodel.OrdersModel;
import com.example.vendorapp.typemodel.UserDetsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetails extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    ItemsAdapter itemsAdapter;

    private static final String TAG = "Orders Details";

    private TextView name, OrderId, status, cost;
    String orderID;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public String telno, name1, id, clientlatitude, clientlongitude, shoplongitude, shoplatitude;
    public int total;
    public int quantity;
    ImageView imgdel, imgedit, phone3, find3;
    public String orderBy;
    List<DetsModel> detsModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        hooks();

        recyclerView = findViewById(R.id.prods3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        imgedit.setOnClickListener(this);
        imgdel.setOnClickListener(this);
        phone3.setOnClickListener(this);
        find3.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        /*
        firebaseUser = firebaseAuth.getCurrentUser();
        shopID = firebaseUser.getUid();
*/
        final Intent intent3 = getIntent();
        orderID = intent3.getStringExtra("orderID");
        orderBy = intent3.getStringExtra("orderBy");

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").
                child(firebaseAuth.getUid()).child("Orders");
        databaseReference.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                OrdersModel ordersModel = snapshot.getValue(OrdersModel.class);
                if (ordersModel != null) {
                    cost.setText("Total:" + ordersModel.getCost());
                    OrderId.setText(ordersModel.getOrderID());
                    //orderBy = ordersModel.getOrderBy();
                    //  clientID = String.valueOf(ordersModel.getOrderBy());
                    status.setText(ordersModel.getOrderStatus());

                    if (status.equals("In Progress")) {
                        status.setText(ordersModel.getOrderStatus());
                        status.setTextColor(getApplicationContext().getResources().getColor(R.color.light_green));
                    } else if (status.equals("Cancelled")) {
                        status.setText(ordersModel.getOrderStatus());
                        status.setTextColor(getApplicationContext().getResources().getColor(R.color.red));
                    } else if (status.equals("Confirmed")) {
                        status.setText(ordersModel.getOrderStatus());
                        status.setTextColor(getApplicationContext().getResources().getColor(R.color.green_pie));
                    }


                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(ordersModel.getOrderBy())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    UserDetsModel userDetsModel = snapshot.getValue(UserDetsModel.class);
                                    if (userDetsModel != null) {
                                        name.setText(userDetsModel.getName());
                                        telno = userDetsModel.getPhone();
                                        clientlatitude = userDetsModel.getLatitude();
                                        clientlongitude = userDetsModel.getLongitude();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Orders");
        databaseReference.child(orderID).child("Items").addValueEventListener(new ValueEventListener() {

            /* @Override
         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(shopID).child("Orders");
         databaseReference.child(orderID).child("Items").addListenerForSingleValueEvent(new ValueEventListener() {*/
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DetsModel detsModel = snapshot.getValue(DetsModel.class);

                detsModelList = new ArrayList<>();

                name1 = detsModel.getName();
                id = detsModel.getPid();
                quantity = detsModel.getQuantity();
                total = detsModel.getTotal();

                detsModelList.add(new DetsModel(name1, id, quantity, total));
                itemsAdapter = new

                        ItemsAdapter(detsModelList, getApplicationContext());
                recyclerView.setAdapter(itemsAdapter);
                itemsAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserDetsModel userDetsModel = snapshot.getValue(UserDetsModel.class);
                if (userDetsModel != null) {
                    shoplongitude = userDetsModel.getLongitude();
                    shoplatitude = userDetsModel.getLatitude();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hooks() {
        name = findViewById(R.id.name2);
        OrderId = findViewById(R.id.email2);
        status = findViewById(R.id.status);
        cost = findViewById(R.id.cost);
        imgdel = findViewById(R.id.deleteorder);
        imgedit = findViewById(R.id.editorder);
        phone3 = findViewById(R.id.phone3);
        find3 = findViewById(R.id.find3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleteorder:

                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Are You Sure");
                builder.setMessage("No Return After this");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteOrder();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Deletion Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                break;
            case R.id.editorder:
                final String[] status3 = {"In Progress", "Confirmed", "Cancelled"};
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(OrderDetails.this);
                mBuilder.setTitle("Update Order Status");
                mBuilder.setSingleChoiceItems(status3, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int xer) {
                        if (xer == 0) {
                            String Message = "In Progress";
                            updateOrderStatus(Message);
                            status.setTextColor(getBaseContext().getResources().getColor(R.color.light_green));
                        } else if (xer == 1) {
                            String Message = "Confirmed";
                            updateOrderStatus(Message);
                            status.setTextColor(getBaseContext().getResources().getColor(R.color.green_pie));
                        } else if (xer == 2) {
                            String Message = "Cancelled";
                            updateOrderStatus(Message);
                            status.setTextColor(getBaseContext().getResources().getColor(R.color.red));
                        }
                        dialog.dismiss();
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mBuilder.show();
                break;
            case R.id.phone3:
                makeCall();
                break;
            case R.id.find3:
                locateuser();
                break;
        }
    }

    private void deleteOrder() {
        FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid()).child("Orders")
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Order Record Deleted Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(OrderDetails.this, MainClient.class));
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateOrderStatus(String message) {
        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("orderStatus", message);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Orders").child(orderID).updateChildren(hashMap2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Order is now" + message, Toast.LENGTH_SHORT).show();
                        prepareNotificationMessage(orderID, message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void locateuser() {
        String address2 = "https://www.google.com/maps/search/?api=1&query="+shoplatitude+","+shoplongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address2));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void makeCall() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(telno))));
        Toast.makeText(getApplicationContext(), "" + telno, Toast.LENGTH_SHORT).show();
    }

    private void prepareNotificationMessage(String orderId, String message) {

        //prepare data for notification
        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;//must be same as subscribed by user
        String NOTIFICATION_TITLE = "Your Order " + orderId;
        String NOTIFICATION_MESSAGE = "" + message;
        String NOTIFICATION_TYPE = "OrderStatusChanged";

        //prepare json (what to send and where to send)
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        try {
            //what to send
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid", orderBy);//client uid
            notificationBodyJo.put("sellerUid", firebaseAuth.getUid());
            notificationBodyJo.put("orderId", orderId);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);
            //where to send
            notificationJo.put("to", NOTIFICATION_TOPIC); //to all those subscribed to this topic
            notificationJo.put("data", notificationBodyJo);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
        }
        sendFcmNotification(notificationJo);

    }

    private void sendFcmNotification(JSONObject notificationJo) {
        //send volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send",
                notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


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