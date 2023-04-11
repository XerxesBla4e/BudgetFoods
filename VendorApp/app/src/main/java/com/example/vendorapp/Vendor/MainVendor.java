package com.example.vendorapp.Vendor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.vendorapp.Cart.DatabaseManager;
import com.example.vendorapp.Client.MainClient;
import com.example.vendorapp.Fragments.FragmentCart;
import com.example.vendorapp.Fragments.Home1Fragment;
import com.example.vendorapp.Fragments.MyOrdersFragment;
import com.example.vendorapp.Fragments.ProductFragment;
import com.example.vendorapp.Login;
import com.example.vendorapp.R;
import com.example.vendorapp.UpdateProfile;
import com.example.vendorapp.typemodel.UserDetsModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLDataException;
import java.util.HashMap;
import java.util.Locale;

public class MainVendor extends AppCompatActivity  {

   // ImageView signout, editprof, lingual;
    TextView myprof;
    private FirebaseAuth firebaseAuth;
    String username, accttype;
    FirebaseUser user;
    BottomNavigationView bottomNavigationView;
    private static final String TAG = "Client Signup";
    DatabaseManager databaseManager;
    Toolbar toolbar;

    @Override 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguage();
        setContentView(R.layout.activity_main_vendor);


        databaseManager = new DatabaseManager(getApplicationContext());

        try {
            databaseManager.open();
        } catch (SQLDataException e) {
            e.printStackTrace();
        }
        hooks();

        setSupportActionBar(toolbar);

        //signout.setOnClickListener(this);
        //editprof.setOnClickListener(this);
       // lingual.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        checkUser();

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container1, new Home1Fragment()).commit();

        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container1, new Home1Fragment()).addToBackStack(null).commit();
                        break;
                    case R.id.crt:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container1, new FragmentCart()).addToBackStack(null).commit();
                        break;
                    case R.id.viewo:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new MyOrdersFragment()).addToBackStack(null).commit();
                        break;
                }

            }
        });

    }

    private void checkUser() {
        if (user == null) {
            startActivity(new Intent(MainVendor.this, Login.class));
        } else {
            loadmyProfile();
        }
    }

    private void makeOffline() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(user.getUid()).updateChildren(hashMap);
    }

    private void loadmyProfile() {
        FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetsModel userDetsModel = snapshot.getValue(UserDetsModel.class);

                username = userDetsModel.getName();
                accttype = userDetsModel.getAccounttype();

                myprof.setText("Hello"+username + "(" + accttype + ")");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void hooks() {
       // signout = findViewById(R.id.signout);
        myprof = findViewById(R.id.prfle);
       // editprof = findViewById(R.id.edit);
      //  lingual = findViewById(R.id.lingual);
        toolbar = findViewById(R.id.toolb);
        bottomNavigationView = findViewById(R.id.bottomNavigationView1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lingual1:
                showLanguages();
                break;
            case R.id.signout:
                firebaseAuth.signOut();
                makeOffline();
                checkUser();
                startActivity(new Intent(MainVendor.this, Login.class));
                break;
            case R.id.edit:
                startActivity(new Intent(MainVendor.this, UpdateProfile.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLanguages() {
        final String[] langs = {"Swahili", "Nyankole", "English"};
        AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(MainVendor.this);
        mBuilder.setTitle("Select Language");
        mBuilder.setSingleChoiceItems(langs, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int xer) {
                if (xer == 0) {
                    setLanguage("nyn");
                    recreate();
                } else if (xer == 1) {
                    setLanguage("sw");
                    recreate();
                } else if (xer == 2) {
                    setLanguage("en");
                    recreate();
                }
                dialog.dismiss();
            }
        });
        androidx.appcompat.app.AlertDialog mDialog = mBuilder.create();
        mBuilder.show();
    }

    public void setLanguage(String language) {
        Locale locale = new Locale(language);
        locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor preferences = getSharedPreferences("xerx", Context.MODE_PRIVATE).edit();
        preferences.putString("language", language);
        preferences.apply();
    }

    public void changeLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences("xerx", Context.MODE_PRIVATE);
        String lang = sharedPreferences.getString("language", "");
        setLanguage(lang);
    }
}
