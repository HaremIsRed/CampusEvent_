package com.example.campusevent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

final class RegistrationDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "campus_event.db";
    private static final int DATABASE_VERSION = 1;

    RegistrationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CampusEventContract.RegistrationEntry.TABLE_NAME + " ("
                + CampusEventContract.RegistrationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CampusEventContract.RegistrationEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + CampusEventContract.RegistrationEntry.COLUMN_REG_NO + " TEXT NOT NULL, "
                + CampusEventContract.RegistrationEntry.COLUMN_EMAIL + " TEXT NOT NULL, "
                + CampusEventContract.RegistrationEntry.COLUMN_PHONE + " TEXT NOT NULL, "
                + CampusEventContract.RegistrationEntry.COLUMN_PROGRAMME + " TEXT NOT NULL, "
                + CampusEventContract.RegistrationEntry.COLUMN_EVENT_NAME + " TEXT NOT NULL"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add a migration here if the registration table schema changes.
    }
}
