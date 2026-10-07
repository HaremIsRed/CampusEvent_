package com.example.campusevent;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationListActivity extends AppCompatActivity {
    private LinearLayout registrationsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Event Registrations");

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(getColor(R.color.bg_dark));

        registrationsContainer = new LinearLayout(this);
        registrationsContainer.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(20);
        registrationsContainer.setPadding(padding, padding, padding, padding);
        scrollView.addView(registrationsContainer);
        setContentView(scrollView);
        loadRegistrations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (registrationsContainer != null) loadRegistrations();
    }

    private void loadRegistrations() {
        registrationsContainer.removeAllViews();
        TextView heading = new TextView(this);
        heading.setText("Saved registrations");
        heading.setTextColor(getColor(R.color.text_primary));
        heading.setTextSize(24);
        heading.setTypeface(null, android.graphics.Typeface.BOLD);
        registrationsContainer.addView(heading, matchWrap());

        int count = 0;
        try (Cursor cursor = getContentResolver().query(
                CampusEventContract.RegistrationEntry.CONTENT_URI, null, null, null, null)) {
            if (cursor != null) {
                count = cursor.getCount();
                while (cursor.moveToNext()) addRegistrationCard(cursor);
            }
        }

        if (count == 0) {
            TextView empty = new TextView(this);
            empty.setText("No registrations yet. Tap Register Now to create one.");
            empty.setTextColor(getColor(R.color.text_secondary));
            empty.setTextSize(16);
            empty.setPadding(0, dp(24), 0, dp(24));
            registrationsContainer.addView(empty, matchWrap());
        }
    }

    private void addRegistrationCard(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(
                CampusEventContract.RegistrationEntry._ID));
        String name = read(cursor, CampusEventContract.RegistrationEntry.COLUMN_NAME);
        String regNo = read(cursor, CampusEventContract.RegistrationEntry.COLUMN_REG_NO);
        String email = read(cursor, CampusEventContract.RegistrationEntry.COLUMN_EMAIL);
        String phone = read(cursor, CampusEventContract.RegistrationEntry.COLUMN_PHONE);
        String programme = read(cursor, CampusEventContract.RegistrationEntry.COLUMN_PROGRAMME);
        String event = read(cursor, CampusEventContract.RegistrationEntry.COLUMN_EVENT_NAME);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(12));
        card.setBackgroundColor(getColor(R.color.card_dark));
        LinearLayout.LayoutParams cardParams = matchWrap();
        cardParams.topMargin = dp(14);
        registrationsContainer.addView(card, cardParams);

        TextView details = new TextView(this);
        details.setText(name + "  ·  " + regNo
                + "\n" + email
                + "\n" + phone + "  ·  " + programme
                + "\n" + event + "  ·  Record #" + id);
        details.setTextColor(getColor(R.color.text_primary));
        details.setTextSize(15);
        details.setLineSpacing(dp(4), 1.0f);
        card.addView(details, matchWrap());

        LinearLayout actions = new LinearLayout(this);
        actions.setGravity(Gravity.END);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams actionsParams = matchWrap();
        actionsParams.topMargin = dp(8);
        card.addView(actions, actionsParams);

        Button edit = new Button(this);
        edit.setText("EDIT");
        edit.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistrationActivity.class);
            intent.putExtra(RegistrationActivity.EXTRA_REGISTRATION_ID, id);
            startActivity(intent);
        });
        actions.addView(edit, new LinearLayout.LayoutParams(0, dp(48), 1));

        Button delete = new Button(this);
        delete.setText("DELETE");
        delete.setTextColor(getColor(R.color.error_red));
        delete.setOnClickListener(v -> confirmDelete(id, name));
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(0, dp(48), 1);
        deleteParams.leftMargin = dp(8);
        actions.addView(delete, deleteParams);
    }

    private void confirmDelete(long id, String name) {
        new AlertDialog.Builder(this)
                .setTitle("Delete registration?")
                .setMessage("Delete the registration for " + name + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    Uri itemUri = Uri.withAppendedPath(
                            CampusEventContract.RegistrationEntry.CONTENT_URI,
                            String.valueOf(id));
                    getContentResolver().delete(itemUri, null, null);
                    loadRegistrations();
                })
                .show();
    }

    private String read(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndexOrThrow(column));
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
