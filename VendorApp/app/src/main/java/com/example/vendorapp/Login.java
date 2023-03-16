package com.example.vendorapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vendorapp.Client.MainClient;
import com.example.vendorapp.Vendor.MainVendor;
import com.example.vendorapp.typemodel.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText email, password;
    private Button Login;
    private TextView frgot, s;
    private String semail, spass;
    private String userID;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private static final String TAG = "lOGIN";

    private FirebaseUser user;
    private DatabaseReference reference;

    TextInputLayout textInputLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        hooks();


        frgot.setOnClickListener(this);
        Login.setOnClickListener(this);
        s.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
/*
        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
        });
        password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);*/
    }

    private void hooks() {
        email = findViewById(R.id.editemail);
        password = findViewById(R.id.editpass);
        Login = findViewById(R.id.btnlog);
        frgot = findViewById(R.id.frgitpass);
        s = findViewById(R.id.s);
        textInputLayout = findViewById(R.id.editpasslayout);
        progressDialog = new ProgressDialog(Login.this);
    }

    private boolean validatefields() {
        semail = email.getText().toString().trim();
        spass = password.getText().toString().trim();


        if (TextUtils.isEmpty(semail)) {
            email.setError("Email Field Cant Be Empty");
            email.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(spass)) {
            password.setError("Password Field Cant Be Empty");
            password.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(semail).matches()) {
            email.setError("Email is invalid");
            email.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnlog:
                if (!validatefields()) {
                    return;
                }
                mAuth.signInWithEmailAndPassword(semail, spass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                makeonline();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                break;
            case R.id.frgitpass:
                startActivity(new Intent(Login.this, ForgotPass.class));
                break;
            case R.id.s:
                startActivity(new Intent(Login.this, ClientSignup.class));
                break;
        }

    }

    private void makeonline() {
        progressDialog.setMessage("Signing In....");
        progressDialog.show();


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "true");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(mAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        checkUserType();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserType() {
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);

                if (userProfile != null) {
                    String accounttype = userProfile.getAccounttype();

                    if (accounttype.equals("User")) {
                        progressDialog.dismiss();
                        startActivity(new Intent(Login.this, MainVendor.class));
                    } else {
                        progressDialog.dismiss();
                        startActivity(new Intent(Login.this, MainClient.class));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}