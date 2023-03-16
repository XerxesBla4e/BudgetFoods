package com.example.vendorapp.Vendor;


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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vendorapp.R;
import com.example.vendorapp.typemodel.ItemModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditItem extends AppCompatActivity implements View.OnClickListener {

    private EditText name, description, price, quantity, discount, discoundesc;
    private String sname, sdescription, sprice, squantity, sdiscount, sdiscountesc;
    public String discountPrice;
    Button add;
    Switch discoun;
    private float dicount1;
    DatabaseReference reference;
    public Boolean discountavailable = false;
    ImageView imageView, bk2;
    Uri ImageUri = null;
    FirebaseStorage mStorage;
    private static final int ITEM_IMAGE_CODE = 89;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    String Pid;
    String Uid;
    String Timestamp1;
    String icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        hooks();

        firebaseAuth = FirebaseAuth.getInstance();

        if (getIntent() != null) {
            Pid = getIntent().getStringExtra("productID");
            Uid = getIntent().getStringExtra("userID");
            Timestamp1 = getIntent().getStringExtra("timestamp");
        }

        discount.setVisibility(View.INVISIBLE);
        discoundesc.setVisibility(View.INVISIBLE);

        add.setOnClickListener(this);
        bk2.setOnClickListener(this);

        imageView.setOnClickListener(this);

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(Uid).child("Products").child(Pid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ItemModel itemProfile = snapshot.getValue(ItemModel.class);

                if (itemProfile != null) {
                    name.setText(itemProfile.getItemname());
                    description.setText(itemProfile.getItemdesc());
                    price.setText(itemProfile.getPrice());
                    quantity.setText(itemProfile.getQuantity());
                    icon = itemProfile.getImage();

                    try {
                        Picasso.with(getApplicationContext()).load(itemProfile.getImage()).into(imageView);
                    } catch (Exception e) {
                        imageView.setImageResource(R.drawable.item);
                    }

                    if (discountavailable) {
                        discount.setText(itemProfile.getDiscount());
                        discoundesc.setText(itemProfile.getDiscountdescr());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditItem.this, "Error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


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


    }

    public boolean validatefields() {
        sname = name.getText().toString().trim();
        sdescription = description.getText().toString().trim();
        sprice = price.getText().toString().trim();
        squantity = quantity.getText().toString().trim();
        sdiscount = discount.getText().toString().trim();
        sdiscountesc = discoundesc.getText().toString().trim();


        if (TextUtils.isEmpty(sname)) {
            name.setError("Item Name Is Required");
            return false;
        }
        if (TextUtils.isEmpty(sdescription)) {
            description.setError("Item Description Is Required");
            return false;
        }
        if (TextUtils.isEmpty(sprice)) {
            price.setError("Item Original Price Is Required");
            return false;
        }
        if (TextUtils.isEmpty(squantity)) {
            quantity.setError("Item Unit Is Required");
            return false;

        }
        if (discountavailable) {
            if (TextUtils.isEmpty(sdiscount)) {
                discount.setError("Item Discount Is Required");
                return false;
            }
            if (TextUtils.isEmpty(sdiscountesc)) {
                discoundesc.setError("Item Discount Description Is Required");
                return false;
            }
            dicount1 = Float.parseFloat(sprice) - Float.parseFloat(sdiscount);
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
        progressDialog.setMessage("Updating Product Information...");
        progressDialog.show();

        if (ImageUri == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("itemname", sname);
            hashMap.put("itemdesc", sdescription);
            hashMap.put("price", sprice);
            hashMap.put("pid", Pid);
            hashMap.put("discount", sdiscount);
            hashMap.put("discountdescr", sdiscountesc);
            hashMap.put("discountPrice", discountPrice);
            hashMap.put("quantity", squantity);
            hashMap.put("discountavailable", discountavailable);
            hashMap.put("timestamp", Pid);
            hashMap.put("uid", firebaseAuth.getUid());
            hashMap.put("image", icon);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(Uid).child("Products").child(Pid).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(EditItem.this, "Product Updated...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditItem.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressDialog.setMessage("Updating Product Information...");
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
                            hashMap.put("pid", Pid);
                            hashMap.put("discount", sdiscount);
                            hashMap.put("discountdescr", sdiscountesc);
                            hashMap.put("discountPrice", discountPrice);
                            hashMap.put("quantity", squantity);
                            hashMap.put("discountavailable", discountavailable);
                            hashMap.put("timestamp", Pid);
                            hashMap.put("uid", firebaseAuth.getUid());
                            hashMap.put("image", "" + task.getResult().toString());

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                            reference.child(Uid).child("Products").child(Pid).updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Toast.makeText(EditItem.this, "Product Updated...", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(EditItem.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            });
        }
    }
}