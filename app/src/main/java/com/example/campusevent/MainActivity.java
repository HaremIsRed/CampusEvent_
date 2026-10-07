package com.example.campusevent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnRegister;
    private Button btnViewRegistrations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO 1: Connect btnRegister to XML
        btnRegister = findViewById(R.id.btnRegister);

        // TODO 2: Add click event to open RegistrationActivity
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        btnViewRegistrations = findViewById(R.id.btnViewRegistrations);
        btnViewRegistrations.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegistrationListActivity.class)));
    }
}
