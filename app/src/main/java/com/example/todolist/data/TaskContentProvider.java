package com.example.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;

public class TaskContentProvider extends ContentProvider {

    public static final int TASKS = 100;
    public static final int TASK_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS + "/# ", TASK_WITH_ID);

        return uriMatcher;
    }

    private TaskDbHelper mTaskDbHelper;

    @Override
    public boolean onCreate() {


        Context context = getContext();
        mTaskDbHelper = new TaskDbHelper(context);
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case TASKS:

                long id = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder){

        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case TASKS:
                retCursor = db.query(TaskContract.TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int tasksDeleted;

        switch (match) {
            case TASK_WITH_ID:
                String id = uri.getPathSegments().get(1);

                tasksDeleted = db.delete(TaskContract.TaskEntry.TABLE_NAME, "_id=? ", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("not yet implemented ");
    }

    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented ");

    }
}
