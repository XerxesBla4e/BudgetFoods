package com.example.vendorapp.Vendor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.vendorapp.Fragments.HomeFragment;
import com.example.vendorapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddItem extends AppCompatActivity implements View.OnClickListener {
    private EditText name, description, price, quantity, discount, discoundesc;
    private String sname, sdescription, sprice, squantity, sdiscount, sdiscountesc;
    public String discountPrice;
    Button add;
    Switch discoun;
    private float dicount1;
    public Boolean discountavailable = false;
    ImageView imageView, bk2;
    Uri ImageUri = null;
    FirebaseStorage mStorage;
    private static final int ITEM_IMAGE_CODE = 89;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        hooks();

        discount.setVisibility(View.INVISIBLE);
        discoundesc.setVisibility(View.INVISIBLE);

        add.setOnClickListener(this);
        bk2.setOnClickListener(this);

        imageView.setOnClickListener(this);
        if (discoun != null) {
            discoun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        discountavailable = true;
                        discount.setVisibility(View.VISIBLE);
                        discoundesc.setVisibility(View.VISIBLE);
                    } else {
                        discountavailable = false;
                        discount.setVisibility(View.INVISIBLE);
                        discoundesc.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public boolean validatefields() {
        sname = name.getText().toString().trim();
        sdescription = description.getText().toString().trim();
        sprice = price.getText().toString().trim();
        squantity = quantity.getText().toString().trim();
        sdiscount = discount.getText().toString().trim();
        sdiscountesc = discoundesc.getText().toString().trim();


        if (TextUtils.isEmpty(sname)) {
            name.setError("Food Name Is Required");
            return false;
        }
        if (TextUtils.isEmpty(sdescription)) {
            description.setError("Source Description Is Required");
            return false;
        }
        if (TextUtils.isEmpty(sprice)) {
            price.setError("Price Is Required");
            return false;
        }
        /*
        if (TextUtils.isEmpty(squantity)) {
            quantity.setError("Item Unit Is Required");
            return false;

        }*/
        if (discountavailable) {
            if (TextUtils.isEmpty(sdiscount)) {
                discount.setError("Food Discount Is Required");
                return false;
            }
            if (TextUtils.isEmpty(sdiscountesc)) {
                discoundesc.setError("Food Discount Description Is Required");
                return false;
            }
            dicount1 = Float.parseFloat(sprice)-Float.parseFloat(sdiscount);
            discountPrice = String.valueOf(dicount1);
        } else {
            sdiscount = "0";
            sdiscountesc = "";
        }
        return true;
    }

    private void hooks() {
        name = findViewById(R.id.editname1);
        description = findViewById(R.id.editdescription);
        price = findViewById(R.id.editprice);
        quantity = findViewById(R.id.editquantity);
        discount = findViewById(R.id.editdiscount);
        discoundesc = findViewById(R.id.editdiscountdesc);
        add = findViewById(R.id.addp);
        discoun = findViewById(R.id.swtch);
        bk2 = findViewById(R.id.bk2);
        imageView = findViewById(R.id.img2);
        progressDialog = new ProgressDialog(this);
        mStorage = FirebaseStorage.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addp:
                if (!validatefields()) {
                    return;
                }
                addProduct();
                break;
            case R.id.img2:
                pickItemImage();
                break;
            case R.id.bk2:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void pickItemImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Item Image"), ITEM_IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == ITEM_IMAGE_CODE && data.getData() != null) {
            ImageUri = data.getData();
            imageView.setImageURI(ImageUri);
        }
    }

    private void addProduct() {
        progressDialog.setMessage("Saving Product Information...");
        progressDialog.show();

        final String timestamp = "" + System.currentTimeMillis();
        if (ImageUri == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("itemname", sname);
            hashMap.put("itemdesc", sdescription);
            hashMap.put("price", sprice);
            hashMap.put("pid", timestamp);
            hashMap.put("discount", sdiscount);
            hashMap.put("discountdescr", sdiscountesc);
            hashMap.put("discountPrice", discountPrice);
            hashMap.put("quantity", squantity);
            hashMap.put("discountavailable", discountavailable);
            hashMap.put("timestamp", timestamp);
            hashMap.put("uid", firebaseAuth.getUid());//use me for shop name logic
            hashMap.put("image", "");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(AddItem.this, "Product Added...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddItem.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressDialog.setMessage("Saving Product Information...");
            progressDialog.show();


            StorageReference filepath = mStorage.getReference().child("imagePost").child(ImageUri.getLastPathSegment());
            filepath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("itemname", sname);
                            hashMap.put("itemdesc", sdescription);
                            hashMap.put("price", sprice);
                            hashMap.put("pid", timestamp);
                            hashMap.put("discount", sdiscount);
                            hashMap.put("discountdescr", sdiscountesc);
                            hashMap.put("discountPrice", discountPrice);
                            hashMap.put("quantity", squantity);
                            hashMap.put("discountavailable", discountavailable);
                            hashMap.put("timestamp", timestamp);
                            hashMap.put("uid", firebaseAuth.getUid());
                            hashMap.put("image", "" + task.getResult().toString());

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                            reference.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddItem.this, "Product Added...", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddItem.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            });
        }
    }
}
