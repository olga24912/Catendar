package ru.mit.au.spb.olga.catendar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
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
public class TaskListFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private ArrayList<Event> eventList = new ArrayList<>();
    private ExpandableListView listOfEvent;

    private boolean showAll;

    private ShakeListener mShaker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.activity_task_list, container, false);

        listOfEvent = (ExpandableListView) rootView.findViewById(R.id.expandableListView);

        mDatabaseHelper = new DatabaseHelper(getContext(), "mydatabase10.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        synchronizedWithDateBase();
        drawTaskList();

        Switch mSwitch = (Switch)rootView.findViewById(R.id.switchShowAll);
        if (mSwitch != null) {
            mSwitch.setOnCheckedChangeListener(this);
        }

        Button onCreateTask = (Button) rootView.findViewById(R.id.add_task);

        onCreateTask.setOnClickListener((View.OnClickListener) this);

        mShaker = new ShakeListener(getActivity());
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                synchronizedWithDateBase();
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
        synchronizedWithDateBase();
        drawTaskList();
    }

    static final private int CREATE_TASK = 0;
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), CreateTaskActivity.class);
        startActivityForResult(intent, CREATE_TASK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_TASK) {
            if (resultCode == getActivity().RESULT_OK) {
                synchronizedWithDateBase();
                drawTaskList();
            }
        }
    }


    private void drawTaskList() {
        ArrayList<Event> eventListWithTasks = new ArrayList<>();
        for (Event ev : eventList) {
            Event newEvent = new Event();
            newEvent.setText(ev.getText());
            for (Task tk : ev.getTaskList()) {
                if (!tk.getIsDone()) {
                    newEvent.addTask(tk);
                }
            }
            if (newEvent.getTaskList().size() > 0) {
                eventListWithTasks.add(newEvent);
            }
        }

        ExpListAdapter adapter;
        if (!showAll) {
            adapter = new ExpListAdapter(getActivity().getApplicationContext(), eventListWithTasks, mSQLiteDatabase);
        } else {
            adapter = new ExpListAdapter(getActivity().getApplicationContext(), eventList, mSQLiteDatabase);
        }
        listOfEvent.setAdapter(adapter);
    }


    private void synchronizedWithDateBase() {
        Map<Integer, Event> giveEventById = new TreeMap<>();

        Cursor cursor = mSQLiteDatabase.query("events", new String[]{DatabaseHelper._ID, DatabaseHelper.EVENT_NAME,
                        DatabaseHelper.EVENT_PARENT_TEMPLATE},
                null, null,
                null, null, null) ;

        while (cursor.moveToNext()) {
            Event currentEvent = new Event();
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            currentEvent.setText(cursor.getString(cursor
                    .getColumnIndex(DatabaseHelper.EVENT_NAME)));

            giveEventById.put(id, currentEvent);

        }

        cursor.close();

        cursor = mSQLiteDatabase.query("tasks", new String[]{DatabaseHelper._ID, DatabaseHelper.TASK_NAME_COLUMN,
                        DatabaseHelper.TASK_PARENT_EVENT_ID,DatabaseHelper.TASK_IS_DONE},
                null, null,
                null, null, null);

        while (cursor.moveToNext()) {
            Task currentTask = new Task();
            int parentEventId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_PARENT_EVENT_ID));
            currentTask.changeText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_NAME_COLUMN)));
            currentTask.changeIsDone(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_IS_DONE)) == 1);
            currentTask.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID)));

            Event eventOfThisTask = giveEventById.get(parentEventId);
            if (eventOfThisTask != null) {
                eventOfThisTask.addTask(currentTask);

                giveEventById.put(parentEventId, eventOfThisTask);
            }
        }

        eventList.clear();
        for (Event event: giveEventById.values()) {
            eventList.add(event);
        }
    }
}
