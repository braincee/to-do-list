package com.example.todolist.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class TaskContract {
    public static final String AUTHORITY = "com.example.android.todolist";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TASKS = "tasks";

    public static final class TaskEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String TABLE_NAME = "tasks";

        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRIORITY = "priority";


    }
}
