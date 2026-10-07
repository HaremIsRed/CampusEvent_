package com.example.campusevent;

import android.net.Uri;
import android.provider.BaseColumns;

public final class CampusEventContract {
    private CampusEventContract() { }

    public static final String CONTENT_AUTHORITY = "com.example.campusevent.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_REGISTRATIONS = "registrations";

    public static final class RegistrationEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REGISTRATIONS)
                .build();

        public static final String TABLE_NAME = "registrations";
        public static final String COLUMN_NAME = "student_name";
        public static final String COLUMN_REG_NO = "registration_number";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_PROGRAMME = "programme";
        public static final String COLUMN_EVENT_NAME = "event_name";

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.example.campusevent.registration";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.example.campusevent.registration";
    }
}
