package com.example.schoolbus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginDriver extends AppCompatActivity {
    private final String pass = "159753";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_driver);

        Button signIn = findViewById(R.id.signIn);
        EditText id = findViewById(R.id.password_driver);

        signIn.setOnClickListener(view -> {
            if (id.getText().toString().equals(pass)) {
                Intent intent = new Intent(getApplicationContext(), DriverHome.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Wrong ID", Toast.LENGTH_SHORT).show();
            }
        });


    }
}