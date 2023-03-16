package com.example.vendorapp.Client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.Adapter.ItemsAdapter;
import com.example.vendorapp.R;
import com.example.vendorapp.typemodel.DetsModel;
import com.example.vendorapp.typemodel.OrdersModel;
import com.example.vendorapp.typemodel.UserDetsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class OrderDetails2 extends AppCompatActivity {
    RecyclerView recyclerView;
    ItemsAdapter itemsAdapter;

    private static final String TAG = "Orders Details";

    private TextView name, OrderId, status, cost;
    String orderID;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    //  private FirebaseUser firebaseUser;
    public String shopID;
    public String telno, name1, id, clientlatitude, clientlongitude, shoplongitude, shoplatitude;
    public int total, quantity;
    public String orderBy;
    List<DetsModel> detsModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details2);

        hooks();

        recyclerView = findViewById(R.id.prods3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        firebaseAuth = FirebaseAuth.getInstance();
        /*
        firebaseUser = firebaseAuth.getCurrentUser();
        shopID = firebaseUser.getUid();
*/
        final Intent intent3 = getIntent();
        orderID = intent3.getStringExtra("orderID");
        shopID = intent3.getStringExtra("shopID");


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(shopID).child("Orders");
        databaseReference.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                OrdersModel ordersModel = snapshot.getValue(OrdersModel.class);
                if (ordersModel != null) {
                    cost.setText("Total:" + ordersModel.getCost());
                    OrderId.setText(ordersModel.getOrderID());
                    orderBy = ordersModel.getOrderBy();
                    //  clientID = String.valueOf(ordersModel.getOrderBy());
                    status.setText(ordersModel.getOrderStatus());

                    if (status.equals("In Progress")) {
                        status.setText(ordersModel.getOrderStatus());
                        status.setTextColor(getApplicationContext().getResources().getColor(R.color.green_pie));
                    } else if (status.equals("Cancelled")) {
                        status.setText(ordersModel.getOrderStatus());
                        status.setTextColor(getApplicationContext().getResources().getColor(R.color.red));
                    } else if (status.equals("Confirmed")) {
                        status.setText(ordersModel.getOrderStatus());
                        status.setTextColor(getApplicationContext().getResources().getColor(R.color.light_green));
                    }


                    FirebaseDatabase.getInstance().getReference("Users").child(ordersModel.getOrderBy()).addListenerForSingleValueEvent(new ValueEventListener() {
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

        detsModelList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(shopID).child("Orders");
        databaseReference.child(orderID).child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DetsModel detsModel = snapshot.getValue(DetsModel.class);

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

        FirebaseDatabase.getInstance().getReference("Users").child(shopID).addListenerForSingleValueEvent(new ValueEventListener() {
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
    }

}