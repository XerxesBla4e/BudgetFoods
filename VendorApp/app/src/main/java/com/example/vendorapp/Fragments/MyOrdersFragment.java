package com.example.vendorapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.Adapter.MyOrdersAdapter;
import com.example.vendorapp.R;
import com.example.vendorapp.typemodel.OrdersModel;
import com.example.vendorapp.typemodel.ShopModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MyOrdersFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference reference;
    public String myID;
    RecyclerView recyclerView;
    MyOrdersAdapter myOrdersAdapter;
    List<OrdersModel> ordersModelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);
        // Inflate the layout for this fragment

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        myID = user.getUid();

        recyclerView = view.findViewById(R.id.orders1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ordersModelList = new ArrayList<>();

        //   myOrdersAdapter = new MyOrdersAdapter(getContext(),ordersModelList);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String shopID = snapshot1.getRef().getKey();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                                .child(shopID).child("Orders");
                        ref.orderByChild("orderBy").equalTo(myID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        OrdersModel ordersModel = dataSnapshot.getValue(OrdersModel.class);
                                        ordersModelList.add(ordersModel);
                                        myOrdersAdapter = new MyOrdersAdapter(getContext(), ordersModelList);
                                        recyclerView.setAdapter(myOrdersAdapter);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}