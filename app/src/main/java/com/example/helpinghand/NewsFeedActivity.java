package com.example.helpinghand;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class NewsFeedActivity extends AppCompatActivity {

    public String phoneNumber;
    public RecyclerView recyclerView;
    public FirebaseRecyclerOptions<NewsFeed> options;
    public FirebaseRecyclerAdapter<NewsFeed, NewsFeedViewHolder> adapter;
    public DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);


        //getting phone number start
        SharedPreferences result = getSharedPreferences("saveData", Context.MODE_PRIVATE);
        phoneNumber = result.getString("phoneNumber", "+911234567890");
        //getting phone number end


        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        //recycler view stuff start
        recyclerView = findViewById(R.id.myRecycler_NewsFeed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        loadData();
        //recycler view stuff end


        //bottom navigation stuff start
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_news_feed);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.nav_help:
                        Intent transfer_help = new Intent(NewsFeedActivity.this, MainHELPActivity.class);
                        startActivity(transfer_help);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.nav_news_feed:
                        return true;

                    case R.id.nav_home:
                        Intent transfer_home = new Intent(NewsFeedActivity.this, ProfileActivity.class);
                        startActivity(transfer_home);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.nav_report:
                        Intent transfer_report = new Intent(NewsFeedActivity.this, ReportIncidentActivity.class);
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
                Intent transfer_upload = new Intent(NewsFeedActivity.this, UploadIncidentActivity.class);
                startActivity(transfer_upload);
                finish();
            }
        });
        //fab stuff end
    }

    private void loadData() {
        options = new FirebaseRecyclerOptions.Builder<NewsFeed>().setQuery(databaseReference, NewsFeed.class).build();
        adapter = new FirebaseRecyclerAdapter<NewsFeed, NewsFeedViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NewsFeedViewHolder newsFeedViewHolder, int i, @NonNull NewsFeed newsFeed) {
                newsFeedViewHolder.textView_details.setText(newsFeed.getDetails());
                Picasso.get().load(newsFeed.getImgLink()).into(newsFeedViewHolder.imageView);
            }

            @NonNull
            @Override
            public NewsFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_newsfeed, parent, false);
                return new NewsFeedViewHolder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

}
