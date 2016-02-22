package ru.mit.au.spb.olga.catendar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by olga on 12.12.15.
 */
public class TaskListFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private ArrayList<Task> taskList = new ArrayList<>();
    private ExpandableListView listOfTask;

    private ArrayList<String> heapName = new ArrayList<>();
    private ArrayList<Long> heapId = new ArrayList<>();

    private boolean showAll;

    private int heap_id = -1;

    private ShakeListener mShaker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.activity_task_list, container, false);

        listOfTask = (ExpandableListView) rootView.findViewById(R.id.toDoExpandableListView);

        mDatabaseHelper = new DatabaseHelper(getContext(), "mydatabase14.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        synchronizedWithDataBase();
        drawTaskList();

        initHeapList();

        mShaker = new ShakeListener(getActivity());
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                synchronizedWithDataBase();
                drawTaskList();
            }
        });

        TextView changePlan = (TextView) rootView.findViewById(R.id.toDoTextView);
        changePlan.setOnClickListener(viewClickListener);

        return rootView;
    }

    View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }
    };

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this.getActivity(), v);
        popupMenu.inflate(R.menu.popupmenu);
        for (String nm: heapName) {
            popupMenu.getMenu().add(0, 0, 0, nm);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        return true;
                    case R.id.menu1:
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                synchronizedWithDataBase();
                drawTaskList();
            }
        });
        popupMenu.show();
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
        synchronizedWithDataBase();
        drawTaskList();
    }

    private void drawTaskList() {
        ExpListAdapter adapter;
        adapter = new ExpListAdapter(getActivity().getApplicationContext(), taskList, mSQLiteDatabase);
        listOfTask.setAdapter(adapter);
    }


    private void synchronizedWithDataBase() {
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

            GregorianCalendar curDate = new GregorianCalendar();
            curDate.setTimeInMillis(1000*cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_DEADLINE)));

            currentTask.setDeadlineTime(curDate);

            curDate.setTimeInMillis(1000 * cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_DURATION)));
            currentTask.setDuration(curDate);

            currentTask.setPriority(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_PRIORITY)));

            curDate.setTimeInMillis(1000 * cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_START_TIME)));
            currentTask.setStartTime(curDate);

            taskList.add(currentTask);
        }
        cursor.close();
    }

    private void initHeapList() {
        heapId.clear();
        heapName.clear();

        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_HEAP,
                new String[]{DatabaseHelper._ID,
                        DatabaseHelper.HEAP_NAME,
                        DatabaseHelper.HEAP_DATE},
                null, null,
                null, null, null);

        while (cursor.moveToNext()) {
            heapId.add(cursor.getLong(cursor.getColumnIndex(DatabaseHelper._ID)));
            heapName.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.HEAP_NAME)));
        }
        cursor.close();
    }
}
