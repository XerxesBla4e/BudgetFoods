package com.example.vendorapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.Adapter.OrdersAdapter;
import com.example.vendorapp.Adapter.ShopAdapter;
import com.example.vendorapp.R;
import com.example.vendorapp.typemodel.OrdersModel;
import com.example.vendorapp.typemodel.ShopModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    OrdersAdapter ordersAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String ID;
    SearchView searchView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setHasOptionsMenu(true);

    /*    Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolBar3);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("Orders");*/

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        ID = user.getUid();

        recyclerView = view.findViewById(R.id.orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<OrdersModel> options =
                new FirebaseRecyclerOptions.Builder<OrdersModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Users").child(ID).child("Orders")
                                , OrdersModel.class)
                        .build();

        ordersAdapter = new OrdersAdapter(getContext(), options);
        recyclerView.setAdapter(ordersAdapter);
        ordersAdapter.notifyDataSetChanged();

        searchView = view.findViewById(R.id.searchview3);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                txtSearch(newText);
                return false;
            }
        });

        return view;
    }

    private void txtSearch(String str) {
        FirebaseRecyclerOptions<OrdersModel> options =
                new FirebaseRecyclerOptions.Builder<OrdersModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Users").child(ID).child("Orders")
                                        .startAt(str).endAt(str + "~")
                                , OrdersModel.class)
                        .build();

        ordersAdapter = new OrdersAdapter(getContext(), options);
        recyclerView.setAdapter(ordersAdapter);
        ordersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        ordersAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        ordersAdapter.stopListening();
    }
/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.search2);
        SearchView searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                txtSearch(query);
                return false;
            }
        });
    }

    private void txtSearch(String str) {
        FirebaseRecyclerOptions<OrdersModel> options =
                new FirebaseRecyclerOptions.Builder<OrdersModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Users").child(ID).child("Orders").orderByChild("orderID")
                                .startAt(str).endAt(str + "~"), OrdersModel.class)
                        .build();

        ordersAdapter = new OrdersAdapter(getContext(), options);
        ordersAdapter.startListening();
        recyclerView.setAdapter(ordersAdapter);
    }*/
}