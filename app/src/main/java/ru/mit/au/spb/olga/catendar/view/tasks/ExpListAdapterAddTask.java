package ru.mit.au.spb.olga.catendar.view.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ru.mit.au.spb.olga.catendar.R;
import ru.mit.au.spb.olga.catendar.model.Task;

public class ExpListAdapterAddTask extends BaseExpandableListAdapter {
    private ArrayList<Task> mGroups;
    private Context mContext;


    public ExpListAdapterAddTask (Context context, ArrayList<Task> groups) {
        mContext = context;
        mGroups = groups;
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
            convertView = inflater.inflate(R.layout.child_view_add_task, null);
        }

        final TextView textPriority = (TextView) convertView.findViewById(R.id.itemAddTaskTextViewPriority);
        final TextView textDuration = (TextView) convertView.findViewById(R.id.itemAddTaskTextViewDuration);
        final TextView textDeadlineTime = (TextView) convertView.findViewById(R.id.itemAddTaskTextViewTime);
        final TextView textComment = (TextView) convertView.findViewById(R.id.itemAddTaskTextViewComment);

        textComment.setText(mGroups.get(groupPosition).getComment());
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
