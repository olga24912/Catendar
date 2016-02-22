package ru.mit.au.spb.olga.catendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by olga on 21.11.15.
 */
public class ExpListAdapterAddTask extends BaseExpandableListAdapter {
    private ArrayList<Task> mGroups;
    private Context mContext;

    private SQLiteDatabase mSQLiteDatabase;


    public ExpListAdapterAddTask (Context context, ArrayList<Task> groups, SQLiteDatabase sQLiteDatabase) {
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

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

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
        textGroup.setText(mGroups.get(groupPosition).getTaskText());

        if (mGroups.get(groupPosition).getIsDone()) {
            textGroup.setBackgroundColor(0xffc5e384);
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_view_add_task, null);
        }

        final TextView textPriority = (TextView) convertView.findViewById(R.id.itemAddTaskTextViewPriority);
        final TextView textDuration = (TextView) convertView.findViewById(R.id.itemAddTaskTextViewDuration);
        final TextView textDeadlineTime = (TextView) convertView.findViewById(R.id.itemAddTaskTextViewTime);
        final TextView textComment = (TextView) convertView.findViewById(R.id.itemAddTaskTextViewComment);

        textComment.setText(mGroups.get(groupPosition).getCommentText());
        textDeadlineTime.setText("Deadline time:" + mGroups.get(groupPosition).getStringDeadlineTime());
        textPriority.setText("Priority: " + String.valueOf(mGroups.get(groupPosition).getPriority()));
        textDuration.setText("Duration: " + mGroups.get(groupPosition).getStringDuration());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
