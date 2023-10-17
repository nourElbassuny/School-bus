package com.example.schoolbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ShowStudent extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String userID;
    private String studentID;
    private final int SELECT_PICTURE = 200;
    private final static int REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private ProgressBar mProgressBar;
    private EditText studentName, studentAge, locationEditText;
    private ImageView takeImage, showImage;
    private Button locationButton, finish;
    private RadioGroup radioGroup;
    private RadioButton genderRadioButton;
    private RadioButton male, female;
    private Spinner spinner;
    private Uri mImageUri;
    private String stageSelected, name, age, gender, Latitude, Longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_student);
        Bundle b = getIntent().getExtras();
        assert b != null;
        userID = b.getString("ParentUid");
        studentID = b.getString("StudentUid");
        studentName = findViewById(R.id.EstudentName);
        studentAge = findViewById(R.id.EstudentAge);
        locationEditText = findViewById(R.id.Elocation);
        takeImage = findViewById(R.id.EtakeImage);
        showImage = findViewById(R.id.EshowImage);
        radioGroup = findViewById(R.id.EradioGroup);
        male = findViewById(R.id.EradioMale);
        female = findViewById(R.id.EradioFemale);
        spinner = findViewById(R.id.Espinner);
        locationButton = findViewById(R.id.EbuttonLocation);
        finish = findViewById(R.id.Efinish);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        locationButton.setOnClickListener(view -> {
            getLastLocation();
        });


        takeImage.setOnClickListener(view -> {
            imageChooser();
        });
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            genderRadioButton = findViewById(i);
            gender = genderRadioButton.getText().toString().trim();
        });
        spinner.setOnItemSelectedListener(this);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Parent").child(userID)
                .child("Student").child(studentID);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.stages,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StudentInformation studentInformation = snapshot.getValue(StudentInformation.class);
                Picasso.get().load(studentInformation.getImageUrl()).fit().centerCrop().into(showImage);
                studentName.setText(studentInformation.getName());
                studentAge.setText(studentInformation.getAge());
                locationEditText.setText(studentInformation.getLatitude() + "," + studentInformation.getLongitude());
                if (studentInformation.getGender().equals("Female")) {
                    female.setChecked(true);
                } else {
                    male.setChecked(true);
                }
                Latitude = studentInformation.getLatitude();
                Longitude = studentInformation.getLongitude();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowStudent.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        finish.setOnClickListener(view -> {
            name = studentName.getText().toString().trim();
            age = studentAge.getText().toString().trim();

            if (mImageUri == null) {
                Toast.makeText(this, "Error you should upload image", Toast.LENGTH_SHORT).show();
            } else if (name == null || name.isEmpty()) {
                studentName.setError("empty");
                Toast.makeText(this, "You should write student name", Toast.LENGTH_SHORT).show();
            } else if (age.isEmpty()) {
                studentAge.setError("empty");
                Toast.makeText(this, "You should write student age", Toast.LENGTH_SHORT).show();
            } else if (gender == null || gender.isEmpty()) {
                Toast.makeText(this, "You should select the gender of student", Toast.LENGTH_SHORT).show();
            } else if (stageSelected == null || stageSelected.isEmpty()) {
                Toast.makeText(this, "You should select the stage education of student", Toast.LENGTH_SHORT).show();
            } else {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                    openHomeActivity();
                }
            }
        });

    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("ParentUid", userID);
        startActivity(intent);
        finish();
    }

    private void uploadFile() {
        if (mImageUri != null) {
            Toast.makeText(ShowStudent.this, "Upload successful", Toast.LENGTH_LONG).show();
            StudentInformation studentInformation = new StudentInformation(name, age, gender, stageSelected,
                    Latitude, Longitude, mImageUri.toString());
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("age", studentInformation.getAge());
            map.put("gender", studentInformation.getGender());
            map.put("stage", studentInformation.getStage());
            map.put("latitude", studentInformation.getLatitude());
            map.put("longitude", studentInformation.getLongitude());
            map.put("imageUrl", studentInformation.getImageUrl());
            mDatabaseRef.updateChildren(map).addOnSuccessListener(unused -> {
                Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(ShowStudent.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        locationEditText.setText("LOCATION : " + addresses.get(0).getLatitude() + "\n" + addresses.get(0).getLongitude());
                        Latitude = "" + addresses.get(0).getLatitude();
                        Longitude = "" + addresses.get(0).getLongitude();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            });
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(ShowStudent.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Please provide the required permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            mImageUri = data.getData();
            if (mImageUri != null) {
                Picasso.get().load(mImageUri).into(showImage);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        stageSelected = adapterView.getItemAtPosition(i).toString().trim();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(this, "Please Choose the education stage", Toast.LENGTH_SHORT).show();
    }

}