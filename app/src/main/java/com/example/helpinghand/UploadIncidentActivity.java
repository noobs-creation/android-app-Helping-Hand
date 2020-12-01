package com.example.helpinghand;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class UploadIncidentActivity extends AppCompatActivity {

    public String phoneNumber;
    private static final int REQUEST_LOCATION = 1;
    public LocationManager locationManager;
    public String latitude,longitude,location;
    private static final int pickImage = 1;
    public ArrayList<Uri> imageList = new ArrayList<Uri>();
    public Button button_upload_submit;
    public String imageUrl;
    public EditText editText_upload_details;
    public String editText_string;
    public ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_incident);

        //getting phone number start
        SharedPreferences result = getSharedPreferences("saveData", Context.MODE_PRIVATE);
        phoneNumber = result.getString("phoneNumber", "+911234567890");
        //getting phone number end

        //location stuff permission start
        ActivityCompat.requestPermissions(this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        //location stuff permission end

        //bottom navigation stuff start
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_report);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.nav_help:
                        Intent transfer_help = new Intent(UploadIncidentActivity.this, MainHELPActivity.class);
                        startActivity(transfer_help);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.nav_news_feed:
                        Intent transfer_news_feed = new Intent(UploadIncidentActivity.this, NewsFeedActivity.class);
                        startActivity(transfer_news_feed);
                        overridePendingTransition(0,0);
                        finish();
                        return true;


                    case R.id.nav_home:
                        Intent transfer_home = new Intent(UploadIncidentActivity.this, ProfileActivity.class);
                        startActivity(transfer_home);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.nav_report:
                        return true;

                }
                return false;
            }
        });
        //bottom navigation stuff end

        //select image stuff start
        imageView = findViewById(R.id.imageView_upload_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent select_image = new Intent(Intent.ACTION_GET_CONTENT);
                select_image.setType("image/*");
                startActivityForResult(select_image, pickImage);
            }
        });
        //select image stuff end

        //uploading and storing image stuff start
        editText_upload_details = findViewById(R.id.editText_upload_details);
        button_upload_submit = findViewById(R.id.button_upload_submit);

        button_upload_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!imageList.isEmpty()){
                    StorageReference imageFolderStorage = FirebaseStorage.getInstance().getReference().child(phoneNumber);
                    Uri imageUri = imageList.get(0);
                    final StorageReference imageName = imageFolderStorage.child("image"+imageUri.getLastPathSegment());
                    imageName.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrl = String.valueOf(uri);
                                    storeLinkInDatabase();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadIncidentActivity.this,"Details storing in Database failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadIncidentActivity.this,"Image storing in Storage failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Toast.makeText(UploadIncidentActivity.this,"Image stored in Storage", Toast.LENGTH_SHORT).show();
                    imageView.setImageResource(R.drawable.logor);

                    Intent transfer = new Intent(UploadIncidentActivity.this, NewsFeedActivity.class);
                    startActivity(transfer);
                    finish();
                }else{
                    Toast.makeText(UploadIncidentActivity.this,"Select an image!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //uploading and storing image stuff start
    }

    //storing image stuff start
    private void storeLinkInDatabase(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(phoneNumber);
        editText_string = editText_upload_details.getText().toString();
        databaseReference.child("details").setValue(editText_string);
        databaseReference.child("imgLink").setValue(imageUrl);
        imageList.clear();
        Toast.makeText(UploadIncidentActivity.this, "Details stored in Database", Toast.LENGTH_SHORT).show();
        editText_string = "";
        editText_upload_details.setText(editText_string);
    }

    //storing image stuff start
    //select_image stuff start
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == pickImage){
            if (resultCode == RESULT_OK){
                if (data != null){
                    Uri imageDataUri = data.getData();
                    imageList.clear();
                    imageList.add(imageDataUri);
                    try {
                        Bitmap bitmap_imageView = MediaStore.Images.Media.getBitmap(getContentResolver(), imageDataUri);
                        imageView.setImageBitmap(bitmap_imageView);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    //select image stuff end

    //button back stuff start
    public void back(View view) {
        Intent transfer = new Intent(UploadIncidentActivity.this, ProfileActivity.class);
        startActivity(transfer);
        finish();
    }
    //button back stuff end

    //instant gps onclick function start
    public void instant_gps(View view) {
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Check gps is enable or not
        assert locationManager != null;
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            //Write Function To enable gps
            OnGPS();
        }
        else
        {
            //GPS is already On then
            getLocation();
        }
    }
    //instant gps onclick function end

    //gps stuff functions start
    private void getLocation() {

        //Check Permissions again

        if (ActivityCompat.checkSelfPermission(UploadIncidentActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UploadIncidentActivity.this,

                Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else
        {
            Location LocationGps= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGps !=null)
            {
                double lat=LocationGps.getLatitude();
                double longi=LocationGps.getLongitude();

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);

                location = "Latitude= "+latitude+"\nLongitude= "+longitude+" Enter your text below : \n";

                editText_upload_details.setText(location);

            }
            else if (LocationNetwork !=null)
            {
                double lat=LocationNetwork.getLatitude();
                double longi=LocationNetwork.getLongitude();

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);

                location = "Latitude= "+latitude+"\nLongitude= "+longitude+" Enter your text below : \n";

                editText_upload_details.setText(location);
            }
            else if (LocationPassive !=null)
            {
                double lat=LocationPassive.getLatitude();
                double longi=LocationPassive.getLongitude();

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);

                location = "Latitude= "+latitude+"\nLongitude= "+longitude+" Enter your text below : \n";
                editText_upload_details.setText(location);
            }
            else
            {
                Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void OnGPS() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    //instant gps stuff function end

}
