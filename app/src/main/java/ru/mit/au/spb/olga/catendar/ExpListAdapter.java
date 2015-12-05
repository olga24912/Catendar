package ru.mit.au.spb.olga.catendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by olga on 21.11.15.
 */
public class ExpListAdapter extends BaseExpandableListAdapter {
    private ArrayList<Event> mGroups;
    private Context mContext;

    private SQLiteDatabase mSQLiteDatabase;


    public ExpListAdapter (Context context, ArrayList<Event> groups, SQLiteDatabase sQLiteDatabase) {
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
        return mGroups.get(groupPosition).getTaskList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition).getTaskList();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).getTaskList().get(childPosition);
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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_view, null);
        }

        if (isExpanded){
            //Изменяем что-нибудь, если текущая Group раскрыта
        }
        else{
            //Изменяем что-нибудь, если текущая Group скрыта
        }

        TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
        textGroup.setText(mGroups.get(groupPosition).getEventText());

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_view, null);
        }

        final TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
        textChild.setText(mGroups.get(groupPosition).getTaskList().get(childPosition).getTaskText());

        Button button = (Button)convertView.findViewById(R.id.buttonChild);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textChild.setBackgroundColor(0xffc5e384);
                String currentTask = mGroups.get(groupPosition).getTaskList().get(childPosition).getTaskText();
                Boolean isDone = mGroups.get(groupPosition).getTaskList().get(childPosition).getIsDone();

                Cursor cursor = mSQLiteDatabase.query("tasks", new String[]{DatabaseHelper._ID, DatabaseHelper.TASK_NAME_COLUMN,
                                DatabaseHelper.TASK_PARENT_EVENT_ID, DatabaseHelper.TASK_IS_DONE},
                        null, null,
                        null, null, null);

                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
                    String name = cursor.getString(cursor
                            .getColumnIndex(DatabaseHelper.TASK_NAME_COLUMN));
                    int parentId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_PARENT_EVENT_ID));
                    if (name.equals(currentTask)) {
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseHelper.TASK_NAME_COLUMN, currentTask);
                        cv.put(DatabaseHelper.TASK_PARENT_EVENT_ID, parentId);
                        cv.put(DatabaseHelper.TASK_IS_DONE, !isDone);

                        mSQLiteDatabase.update("tasks", cv, "_id " + "=" + id, null);
                    }
                }

                cursor.close();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
