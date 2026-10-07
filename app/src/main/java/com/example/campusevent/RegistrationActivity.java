package com.example.campusevent;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class RegistrationActivity extends AppCompatActivity {
    public static final String EXTRA_REGISTRATION_ID = "registration_id";
    private static final String EVENT_NAME = "Tech Innovation Day 2026";

    private EditText edtName, edtRegNo, edtEmail, edtPhone, edtProgramme;
    private Button btnSubmit;
    private long registrationId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        edtName = findViewById(R.id.edtName);
        edtRegNo = findViewById(R.id.edtRegNo);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtProgramme = findViewById(R.id.edtProgramme);
        btnSubmit = findViewById(R.id.btnSubmit);

        registrationId = getIntent().getLongExtra(EXTRA_REGISTRATION_ID, -1L);
        if (registrationId >= 0) {
            setTitle("Edit Registration");
            btnSubmit.setText("UPDATE REGISTRATION");
            loadRegistration(registrationId);
        }
        btnSubmit.setOnClickListener(v -> saveRegistration());
    }

    private void loadRegistration(long id) {
        Uri itemUri = Uri.withAppendedPath(CampusEventContract.RegistrationEntry.CONTENT_URI,
                String.valueOf(id));
        try (Cursor cursor = getContentResolver().query(itemUri, null, null, null, null)) {
            if (cursor == null || !cursor.moveToFirst()) {
                showError("This registration could not be found");
                finish();
                return;
            }
            edtName.setText(read(cursor, CampusEventContract.RegistrationEntry.COLUMN_NAME));
            edtRegNo.setText(read(cursor, CampusEventContract.RegistrationEntry.COLUMN_REG_NO));
            edtEmail.setText(read(cursor, CampusEventContract.RegistrationEntry.COLUMN_EMAIL));
            edtPhone.setText(read(cursor, CampusEventContract.RegistrationEntry.COLUMN_PHONE));
            edtProgramme.setText(read(cursor, CampusEventContract.RegistrationEntry.COLUMN_PROGRAMME));
        }
    }

    private String read(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndexOrThrow(column));
    }

    private void saveRegistration() {
        String name = edtName.getText().toString().trim();
        String regNo = edtRegNo.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String programme = edtProgramme.getText().toString().trim();

        if (!validateInput(name, regNo, email, phone, programme)) return;

        ContentValues values = new ContentValues();
        values.put(CampusEventContract.RegistrationEntry.COLUMN_NAME, name);
        values.put(CampusEventContract.RegistrationEntry.COLUMN_REG_NO, regNo);
        values.put(CampusEventContract.RegistrationEntry.COLUMN_EMAIL, email);
        values.put(CampusEventContract.RegistrationEntry.COLUMN_PHONE, phone);
        values.put(CampusEventContract.RegistrationEntry.COLUMN_PROGRAMME, programme);
        values.put(CampusEventContract.RegistrationEntry.COLUMN_EVENT_NAME, EVENT_NAME);

        if (registrationId >= 0) {
            Uri itemUri = Uri.withAppendedPath(CampusEventContract.RegistrationEntry.CONTENT_URI,
                    String.valueOf(registrationId));
            int updated = getContentResolver().update(itemUri, values, null, null);
            if (updated == 0) {
                showError("The registration was not updated");
                return;
            }
        } else {
            Uri resultUri = getContentResolver().insert(
                    CampusEventContract.RegistrationEntry.CONTENT_URI, values);
            if (resultUri == null) {
                showError("The registration could not be saved");
                return;
            }
            registrationId = Long.parseLong(resultUri.getLastPathSegment());
        }

        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(EXTRA_REGISTRATION_ID, registrationId);
        startActivity(intent);
        finish();
    }

    private boolean validateInput(String name, String regNo, String email,
                                  String phone, String programme) {
        if (name.isEmpty()) return showInvalid("Full name is required");
        if (regNo.isEmpty()) return showInvalid("Student registration number is required");
        if (email.isEmpty()) return showInvalid("Email is required");
        if (phone.isEmpty()) return showInvalid("Phone number is required");
        if (programme.isEmpty()) return showInvalid("Programme is required");
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return showInvalid("Please enter a valid email address");
        }
        if (!phone.matches("^\\+?[0-9]{9,11}$")) {
            return showInvalid("Please enter a valid phone number");
        }
        return true;
    }

    private boolean showInvalid(String message) {
        showError(message);
        return false;
    }

    private void showError(String message) {
        Snackbar.make(btnSubmit, message, Snackbar.LENGTH_SHORT).show();
    }
}
