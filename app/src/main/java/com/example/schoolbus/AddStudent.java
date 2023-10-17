package com.example.schoolbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.MimeTypeMap;
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
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddStudent extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String userID;
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
    private Spinner spinner;
    private Uri mImageUri;
    private String stageSelected, name, age, gender, Latitude, Longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        Bundle b = getIntent().getExtras();
        userID = b.getString("ParentUid");


        studentName = findViewById(R.id.studentName);
        studentAge = findViewById(R.id.studentAge);
        locationEditText = findViewById(R.id.location);
        takeImage = findViewById(R.id.takeImage);
        showImage = findViewById(R.id.showImage);
        radioGroup = findViewById(R.id.radioGroup);
        spinner = findViewById(R.id.spinner);
        locationButton = findViewById(R.id.buttonLocation);
        finish = findViewById(R.id.finish);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mStorageRef = FirebaseStorage.getInstance().getReference("Parent").child(userID).child("Student");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Parent").child(userID).child("Student");
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

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.stages,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

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

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() +
                    "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(AddStudent.this, "Upload successful", Toast.LENGTH_LONG).show();


                        StudentInformation studentInformation = new StudentInformation(name, age, gender, stageSelected,
                                Latitude, Longitude, mImageUri.toString());

                        String upLoadId = mDatabaseRef.push().getKey();
                        mDatabaseRef.child(upLoadId).setValue(studentInformation);
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddStudent.this, e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnProgressListener(snapshot -> {

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(AddStudent.this, Locale.getDefault());
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
        ActivityCompat.requestPermissions(AddStudent.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
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