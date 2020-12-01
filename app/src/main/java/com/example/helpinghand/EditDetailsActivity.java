package com.example.helpinghand;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class EditDetailsActivity extends AppCompatActivity {

    private static final int pickImage = 1;
    public String url;
    ArrayList<Uri> imageList = new ArrayList<Uri>();
    EditText editText_name, editText_aadhar, editText_bloodGroup;
    TextView textView_age, textView_date;
    FloatingActionButton fab_date;
    Button button_submit, button_back;
    ImageView profilePic;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_details);

        SharedPreferences result = getSharedPreferences("saveData", Context.MODE_PRIVATE);
        phoneNumber = result.getString("phoneNumber", "+911234567890");

        editText_aadhar = findViewById(R.id.editText_EditDetails_Aadhar);
        fab_date = findViewById(R.id.floatingActionButton_date);
        textView_age = findViewById(R.id.textView_EditDetails_Age);
        textView_date = findViewById(R.id.textView_EditDetails_Date);
        editText_name = findViewById(R.id.editText_EditDetails_Name);
        editText_bloodGroup = findViewById(R.id.editText_EditDetails_BloodGroup);
        button_submit = findViewById(R.id.button_EditDetails_submit);
        profilePic = findViewById(R.id.imageView_EditDetails_profilePic);
        button_back = findViewById(R.id.button_EditDetails_back);
        //birthday picker and age stuff start
        fab_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(), datePickerListener, mYear, mMonth, mDay);
                dateDialog.getDatePicker().setMaxDate(new Date().getTime());
                dateDialog.show();
            }
        });
        //birthday picker and age stuff end

        //back button stuff start
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent transfer = new Intent(EditDetailsActivity.this, ProfileActivity.class);
                startActivity(transfer);
                finish();
            }
        });
        //back button stuff end

        //profile picture selection stuff start
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent select_image = new Intent(Intent.ACTION_GET_CONTENT);
                select_image.setType("image/*");
                startActivityForResult(select_image, pickImage);
            }
        });
        //profile picture selection stuff start

        //uploading details to database stuff start
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //image storing stuff start
                if (!imageList.isEmpty()){
                    StorageReference imageFolderStorage = FirebaseStorage.getInstance().getReference().child(phoneNumber);
                    Uri imageUri = imageList.get(0);
                    final StorageReference imageName = imageFolderStorage.child("Profileimage"+imageUri.getLastPathSegment());
                    imageName.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    url = String.valueOf(uri);
                                    storeLinkInDatabase();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditDetailsActivity.this,"EditDetails storing in Database failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditDetailsActivity.this,"ProfileImage storing in Storage failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Toast.makeText(EditDetailsActivity.this,"ProfileImage stored in Storage", Toast.LENGTH_SHORT).show();
                    profilePic.setImageResource(R.drawable.logor);
                    //image storing stuff end
                    Intent transfer_home = new Intent(EditDetailsActivity.this, ProfileActivity.class);
                    startActivity(transfer_home);
                    finish();
                }else {
                    Toast.makeText(EditDetailsActivity.this,"Select image first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //uploading details to database stuff end
    }

    //storing image stuff start
    private void storeLinkInDatabase(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(phoneNumber);
        databaseReference.child("name").setValue(editText_name.getText().toString().trim());
        databaseReference.child("aadhar").setValue(editText_aadhar.getText().toString().trim());
        databaseReference.child("age").setValue(textView_age.getText().toString().trim());
        databaseReference.child("DoB").setValue(textView_date.getText().toString().trim());
        databaseReference.child("bloodGroup").setValue(editText_bloodGroup.getText().toString().trim());
        databaseReference.child("imgProfile").setValue(url);
        imageList.clear();
        Toast.makeText(EditDetailsActivity.this, "EditDetails stored in Database", Toast.LENGTH_SHORT).show();
    }

    //storing image stuff end

    //birthday picker and age stuff start
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            String format = new SimpleDateFormat("dd MMM YYYY").format(c.getTime());
            textView_date.setText(format);
            String age = Integer.toString(calculateAge(c.getTimeInMillis()));
            textView_age.setText(age+" yrs.");
        }
    };

    int calculateAge(long date){
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if(today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)){
            age--;
        }
        return age;
    }
    //birthday picker and age stuff end

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
                        profilePic.setImageBitmap(bitmap_imageView);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    //select image stuff end

}