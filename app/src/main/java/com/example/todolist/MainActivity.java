package com.example.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.data.TaskContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;

    private CustomCursor mCursor;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTasks);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCursor = new CustomCursor(this);
        mRecyclerView.setAdapter(mCursor);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                int id = (int) viewHolder.itemView.getTag();

                String stringId = Integer.toString(id);
                Uri uri = TaskContract.TaskEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                getContentResolver().delete(uri, null, null);

                LoaderManager.getInstance(MainActivity.this).restartLoader(TASK_LOADER_ID,null, MainActivity.this);

            }
        }).attachToRecyclerView(mRecyclerView);

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent addTaskIntent = new Intent(MainActivity.this, AddTask.class);
                startActivity(addTaskIntent);

            }
        });

        LoaderManager.getInstance(this).initLoader(TASK_LOADER_ID, null, this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        LoaderManager.getInstance(this).restartLoader(TASK_LOADER_ID, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs){

        return new AsyncTaskLoader<Cursor>(this){

            Cursor mTaskData = null;

            @Override
            protected void onStartLoading(){
                if (mTaskData != null){
                    deliverResult(mTaskData);
                }else {
                    forceLoad();
                }
            }
            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            TaskContract.TaskEntry.COLUMN_PRIORITY);
                }catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){

        mCursor.swapCursor(data);
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mCursor.swapCursor(null);
    }
}

