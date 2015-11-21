package ru.mit.au.spb.olga.catendar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by olga on 26.10.15.
 */
public class TaskListActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private ArrayList<Event> eventList = new ArrayList<>();
    private ExpandableListView listOfEvent;

    private void drawTaskList() {
        Map<String, String> map;

        ArrayList<Map<String, String>> groupDataList = new ArrayList<>();

        for (Event cur: eventList) {
            map = new HashMap<>();
            map.put("groupName", cur.getEventText());
            groupDataList.add(map);
        }

        String groupFrom[] = new String[]{"groupName"};
        int groupTo[] = new int[]{android.R.id.text1};

        ArrayList<ArrayList<Map<String, String>>> сhildDataList = new ArrayList<>();

        ArrayList<Map<String, String>> сhildDataItemList;
        for (int i = 0; i < eventList.size(); ++i) {
            сhildDataItemList = new ArrayList<>();
            Event currentEvent = eventList.get(i);
            for (Task currentTask : currentEvent.getTaskList()) {
                map = new HashMap<>();
                map.put("taskName", currentTask.getTaskText());
                сhildDataItemList.add(map);
            }
            сhildDataList.add(сhildDataItemList);
        }

        String childFrom[] = new String[]{"taskName"};
        int childTo[] = new int[]{android.R.id.text1};

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                this, groupDataList,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, сhildDataList, android.R.layout.simple_list_item_1,
                childFrom, childTo);

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
            currentEvent.changeText(cursor.getString(cursor
                    .getColumnIndex(DatabaseHelper.EVENT_NAME)));

            giveEventById.put(id, currentEvent);

        }

        cursor.close();

        cursor = mSQLiteDatabase.query("tasks", new String[]{DatabaseHelper._ID, DatabaseHelper.TASK_NAME_COLUMN,
                        DatabaseHelper.TASK_PARENT_EVENT_ID},
                null, null,
                null, null, null);

        while (cursor.moveToNext()) {
            Task currentTask = new Task();
            int parentEventId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_PARENT_EVENT_ID));
            currentTask.changeText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_NAME_COLUMN)));

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

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        synchronizedWithDateBase();
        drawTaskList();
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
}