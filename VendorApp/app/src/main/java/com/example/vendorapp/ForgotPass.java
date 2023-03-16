package com.example.vendorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPass extends AppCompatActivity implements View.OnClickListener {
    Button recbtn;
    EditText email;
    String semail;
    private ImageView back;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        hooks();

        recbtn.setOnClickListener(this);
        back.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void hooks() {
        email = findViewById(R.id.editrecov);
        recbtn = findViewById(R.id.btn);
        back = findViewById(R.id.bk);
        progressDialog = new ProgressDialog(ForgotPass.this);
    }

    private boolean validatefields() {
        semail = email.getText().toString().trim();

        if (TextUtils.isEmpty(semail)) {
            email.setError("Email Field Cant Be Empty");
            email.requestFocus();
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
            case R.id.btn:
                semail = email.getText().toString().trim();

                if (!validatefields()) {
                    return;
                }
                progressDialog.setMessage("Recovering Password");
                progressDialog.show();
                firebaseAuth.sendPasswordResetEmail(semail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ForgotPass.this, "Check Your Email To Reset Your Password", Toast.LENGTH_LONG).show();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(ForgotPass.this, "Ooops! Something went wrong", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                break;
            case R.id.bk:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}