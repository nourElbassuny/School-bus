package com.example.schoolbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Test1 extends AppCompatActivity {
    String userID;
    ImageView showImage;
    private DatabaseReference mDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        Bundle b = getIntent().getExtras();
        userID = b.getString("ParentUid");
        Toast.makeText(this, userID, Toast.LENGTH_SHORT).show();
        showImage = findViewById(R.id.showImage);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Parent").child(userID).child("Student");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapShot : snapshot.getChildren()) {
                    StudentInformation studentInformation = postSnapShot.getValue(StudentInformation.class);
                    Picasso.get().load(studentInformation.getImageUrl()).into(showImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Test1.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}