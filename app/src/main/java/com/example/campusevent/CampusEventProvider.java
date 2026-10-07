package com.example.campusevent;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CampusEventProvider extends ContentProvider {
    private static final int REGISTRATIONS = 100;
    private static final int REGISTRATION_ID = 101;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    private RegistrationDbHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(CampusEventContract.CONTENT_AUTHORITY,
                CampusEventContract.PATH_REGISTRATIONS, REGISTRATIONS);
        matcher.addURI(CampusEventContract.CONTENT_AUTHORITY,
                CampusEventContract.PATH_REGISTRATIONS + "/#", REGISTRATION_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        if (getContext() == null) return false;
        dbHelper = new RegistrationDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String querySelection = selection;
        String[] queryArgs = selectionArgs;

        if (URI_MATCHER.match(uri) == REGISTRATION_ID) {
            String id = uri.getLastPathSegment();
            querySelection = appendSelection(selection,
                    CampusEventContract.RegistrationEntry._ID + "=?");
            queryArgs = appendArgument(selectionArgs, id);
        } else if (URI_MATCHER.match(uri) != REGISTRATIONS) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = db.query(CampusEventContract.RegistrationEntry.TABLE_NAME,
                projection, querySelection, queryArgs, null, null,
                TextUtils.isEmpty(sortOrder)
                        ? CampusEventContract.RegistrationEntry._ID + " DESC"
                        : sortOrder);
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case REGISTRATIONS:
                return CampusEventContract.RegistrationEntry.CONTENT_TYPE;
            case REGISTRATION_ID:
                return CampusEventContract.RegistrationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (URI_MATCHER.match(uri) != REGISTRATIONS) {
            throw new IllegalArgumentException("Insert requires the registrations URI: " + uri);
        }
        if (values == null) throw new IllegalArgumentException("Registration values are required");

        long id = dbHelper.getWritableDatabase().insertOrThrow(
                CampusEventContract.RegistrationEntry.TABLE_NAME, null, values);
        Uri insertedUri = ContentUris.withAppendedId(
                CampusEventContract.RegistrationEntry.CONTENT_URI, id);
        notifyChange(insertedUri);
        return insertedUri;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        if (values == null) return 0;
        int match = URI_MATCHER.match(uri);
        String updateSelection = selection;
        String[] updateArgs = selectionArgs;
        if (match == REGISTRATION_ID) {
            updateSelection = appendSelection(selection,
                    CampusEventContract.RegistrationEntry._ID + "=?");
            updateArgs = appendArgument(selectionArgs, uri.getLastPathSegment());
        } else if (match != REGISTRATIONS) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        int count = dbHelper.getWritableDatabase().update(
                CampusEventContract.RegistrationEntry.TABLE_NAME,
                values, updateSelection, updateArgs);
        if (count > 0) notifyChange(uri);
        return count;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);
        String deleteSelection = selection;
        String[] deleteArgs = selectionArgs;
        if (match == REGISTRATION_ID) {
            deleteSelection = appendSelection(selection,
                    CampusEventContract.RegistrationEntry._ID + "=?");
            deleteArgs = appendArgument(selectionArgs, uri.getLastPathSegment());
        } else if (match != REGISTRATIONS) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        int count = dbHelper.getWritableDatabase().delete(
                CampusEventContract.RegistrationEntry.TABLE_NAME,
                deleteSelection, deleteArgs);
        if (count > 0) notifyChange(uri);
        return count;
    }

    private void notifyChange(Uri uri) {
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(
                    CampusEventContract.RegistrationEntry.CONTENT_URI, null);
        }
    }

    private static String appendSelection(@Nullable String selection, String condition) {
        return TextUtils.isEmpty(selection) ? condition : "(" + selection + ") AND " + condition;
    }

    private static String[] appendArgument(@Nullable String[] args, String value) {
        if (args == null || args.length == 0) return new String[]{value};
        String[] combined = new String[args.length + 1];
        System.arraycopy(args, 0, combined, 0, args.length);
        combined[args.length] = value;
        return combined;
    }
}
