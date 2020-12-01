package com.example.helpinghand;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReportIncidentActivity extends AppCompatActivity {

    TimePickerDialog picker;
    ImageView imageView;
    public String phoneNumber;
    TextView dateOfIncident, timeOfIncident, reportingTimeAndDate;
    EditText district, subDivision, locationOfIncident, nameOfOffender, whatHappened, Description;
    private static final int pickImage = 1;
    public ArrayList<Uri> imageList = new ArrayList<Uri>();
    Button submit, upload_image;
    public int counter;
    public String imageUrl, name;
    SharedPreferences counterPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_incident);

        //getting phone number start
        SharedPreferences result = getSharedPreferences("saveData", Context.MODE_PRIVATE);
        phoneNumber = result.getString("phoneNumber", "+911234567890");

        counterPreferences = getSharedPreferences("FIRnumber", Context.MODE_PRIVATE);
        counter = counterPreferences.getInt("counter", 9900);

        name = "FIR"+counter;
        //getting phone number end

        //bottom navigation stuff start
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_report);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.nav_help:
                        Intent transfer_help = new Intent(ReportIncidentActivity.this, MainHELPActivity.class);
                        startActivity(transfer_help);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.nav_news_feed:
                        Intent transfer_news_feed = new Intent(ReportIncidentActivity.this, NewsFeedActivity.class);
                        startActivity(transfer_news_feed);
                        overridePendingTransition(0,0);
                        finish();
                        return true;


                    case R.id.nav_home:
                        Intent transfer_home = new Intent(ReportIncidentActivity.this, ProfileActivity.class);
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

        //fab stuff start
        FloatingActionButton FAB = findViewById(R.id.fab_button);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent transfer_upload = new Intent(ReportIncidentActivity.this, UploadIncidentActivity.class);
                startActivity(transfer_upload);
                finish();
            }
        });
        //fab stuff end

        //textView stuff starts

        dateOfIncident = findViewById(R.id.textView_dateOfIncident);
        timeOfIncident = findViewById(R.id.textView_timeOfIncident);
        reportingTimeAndDate = findViewById(R.id.textView_reportingTimeAndDate);

        //getting date stuff starts
        dateOfIncident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(v);
            }
        });
        //getting date stuff ends

        //getting time stuff starts
        timeOfIncident.setInputType(InputType.TYPE_NULL);
        timeOfIncident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });
        //getting time stuff ends

        //getting system date and time starts
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        reportingTimeAndDate.setText("Reporting Date and Time : "+formattedDate);
        //getting system date and time ends


        //textView stuff ends

        //editText stuff starts

        district = findViewById(R.id.editText_district);
        subDivision = findViewById(R.id.editText_subDivision);
        locationOfIncident = findViewById(R.id.editText_locationOfIncident);
        nameOfOffender = findViewById(R.id.editText_nameOfOffender);
        whatHappened = findViewById(R.id.editText_whatHappened);
        Description = findViewById(R.id.editText_Description);

        //editText stuff ends

        //select image stuff starts
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent select_image = new Intent(Intent.ACTION_GET_CONTENT);
                select_image.setType("image/*");
                startActivityForResult(select_image, pickImage);
            }
        });
        //select image stuff ends

        //button stuff starts

        //upload button for image starts
        upload_image = findViewById(R.id.button_uploadImage);
        upload_image.setOnClickListener(new View.OnClickListener() {
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
                                    storeImageLinkInDatabase();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ReportIncidentActivity.this,"Details storing in Database failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ReportIncidentActivity.this,"Image storing in Storage failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Toast.makeText(ReportIncidentActivity.this,"Image stored in Storage", Toast.LENGTH_SHORT).show();
                    imageView.setImageResource(R.drawable.logor);

                }else{
                    Toast.makeText(ReportIncidentActivity.this,"Select an image!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //upload button for image ends

        //submit button starts
        submit = findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeDataInDatabase();
                SharedPreferences.Editor editor = counterPreferences.edit();
                counter += 1;
                editor.putInt("counter", counter);
                editor.apply();

                startActivity(new Intent(ReportIncidentActivity.this, ProfileActivity.class));
                finish();
            }
        });
        //submit button ends
        //button stuff ends

    }

    //storing image stuff starts
    private void storeImageLinkInDatabase(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(phoneNumber).child(name);
        databaseReference.child("imgLinkforFIR").setValue(imageUrl);
        imageList.clear();
        Toast.makeText(ReportIncidentActivity.this, "Image Link stored in Database", Toast.LENGTH_SHORT).show();
    }

    //storing image stuff ends

    //storing image stuff starts
    private void storeDataInDatabase(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(phoneNumber).child(name);
        databaseReference.child("district").setValue(district.getText().toString().trim());
        databaseReference.child("subDivision").setValue(subDivision.getText().toString().trim());
        databaseReference.child("locationOfIncident").setValue(locationOfIncident.getText().toString().trim());
        databaseReference.child("nameOfOffender").setValue(nameOfOffender.getText().toString().trim());
        databaseReference.child("whatHappened").setValue(whatHappened.getText().toString().trim());
        databaseReference.child("Description").setValue(Description.getText().toString().trim());
        databaseReference.child("dateOfIncident").setValue(dateOfIncident.getText().toString().trim());
        databaseReference.child("timeOfIncident").setValue(timeOfIncident.getText().toString().trim());
        databaseReference.child("reportingTimeAndDate").setValue(reportingTimeAndDate.getText().toString().trim());
        Toast.makeText(ReportIncidentActivity.this, "Details stored in Database", Toast.LENGTH_SHORT).show();
    }

    //storing image stuff ends

    //date and time stuff starts
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            String format = new SimpleDateFormat("dd MMM YYYY").format(c.getTime());
            dateOfIncident.setText("Date of Incident : "+format);
        }
    };

    private void selectDate(View view){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dateDialog = new DatePickerDialog(view.getContext(), datePickerListener, mYear, mMonth, mDay);
        dateDialog.getDatePicker().setMaxDate(new Date().getTime());
        dateDialog.show();
    }

    private void selectTime(){
        final Calendar cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        // time picker dialog
        picker = new TimePickerDialog(ReportIncidentActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        timeOfIncident.setText("Time of Incident : "+sHour + ":" + sMinute);
                    }
                }, hour, minutes, true);
        picker.show();
    }
    //date and time stuff ends

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

}
