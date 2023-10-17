package com.example.schoolbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DriverHome extends AppCompatActivity {
    ImageView signOut;

    private DriverAdapter mAdapter;
    private List<StudentInformation> mStudentInf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        ProgressDialog progressDialog = new ProgressDialog(DriverHome.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        RecyclerView mRecyclerView = findViewById(R.id.recycler_view_driver);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mStudentInf = new ArrayList<>();
        mAdapter = new DriverAdapter(DriverHome.this, mStudentInf);
        mRecyclerView.setAdapter(mAdapter);

        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Parent");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot1 : snapshot.getChildren()) {
                    if (postSnapshot1.child("Student").exists()) {
                        for (DataSnapshot postSnapShot2:postSnapshot1.child("Student").getChildren()) {

                            StudentInformation studentInformation = postSnapShot2.getValue(StudentInformation.class);

                            mStudentInf.add(studentInformation);
                        }

                    }
                }
                mAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DriverHome.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        signOut = findViewById(R.id.signOut);
        signOut.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginDriver.class);
            startActivity(intent);
            finish();
        });

    }
}