package com.example.helpinghand;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    Button button_edit;
    public String phoneNumber;
    TextView textViewPhoneNumber;
    TextView textView_name, textView_age, textView_aadhar, textView_DoB, textView_bloodGroup;
    ImageView imageView_profile;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //getting phone number start
        SharedPreferences result = getSharedPreferences("saveData", Context.MODE_PRIVATE);
        phoneNumber = result.getString("phoneNumber", "+911234567890");
        //phoneNumber = "+918777852961";
        //getting phone number end

        textViewPhoneNumber = findViewById(R.id.textViewProfileNumber);
        imageView_profile = findViewById(R.id.imageViewProfile);
        textView_aadhar = findViewById(R.id.textViewProfileAadhar);
        textView_age = findViewById(R.id.textViewAge);
        textView_DoB = findViewById(R.id.textViewDoB);
        textView_bloodGroup = findViewById(R.id.textViewBloodGroup);
        textView_name = findViewById(R.id.textViewProfileName);

        textView_name.setText("NAME : details not entered yet");
        textView_aadhar.setText("AADHAR : details not entered yet");
        textView_DoB.setText("DOB : details not entered yet");
        textView_bloodGroup.setText("BLOODGROUP : details not entered yet");
        textView_age.setVisibility(View.GONE);
        textViewPhoneNumber.setText("PHONE NO. : " + phoneNumber);
        //retrieving data from firebase database start
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(phoneNumber);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = "NAME : " + dataSnapshot.child("name").getValue().toString();
                String age = "AGE : " + dataSnapshot.child("age").getValue().toString();
                String bloodGroup = "BLOODGROUP : " + dataSnapshot.child("bloodGroup").getValue().toString();
                String DoB = "DOB : " + dataSnapshot.child("DoB").getValue().toString();
                String aadhar = "AADHAR : " + dataSnapshot.child("aadhar").getValue().toString();
                String image = dataSnapshot.child("imgProfile").getValue().toString();

                //setting data in place
                textView_age.setVisibility(View.VISIBLE);
                textView_name.setText(name);
                textView_age.setText(age);
                textView_aadhar.setText(aadhar);
                textView_DoB.setText(DoB);
                textView_bloodGroup.setText(bloodGroup);
                Picasso.get().load(image).into(imageView_profile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Cannot retrieve user details!", Toast.LENGTH_LONG).show();
            }
        });
        //retrieving data from firebase database end

        //button profile editor stuff start
        button_edit = findViewById(R.id.buttonProfileEdit);
        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(ProfileActivity.this, EditDetailsActivity.class);
                startActivity(editIntent);
                finish();
            }
        });
        //button profile editor stuff end

        //bottom navigation stuff start
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_help:
                        Intent transfer_help = new Intent(ProfileActivity.this, MainHELPActivity.class);
                        startActivity(transfer_help);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.nav_news_feed:
                        Intent transfer_news_feed = new Intent(ProfileActivity.this, NewsFeedActivity.class);
                        startActivity(transfer_news_feed);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.nav_home:
                        return true;

                    case R.id.nav_report:
                        Intent transfer_report = new Intent(ProfileActivity.this, ReportIncidentActivity.class);
                        startActivity(transfer_report);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                }
                return false;
            }
        });
        //bottom navigation stuff end
        //logout button stuff start
        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, AuthenticationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        //logout button start end
    }
}
