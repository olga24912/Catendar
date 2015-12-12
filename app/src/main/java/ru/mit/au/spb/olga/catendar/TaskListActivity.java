package ru.mit.au.spb.olga.catendar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by olga on 26.10.15.
 */
public class TaskListActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private ArrayList<Event> eventList = new ArrayList<>();
    private ExpandableListView listOfEvent;

    private boolean showAll;

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
            adapter = new ExpListAdapter(getApplicationContext(), eventListWithTasks, mSQLiteDatabase);
        } else {
            adapter = new ExpListAdapter(getApplicationContext(), eventList, mSQLiteDatabase);
        }
        listOfEvent.setAdapter(adapter);
    }


    private void synchronizedWithDateBase() {
        Map <Integer, Event> giveEventById = new TreeMap<>();

        Cursor cursor = mSQLiteDatabase.query("events", new String[]{DatabaseHelper._ID, DatabaseHelper.EVENT_NAME,
                        DatabaseHelper.EVENT_PARENT_CALENDAR},
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_list);

        listOfEvent = (ExpandableListView) findViewById(R.id.expandableListView);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase6.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        synchronizedWithDateBase();
        drawTaskList();

        Switch mSwitch = (Switch)findViewById(R.id.switchShowAll);
        if (mSwitch != null) {
            mSwitch.setOnCheckedChangeListener(this);
        }
    }

    public void onCancelTaskListClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    static final private int CREATE_TASK = 0;
    public void onCreateTaskClick(View view) {
        Intent intent = new Intent(TaskListActivity.this, CreateTaskActivity.class);
        startActivityForResult(intent, CREATE_TASK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_TASK) {
            if (resultCode == RESULT_OK) {
                synchronizedWithDateBase();
                drawTaskList();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        showAll = isChecked;
        synchronizedWithDateBase();
        drawTaskList();
    }
}