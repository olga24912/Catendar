package ru.mit.au.spb.olga.catendar.view.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import ru.mit.au.spb.olga.catendar.R;
import ru.mit.au.spb.olga.catendar.model.DatabaseHelper;
import ru.mit.au.spb.olga.catendar.model.Task;

public class ExpListAdapter extends BaseExpandableListAdapter {
    private ArrayList<Task> mGroups;
    private Context mContext;

    private SQLiteDatabase mSQLiteDatabase;


    public ExpListAdapter (Context context, ArrayList<Task> groups, SQLiteDatabase sQLiteDatabase) {
        mContext = context;
        mGroups = groups;

        mSQLiteDatabase = sQLiteDatabase;
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }


    @NotNull
    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @NotNull
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @NotNull
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_view, null);
        }

        TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
        textGroup.setText(mGroups.get(groupPosition).getText());

        if (mGroups.get(groupPosition).isDone()) {
            textGroup.setBackgroundColor(0xffc5e384);
        }

        return convertView;
    }

    @NotNull
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_view, null);
        }

        final TextView textPriority = (TextView) convertView.findViewById(R.id.itemToDoTextViewPriority);
        final TextView textDuration = (TextView) convertView.findViewById(R.id.itemToDoTextViewDuration);
        final TextView textDeadlineTime = (TextView) convertView.findViewById(R.id.itemToDoTextViewTime);
        final TextView textComment = (TextView) convertView.findViewById(R.id.itemToDoTextViewComment);

        textComment.setText(mGroups.get(groupPosition).getComment());
        if (mGroups.get(groupPosition).getDeadlineTimeInSeconds() >
                (new GregorianCalendar()).getTimeInMillis()/1000 + 50 * 365 * 24 * 60 * 60) {
            textDeadlineTime.setText("Deadline time: -");
        } else {
            textDeadlineTime.setText("Deadline time:" + mGroups.get(groupPosition).getStringDeadlineTime());
        }
        textPriority.setText("Priority: " + String.valueOf(mGroups.get(groupPosition).getPriority()));
        textDuration.setText("Duration: " + mGroups.get(groupPosition).getStringDuration());

        Button buttonDelete = (Button)convertView.findViewById(R.id.itemToDoButtonDelete);
        Button buttonDone = (Button)convertView.findViewById(R.id.itemToDoToggleButtonDone);
        Button buttonChange = (Button)convertView.findViewById(R.id.itemToDoButtonChange);

        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, CreateTaskActivity.class);

                intent.putExtra("id", mGroups.get(groupPosition).getId());
                mContext.startActivity(intent);
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textPriority.setBackgroundColor(0xffc5e384);

                Long dataBaseId = mGroups.get(groupPosition).getId();

                mSQLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE_TASK, DatabaseHelper._ID + "=" + dataBaseId, null);
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPriority.setBackgroundColor(0xffc5e384);

                Long dataBaseId = mGroups.get(groupPosition).getId();

                Task currentTask = mGroups.get(groupPosition);

                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.TASK_NAME_COLUMN, currentTask.getText());
                cv.put(DatabaseHelper.TASK_COMMENT, currentTask.getComment());
                cv.put(DatabaseHelper.TASK_START_TIME, currentTask.getStartDateInSeconds());
                cv.put(DatabaseHelper.TASK_DEADLINE, currentTask.getDeadlineTimeInSeconds());
                cv.put(DatabaseHelper.TASK_DURATION, currentTask.getDurationTimeInSeconds());
                cv.put(DatabaseHelper.TASK_IS_DONE, !currentTask.isDone());
                cv.put(DatabaseHelper.TASK_PRIORITY, currentTask.getPriority());

                mSQLiteDatabase.update(DatabaseHelper.DATABASE_TABLE_TASK, cv, DatabaseHelper._ID + " = " + dataBaseId, null);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}