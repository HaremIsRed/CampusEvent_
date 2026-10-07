package com.example.campusevent;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmationActivity extends AppCompatActivity {
    private TextView txtName, txtRegNo, txtEmail, txtPhone, txtProgramme;
    private TextView txtEvent, txtStatus;
    private Button btnHome;
    private long registrationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        txtName = findViewById(R.id.txtName);
        txtRegNo = findViewById(R.id.txtRegNo);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtProgramme = findViewById(R.id.txtProgramme);
        txtEvent = findViewById(R.id.txtEvent);
        txtStatus = findViewById(R.id.txtStatus);
        btnHome = findViewById(R.id.btnHome);

        registrationId = getIntent().getLongExtra(
                RegistrationActivity.EXTRA_REGISTRATION_ID, -1L);
        if (registrationId >= 0) loadRegistration(registrationId);

        btnHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });
    }

    private void loadRegistration(long id) {
        Uri itemUri = Uri.withAppendedPath(CampusEventContract.RegistrationEntry.CONTENT_URI,
                String.valueOf(id));
        try (Cursor cursor = getContentResolver().query(itemUri, null, null, null, null)) {
            if (cursor == null || !cursor.moveToFirst()) {
                txtStatus.setText("Registration not found");
                return;
            }
            txtName.setText(read(cursor, CampusEventContract.RegistrationEntry.COLUMN_NAME));
            txtRegNo.setText(read(cursor, CampusEventContract.RegistrationEntry.COLUMN_REG_NO));
            txtEmail.setText(read(cursor, CampusEventContract.RegistrationEntry.COLUMN_EMAIL));
            txtPhone.setText(read(cursor, CampusEventContract.RegistrationEntry.COLUMN_PHONE));
            txtProgramme.setText(read(cursor, CampusEventContract.RegistrationEntry.COLUMN_PROGRAMME));
            txtEvent.setText(read(cursor, CampusEventContract.RegistrationEntry.COLUMN_EVENT_NAME));
            txtStatus.setText("Registration Successful · ID " + id);
        }
    }

    private String read(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndexOrThrow(column));
    }
}
