package com.example.schoolbus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SignUpDriver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_driver);
        TextView login = findViewById(R.id.login);
        login.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginDriver.class);
            startActivity(intent);
        });

    }
}