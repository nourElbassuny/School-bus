package com.example.schoolbus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ChoosePersonality extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_personality);

        Button driver = findViewById(R.id.driver);
        Button parent = findViewById(R.id.parent);
        driver.setOnClickListener(view -> {
            Intent intent = new Intent(ChoosePersonality.this, LoginDriver.class);
            startActivity(intent);

        });
        parent.setOnClickListener(view -> {
            Intent intent = new Intent(ChoosePersonality.this, LoginParent.class);
            startActivity(intent);
        });
    }
}