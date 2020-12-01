package com.example.helpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainHELPActivity extends AppCompatActivity {

    public String phoneNumber;
    Button button_sos, button_call;

    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_CALL = 1;
    public LocationManager locationManager;
    public String latitude, longitude, location;
    public String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_h_e_l_p);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(MainHELPActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainHELPActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }

        //getting phone number start
        SharedPreferences result = getSharedPreferences("saveData", Context.MODE_PRIVATE);
        phoneNumber = result.getString("phoneNumber", "+911234567890");
        //getting phone number end


        // Check gps is enable or not
        assert locationManager != null;
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Write Function To enable gps
            OnGPS();
        } else {
            //GPS is already On then
            getLocation();
        }

        //button stuff starts
        button_sos = findViewById(R.id.button_sos);
        button_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainHELPActivity.this, location, Toast.LENGTH_LONG).show();

                Send sendcode = new Send();
                message = location + "Help Me !!!\nPHONE NO. : " + phoneNumber;
                sendcode.execute();

            }
        });
        //button stuff ends


        //button stuff 2 starts
        button_call = findViewById(R.id.button_call);
        button_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:1091"));

                if (ActivityCompat.checkSelfPermission(MainHELPActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);

            }
        });
        //button stuff 2 ends


        //bottom navigation stuff start
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_help);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.nav_help:
                        return true;

                    case R.id.nav_news_feed:
                        Intent transfer_news_feed = new Intent(MainHELPActivity.this, NewsFeedActivity.class);
                        startActivity(transfer_news_feed);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.nav_home:
                        Intent transfer_home = new Intent(MainHELPActivity.this, ProfileActivity.class);
                        startActivity(transfer_home);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.nav_report:
                        Intent transfer_report = new Intent(MainHELPActivity.this, ReportIncidentActivity.class);
                        startActivity(transfer_report);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                }
                return false;
            }
        });
        //bottom navigation stuff end

        //fab stuff start
        FloatingActionButton FAB = findViewById(R.id.fab_button);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent transfer_upload = new Intent(MainHELPActivity.this, UploadIncidentActivity.class);
                startActivity(transfer_upload);
                finish();
            }
        });
        //fab stuff end

    }

    class Send extends AsyncTask<Void,Void,Void> {

        Socket s;
        PrintWriter pw;

        @Override
        protected Void doInBackground(Void... params){
            try{
                s = new Socket("DESKTOP-2MC2GJ1",8000);
                pw = new PrintWriter(s.getOutputStream());
                pw.write(message);
                pw.flush();
                pw.close();
                s.close();
            }catch(UnknownHostException e){
                System.out.println("Fail!");
                e.printStackTrace();
            }catch (IOException e){
                System.out.println("Fail!");
                e.printStackTrace();
            }
            return null;
        }
    }


    //gps stuff functions start
    private void getLocation() {

        //Check Permissions again

        if (ActivityCompat.checkSelfPermission(MainHELPActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainHELPActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
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

            }
            else if (LocationNetwork !=null)
            {
                double lat=LocationNetwork.getLatitude();
                double longi=LocationNetwork.getLongitude();

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);

                location = "Latitude= "+latitude+"\nLongitude= "+longitude+" Enter your text below : \n";

            }
            else if (LocationPassive !=null)
            {
                double lat=LocationPassive.getLatitude();
                double longi=LocationPassive.getLongitude();

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);

                location = "Latitude= "+latitude+"\nLongitude= "+longitude+" Enter your text below : \n";

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
