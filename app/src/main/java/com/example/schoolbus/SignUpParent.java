package com.example.schoolbus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpParent extends AppCompatActivity {
  private   EditText firstname, secondName, email, password, confirmPassword;
    private   FirebaseAuth firebaseAuth;
   private FirebaseUser firebaseUser;
   private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_parent);
        firstname = findViewById(R.id.first_name);
        secondName = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        Button signup = findViewById(R.id.signup);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Parent");

        signup.setOnClickListener(view -> {
            String SFirstName = firstname.getText().toString().trim();
            String SLastName = secondName.getText().toString().trim();
            String SEmail = email.getText().toString().trim();
            String SPassword = password.getText().toString().trim();
            String CONFIRMPassword = confirmPassword.getText().toString().trim();

            if (SPassword.equals(CONFIRMPassword)) {
                firebaseAuth.createUserWithEmailAndPassword(SEmail, SPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        firebaseUser = task.getResult().getUser();

                        DatabaseReference newUser = databaseReference.child(firebaseUser.getUid());
                        newUser.setValue(new ParentInformation( SEmail,SFirstName, SLastName, SPassword));

                        String uid = firebaseUser.getUid().trim();
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        intent.putExtra("ParentUid", uid);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                confirmPassword.setError("Not match");
                Toast.makeText(this, "Password can't match", Toast.LENGTH_SHORT).show();
            }

        });

        TextView login = findViewById(R.id.login);
        login.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginParent.class);
            startActivity(intent);
        });
    }
}