package com.example.vendorapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.vendorapp.Client.MainClient;
import com.example.vendorapp.Vendor.MainVendor;
import com.example.vendorapp.typemodel.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIMER = 3000;
    TextView appname;
    private static final String TAG = "Vendor Signup";
    LottieAnimationView lottie;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        appname = findViewById(R.id.appnme);
        lottie = findViewById(R.id.lottie);

        firebaseAuth = FirebaseAuth.getInstance();

        appname.animate().translationY(-1400).setDuration(2700).setStartDelay(0);
        lottie.animate().translationX(2000).setDuration(2000).setStartDelay(2900);

        if (!isConnected()) {
            showNoInternetDialog();
        } else {

            firebaseAuth = FirebaseAuth.getInstance();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    user = firebaseAuth.getCurrentUser();
                    if (user == null) {

                        startActivity(new Intent(MainActivity.this, Login.class));

                    } else {
                        checkUserType();
                    }
                }
            }, SPLASH_TIMER);
        }

    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please Connect Internet Connection To use this app\nSome features" +
                "may not be available without internet");
        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
        builder.setCancelable(false);
        builder.show();

    }

    private void checkUserType() {
        user = firebaseAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);

                if (userProfile != null) {
                    String accounttype = userProfile.getAccounttype();

                    if (accounttype.equals("User")) {
                        startActivity(new Intent(MainActivity.this, MainVendor.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, MainClient.class));

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
    }


}