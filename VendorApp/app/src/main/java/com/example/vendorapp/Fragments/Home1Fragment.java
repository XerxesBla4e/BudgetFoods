package com.example.vendorapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import androidx.annotation.RequiresApi;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.vendorapp.Adapter.Adapter;
import com.example.vendorapp.Adapter.ItemAdapter;
import com.example.vendorapp.Adapter.ShopAdapter;
import com.example.vendorapp.Bgtasks.reminder;
import com.example.vendorapp.Client.MainClient;
import com.example.vendorapp.MainActivity;
import com.example.vendorapp.R;
import com.example.vendorapp.Vendor.MainVendor;
import com.example.vendorapp.typemodel.ItemModel;
import com.example.vendorapp.typemodel.ShopModel;
import com.example.vendorapp.typemodel.Users;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Home1Fragment extends Fragment {
    RecyclerView recyclerView;
    ShopAdapter Adapter;
    //Adapter adapter;
    List<ShopModel> shopModelList;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private String city;
    private int days;
    SearchView searchView;

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home1, container, false);

        recyclerView = view.findViewById(R.id.shops);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shopModelList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseRecyclerOptions<ShopModel> options =
                new FirebaseRecyclerOptions.Builder<ShopModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Users").orderByChild("accounttype")
                                .equalTo("Vendor"), ShopModel.class)
                        .build();

        Adapter = new ShopAdapter(getContext(), options);
        recyclerView.setAdapter(Adapter);
        Adapter.notifyDataSetChanged();

        searchView = view.findViewById(R.id.searchview1);

      /*
        user = firebaseAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);

                if (userProfile != null) {
                    city = userProfile.getCity();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("TAG","Error: "+error.getMessage());

            }
        });

        Query adminquery = FirebaseDatabase.getInstance().getReference("Users").orderByChild("accounttype")
                .equalTo("Vendor");
       // Query query = adminquery.orderByChild("city").equalTo(city);
        adminquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        ShopModel shopModel = snapshot1.getValue(ShopModel.class);
                        shopModelList.add(shopModel);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("TAG","Error2: "+error.getMessage());
            }
        });*/

        days = actualdayCount();


        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                reminder.class, days, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(requireContext()).enqueue(workRequest);

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

    //search query implementation

    private void txtSearch(String str) {
        FirebaseRecyclerOptions<ShopModel> options =
                new FirebaseRecyclerOptions.Builder<ShopModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Users").orderByChild("accounttype")
                                .equalTo("Vendor").startAt(str).endAt(str + "~"), ShopModel.class)
                        .build();

        Adapter = new ShopAdapter(getContext(), options);
        recyclerView.setAdapter(Adapter);
        Adapter.notifyDataSetChanged();
    }
//end search query implementation

    //get actual month day count
    private int actualdayCount() {
        int day = 0;
        int[] monthArray31 = {1, 3, 5, 7, 8, 10, 12};

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        boolean isLeapYear = (((currentYear % 4 == 0) && (currentYear % 100 != 0)) || (currentYear % 400 == 0));

        if (currentMonth == 2) {
            day = isLeapYear ? 29 : 28;
        } else if (Arrays.asList(monthArray31).contains(currentMonth)) {
            day = 31;
        } else {
            day = 30;
        }
        return day;
    }
//end month day count

    //start listening for firebase items
    @Override
    public void onStart() {
        super.onStart();
        Adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        Adapter.stopListening();
    }
}