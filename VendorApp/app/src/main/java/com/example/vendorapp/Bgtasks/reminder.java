package com.example.vendorapp.Bgtasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class reminder extends Worker {
    private String fee;
    public String myID;

    public reminder(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //get my firebaseAuth id
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            myID = user.getUid();
        } else {
            Log.d("TAG", "User ID is null");
        }


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String shopID = snapshot1.getRef().getKey();

                        assert shopID != null;
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                                .child(shopID).child("Orders");
                        ref.orderByChild("orderBy").equalTo(myID).limitToLast(1);
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                        String deliveryfee = snapshot.child("delivery").getValue(String.class);
                                        savefee(deliveryfee);
                                        showReminderDialog();
                                }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.d("TAG", error.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        return null;
    }

    private void showReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Payment Reminder");
        builder.setMessage("Do You wish to Make Your \nMonthly Delivery feePayment of" + fee);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               // startPaymentactivity(fee);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void savefee(String deliveryfee) {
        this.fee = deliveryfee;
    }
}
