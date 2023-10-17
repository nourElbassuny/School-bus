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


import java.util.Objects;


public class LoginParent extends AppCompatActivity {
    private EditText email, password;
    private FirebaseAuth firebaseAuth;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_parent);
        TextView signupParent = findViewById(R.id.signupParent);
        Button signIn = findViewById(R.id.signIn);
        email = findViewById(R.id.email_parent);
        password = findViewById(R.id.password_parent);
        firebaseAuth = FirebaseAuth.getInstance();

        signIn.setOnClickListener(view -> {
            if (validateEmail() && validatePassword()) {
                checkUser(email.getText().toString().trim(), password.getText().toString().trim());
            }
        });
        signupParent.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignUpParent.class);
            startActivity(intent);
            finish();
        });

    }

    public Boolean validateEmail() {
        String val = email.getText().toString().trim();
        if (val.isEmpty()) {
            email.setError("Password cannot be empty");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = password.getText().toString().trim();
        if (val.isEmpty()) {
            password.setError("Password cannot be empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    public void checkUser(String emailAddress, String usePassword) {
        firebaseAuth.signInWithEmailAndPassword(emailAddress, usePassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseUser = task.getResult().getUser();
                assert firebaseUser != null;
                String uid = firebaseUser.getUid().trim();
                Intent intent = new Intent(getApplicationContext(), Home.class);
                intent.putExtra("ParentUid", uid);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}