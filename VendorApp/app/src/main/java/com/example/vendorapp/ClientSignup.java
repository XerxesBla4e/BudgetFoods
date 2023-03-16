package com.example.vendorapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ClientSignup extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {
    private static final int IMG_PICK_CODE = 99;
    private ImageView coordinates;
    public Double longitude, latitude;
    private String address;
    private EditText name, email, phonenumber, pass, repass, addres;
    private String sname, semail, sphonenumber, spass, srepass,city,state,country,district;
    private TextView vendorsignup;
    public ProgressDialog progressDialog;
    public FirebaseAuth mAuth;
    FirebaseStorage mStorage;
    private ImageView profile, back;
    Uri choosenimg = null;
    Button signup;
    private static final String TAG = "Client Signup";

    private static final int MY_PERMISSION_REQUEST_CODE = 71;
    private static final int PLAY_SERVICES_RES_REQUEST = 72;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISTANCE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_signup);

        hooks();

        profile.setOnClickListener(this);
        signup.setOnClickListener(this);
        vendorsignup.setOnClickListener(this);

        coordinates.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        back.setOnClickListener(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {

                buildGoogleApiClient();
                createLocationRequest();
            }
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {

                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
            }
        }
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            try {
                Geocoder geocoder = new Geocoder(ClientSignup.this, Locale.getDefault());
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                address = addressList.get(0).getAddressLine(0);
                addres.setText(address);
                city = addressList.get(0).getLocality();//city
                state = addressList.get(0).getAdminArea();//region
                country = addressList.get(0).getCountryName();//country
                district = addressList.get(0).getSubAdminArea();//district

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(ClientSignup.this, "Couldnt get the location", Toast.LENGTH_SHORT).show();
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RES_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }


    private void hooks() {
        coordinates = findViewById(R.id.cordinates);
        name = findViewById(R.id.editname);
        email = findViewById(R.id.editemail);
        phonenumber = findViewById(R.id.editphone);
        pass = findViewById(R.id.editpassword);
        repass = findViewById(R.id.editrepassword);
        signup = findViewById(R.id.btnsgnup);
        back = findViewById(R.id.bk1);
        addres = findViewById(R.id.editaddress);
        profile = findViewById(R.id.profile);
        vendorsignup = findViewById(R.id.vndrsignup);
        progressDialog = new ProgressDialog(ClientSignup.this);
        mStorage = FirebaseStorage.getInstance();

    }


    private boolean validatefields() {
        sname = name.getText().toString().trim();
        semail = email.getText().toString().trim();
        sphonenumber = phonenumber.getText().toString().trim();
        spass = pass.getText().toString().trim();
        srepass = repass.getText().toString().trim();


        if (TextUtils.isEmpty(sname)) {
            name.setError("Name Field Cant Be Empty");
            name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(semail)) {
            name.setError("Email Field Cant Be Empty");
            name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(sphonenumber)) {
            name.setError("PhoneNumber Field Cant Be Empty");
            name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(spass)) {
            name.setError("Password Field Cant Be Empty");
            name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(srepass)) {
            name.setError("Repassword Field Cant Be Empty");
            name.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(semail).matches()) {
            email.setError("Email is invalid");
            email.requestFocus();
            return false;
        }
        if (!spass.equals(srepass)) {
            name.setError("Passwords Don't March");
            name.requestFocus();
            return false;
        }

        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cordinates:
                displayLocation();
                break;
            case R.id.btnsgnup:
                sname = name.getText().toString().trim();
                semail = email.getText().toString().trim();
                sphonenumber = phonenumber.getText().toString().trim();
                spass = pass.getText().toString().trim();
                srepass = repass.getText().toString().trim();

                if (!validatefields()) {
                    return;
                }
                mAuth.createUserWithEmailAndPassword(semail, spass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                progressDialog.dismiss();
                                if (!validatefields()) {
                                    return;
                                }
                                UploadData();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                break;
            case R.id.profile:
                pickImage();
                break;
            case R.id.vndrsignup:
                startActivity(new Intent(ClientSignup.this, VendorSignUp.class));
                break;
            case R.id.bk1:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //finish();
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose Profile Image"), IMG_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_PICK_CODE && resultCode == RESULT_OK && data.getData() != null) {
            choosenimg = data.getData();
            profile.setImageURI(choosenimg);
        }

    }

    private void UploadData() {
        progressDialog.setMessage("Saving Account Information...");
        progressDialog.show();

        final String timestamp = "" + System.currentTimeMillis();

        if (choosenimg == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid", "" + mAuth.getUid());
            hashMap.put("email", "" + semail);
            hashMap.put("name", "" + sname);
            hashMap.put("phone", "" + sphonenumber);
            hashMap.put("address", "" + address);
            hashMap.put("city", "" + city);
            hashMap.put("state", "" + state);
            hashMap.put("country", "" + country);
            hashMap.put("district", "" + district);
            hashMap.put("latitude", "" + longitude);
            hashMap.put("longitude", "" + latitude);
            hashMap.put("timestamp", "" + timestamp);
            hashMap.put("accounttype", "User");
            hashMap.put("online", "true");
            hashMap.put("image", "");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(mAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(ClientSignup.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ClientSignup.this, Login.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ClientSignup.this, "Failed: Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressDialog.setMessage("Uploading.......");
            progressDialog.show();

            StorageReference filepath = mStorage.getReference().child("imagePost").child(choosenimg.getLastPathSegment());
            filepath.putFile(choosenimg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid", "" + mAuth.getUid());
                            hashMap.put("email", "" + semail);
                            hashMap.put("name", "" + sname);
                            hashMap.put("phone", "" + sphonenumber);
                            hashMap.put("address", "" + address);
                            hashMap.put("city", "" + city);
                            hashMap.put("state", "" + state);
                            hashMap.put("country", "" + country);
                            hashMap.put("district", "" + district);
                            hashMap.put("latitude", "" + longitude);
                            hashMap.put("longitude", "" + latitude);
                            hashMap.put("timestamp", "" + timestamp);
                            hashMap.put("accounttype", "User");
                            hashMap.put("online", "true");
                            hashMap.put("image", "" + task.getResult().toString());

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.child(mAuth.getUid()).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ClientSignup.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ClientSignup.this, Login.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    });
                }
            });


        }
    }


    public void onConnected(Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        // displayLocation();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
}