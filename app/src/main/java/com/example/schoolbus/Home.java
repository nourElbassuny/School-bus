package com.example.schoolbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements StudentAdapter.OnItemClickListener {
    private String userID;
    ImageView logout;
    Button newStudent;
    TextView title;
    private FirebaseStorage mStorage;
    private ValueEventListener mDBListener;
    private DatabaseReference mDatabaseRef;
    private StudentAdapter mAdapter;
    private List<StudentInformation> mStudentInf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ProgressDialog progressDialog = new ProgressDialog(Home.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Bundle b = getIntent().getExtras();
        assert b != null;
        userID = b.getString("ParentUid");
        logout = findViewById(R.id.signOut);
        newStudent = findViewById(R.id.add_new_student);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        title = findViewById(R.id.titleName);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mStudentInf = new ArrayList<>();

        mAdapter = new StudentAdapter(Home.this, mStudentInf);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(Home.this);

        mStorage = FirebaseStorage.getInstance().getReference("Parent").getStorage();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Parent").child(userID).child("Student");

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mStudentInf.clear();
                for (DataSnapshot postSnapShot : snapshot.getChildren()) {
                    StudentInformation studentInformation = postSnapShot.getValue(StudentInformation.class);
                    studentInformation.setKey(postSnapShot.getKey());
                    mStudentInf.add(studentInformation);
                }
                mAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        logout.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginParent.class);
            startActivity(intent);
            finish();
        });
        newStudent.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddStudent.class);
            intent.putExtra("ParentUid", userID);
            startActivity(intent);
        });


    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click at position " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(int position) {
        StudentInformation studentItem = mStudentInf.get(position);
        String selectedKey = studentItem.getKey();
        Intent intent = new Intent(getApplicationContext(), ShowStudent.class);
        intent.putExtra("ParentUid", userID);
        intent.putExtra("StudentUid", selectedKey);
        startActivity(intent);
        Toast.makeText(this, "Edit click at position " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        StudentInformation studentItem = mStudentInf.get(position);
        String selectedKey = studentItem.getKey();

        Toast.makeText(this, selectedKey, Toast.LENGTH_SHORT).show();
        DatabaseReference mTask = mDatabaseRef.child(selectedKey);
        mTask.removeValue().addOnSuccessListener(unused -> {
            Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(error -> {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        });


        //  StorageReference imageRef = mStorage.getReferenceFromUrl(studentItem.getImageUrl());
//        imageRef.delete().addOnSuccessListener(aVoid -> {
//
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

}