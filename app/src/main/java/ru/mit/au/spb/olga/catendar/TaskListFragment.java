package ru.mit.au.spb.olga.catendar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by olga on 12.12.15.
 */
public class TaskListFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private ArrayList<Task> taskList = new ArrayList<>();
    private ExpandableListView listOfTask;

    private boolean showAll;

    private ShakeListener mShaker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.activity_task_list, container, false);

        listOfTask = (ExpandableListView) rootView.findViewById(R.id.toDoExpandableListView);

        mDatabaseHelper = new DatabaseHelper(getContext(), "mydatabase13.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        synchronizedWithDateBase();
        drawTaskList();

        /*Switch mSwitch = (Switch)rootView.findViewById(R.id.switchShowAll);
        if (mSwitch != null) {
            mSwitch.setOnCheckedChangeListener(this);
        }*/

        mShaker = new ShakeListener(getActivity());
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                //synchronizedWithDateBase();
                drawTaskList();
            }
        });
        return rootView;
    }
    @Override
    public void onResume()
    {
        mShaker.resume();
        super.onResume();
    }
    @Override
    public void onPause()
    {
        mShaker.pause();
        super.onPause();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        showAll = isChecked;
        //synchronizedWithDateBase();
        drawTaskList();
    }

    private void drawTaskList() {
        ExpListAdapter adapter;
        adapter = new ExpListAdapter(getActivity().getApplicationContext(), taskList, mSQLiteDatabase);
        listOfTask.setAdapter(adapter);
    }


    private void synchronizedWithDateBase() {
        taskList.clear();

        Cursor cursor = mSQLiteDatabase.query("tasks", new String[]{DatabaseHelper._ID,
                        DatabaseHelper.TASK_NAME_COLUMN,
                        DatabaseHelper.TASK_PRIORITY,
                        DatabaseHelper.TASK_COMMENT,
                        DatabaseHelper.TASK_DURATION,
                        DatabaseHelper.TASK_START_TIME,
                        DatabaseHelper.TASK_DEADLINE,
                        DatabaseHelper.TASK_IS_DONE},
                null, null,
                null, null, null);

        while (cursor.moveToNext()) {
            Task currentTask = new Task();

            currentTask.changeText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_NAME_COLUMN)));
            currentTask.setComment(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_COMMENT)));
            currentTask.changeIsDone(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_IS_DONE)) == 1);
            currentTask.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID)));

            taskList.add(currentTask);
        }
        cursor.close();
    }
}
