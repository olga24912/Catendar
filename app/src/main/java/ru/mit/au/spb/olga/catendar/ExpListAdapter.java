package ru.mit.au.spb.olga.catendar;

import android.content.Context;
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

    public ExpListAdapter (Context context, ArrayList<Event> groups) {
        mContext = context;
        mGroups = groups;
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
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_view, null);
        }

        TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
        textChild.setText(mGroups.get(groupPosition).getTaskList().get(childPosition).getTaskText());

        Button button = (Button)convertView.findViewById(R.id.buttonChild);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
