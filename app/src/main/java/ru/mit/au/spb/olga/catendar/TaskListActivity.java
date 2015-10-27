package ru.mit.au.spb.olga.catendar;

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

/**
 * Created by olga on 26.10.15.
 */
public class TaskListActivity extends AppCompatActivity {

    private ArrayList<String> eventList = new ArrayList<>();
    private ExpandableListView listOfEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_list);

        listOfEvent = (ExpandableListView) findViewById(R.id.expandableListView);
        eventList = getIntent().getExtras().getStringArrayList("eventList");

        Map<String, String> map;

        ArrayList<Map<String, String> > groupDataList = new ArrayList<>();

        for (String cur: eventList) {
            map = new HashMap<>();
            map.put("groupName", cur);
            groupDataList.add(map);
        }

        String groupFrom[] = new String[] {"groupName"};
        int groupTo[] = new int[] {android.R.id.text1};

        ArrayList<ArrayList<Map<String, String>>> сhildDataList = new ArrayList<>();

        ArrayList<Map<String, String>> сhildDataItemList = new ArrayList<>();
        for (int i = 0; i < eventList.size(); ++i) {
            сhildDataItemList = new ArrayList<>();
            сhildDataList.add(сhildDataItemList);
        }
        String childFrom[] = new String[] { "eventName" };
        int childTo[] = new int[] { android.R.id.text1 };

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                this, groupDataList,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, сhildDataList, android.R.layout.simple_list_item_1,
                childFrom, childTo);

        listOfEvent.setAdapter(adapter);
    }

    public void onCancelTaskListClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
