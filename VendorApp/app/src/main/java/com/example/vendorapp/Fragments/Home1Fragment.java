package com.example.vendorapp.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import androidx.annotation.RequiresApi;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.Adapter.ItemAdapter;
import com.example.vendorapp.Adapter.ShopAdapter;
import com.example.vendorapp.R;
import com.example.vendorapp.typemodel.ItemModel;
import com.example.vendorapp.typemodel.ShopModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class Home1Fragment extends Fragment {
    RecyclerView recyclerView;
    ShopAdapter shopAdapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home1, container, false);

        recyclerView = view.findViewById(R.id.shops);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<ShopModel> options =
                new FirebaseRecyclerOptions.Builder<ShopModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Users").orderByChild("accounttype")
                                .equalTo("Vendor"), ShopModel.class)
                        .build();

        shopAdapter = new ShopAdapter(getContext(), options);
        recyclerView.setAdapter(shopAdapter);
        shopAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        shopAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        shopAdapter.stopListening();
    }
}