package ru.mit.au.spb.olga.catendar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashSet;

import static java.util.Collections.sort;

/**
 * Created by olga on 12.12.15.
 */
public class TaskListFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private class cmpDuration implements Comparator<Task> {
        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.getDurationTimeInSecond() - rhs.getDurationTimeInSecond() < 0) {
                return -1;
            } else if (lhs.getDurationTimeInSecond() - rhs.getDurationTimeInSecond() == 0) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    private class cmpDeadline implements Comparator<Task> {
        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.getDeadlineTimeInSecond() - rhs.getDeadlineTimeInSecond() < 0) {
                return -1;
            } else if (lhs.getDeadlineTimeInSecond() - rhs.getDeadlineTimeInSecond() == 0) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    private class cmpPriority implements Comparator<Task> {
        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.getPriority() - rhs.getPriority() > 0) {
                return -1;
            } else if (lhs.getPriority() - rhs.getPriority() == 0) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    private Comparator<Task> cmp = new cmpDeadline();

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private ArrayList<Task> taskList = new ArrayList<>();
    private ExpandableListView listOfTask;

    private ArrayList<String> heapName = new ArrayList<>();
    private ArrayList<Long> heapId = new ArrayList<>();

    private boolean showAll;

    private long heap_id = -1;

    private ShakeListener mShaker;

    private TextView planTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.activity_task_list, container, false);

        listOfTask = (ExpandableListView) rootView.findViewById(R.id.toDoExpandableListView);

        planTitle = (TextView) rootView.findViewById(R.id.toDoTextView);

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

        setHasOptionsMenu(true);

        Button DeleteButton = (Button) rootView.findViewById(R.id.toDoButtonDelete);
        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (heap_id == -1) {
                    return;
                } else {
                    mSQLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE_TASK_HEAP,
                            DatabaseHelper.TASK_HEAP_HEAP_ID + " = " + heap_id, null);

                    mSQLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE_HEAP,
                            DatabaseHelper._ID + " = " + heap_id, null);
                    heap_id = -1;
                    initHeapList();
                    synchronizedWithDataBase();
                    drawTaskList();
                }
            }
        });

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
        for (int i = 0; i < heapName.size(); ++i) {
            String nm = heapName.get(i);
            popupMenu.getMenu().add(0, i + 1, 0, nm).getItemId();
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu1) {
                    heap_id = -1;
                    return true;
                } else if (0 < item.getItemId() && item.getItemId() <= heapId.size()) {
                    heap_id = heapId.get(item.getItemId() - 1);
                    planTitle.setText(heapName.get(item.getItemId() - 1));
                    return true;
                } else {
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
        sort(taskList, cmp);
        ExpListAdapter adapter;
        adapter = new ExpListAdapter(getActivity().getApplicationContext(), taskList, mSQLiteDatabase);
        listOfTask.setAdapter(adapter);
    }


    private HashSet<Long> taskIDHeap = new HashSet<>();

    private void createTaskIDHeap () {
        taskIDHeap.clear();
        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TASK_HEAP,
                new String[]{DatabaseHelper._ID,
                        DatabaseHelper.TASK_HEAP_TASK_ID,
                        DatabaseHelper.TASK_HEAP_HEAP_ID},
                DatabaseHelper.TASK_HEAP_HEAP_ID + "=" + heap_id, null,
                null, null, null);
        int val = cursor.getCount();
        while (cursor.moveToNext()) {
            taskIDHeap.add(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.TASK_HEAP_TASK_ID)));
        }
        cursor.close();
    }

    private void synchronizedWithDataBase() {
        createTaskIDHeap();

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
            currentTask.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper._ID)));

            Long idInHeap = currentTask.getId();
            if (!(heap_id == -1 || taskIDHeap.contains(idInHeap))) {
                continue;
            }

            GregorianCalendar curDate = new GregorianCalendar();
            curDate.setTimeInMillis(1000 * cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_DEADLINE)));

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_deadline:
                cmp = new cmpDeadline();
                drawTaskList();
                return true;
            case R.id.action_duration:
                cmp = new cmpDuration();
                drawTaskList();
                return true;
            case R.id.action_not_show_all:
                showAll = false;
                return true;
            case R.id.action_show_all:
                showAll = true;
                return true;
            case R.id.action_priority:
                cmp = new cmpPriority();
                drawTaskList();
                return true;
            default:
                break;
        }

        return false;
    }
}
