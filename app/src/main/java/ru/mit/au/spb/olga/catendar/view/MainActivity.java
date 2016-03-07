package ru.mit.au.spb.olga.catendar.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.cloudboost.CloudApp;
import ru.mit.au.spb.olga.catendar.R;
import ru.mit.au.spb.olga.catendar.view.calendar.CalendarFragment;
import ru.mit.au.spb.olga.catendar.view.calendar.SimpleGestureFilter;
import ru.mit.au.spb.olga.catendar.view.events.CreateEventActivity;
import ru.mit.au.spb.olga.catendar.view.tasks.CreatePlanActivity;
import ru.mit.au.spb.olga.catendar.view.tasks.CreateTaskActivity;
import ru.mit.au.spb.olga.catendar.view.tasks.TaskListFragment;
import ru.mit.au.spb.olga.catendar.view.template.CreateTemplateActivity;
import ru.mit.au.spb.olga.catendar.view.template.CreateWeekActivity;
import ru.mit.au.spb.olga.catendar.view.template.DeleteTemplateActivity;

/// ActionBarActivity deprecated
public class MainActivity extends ActionBarActivity implements SimpleGestureFilter.SimpleGestureListener{
    private static final int ITEM_COUNT = 8;
    private static final int CALENDAR_ITEM = 0;
    private static final int TASK_LIST_ITEM = 1;
    private static final int CREATE_TASK_ITEM = 2;
    private static final int CREATE_EVENT_ITEM = 3;
    private static final int CREATE_WEEK_ITEM = 4;
    private static final int CREATE_TEMPLATE_ITEM = 5;
    private static final int DELETE_TEMPLATE_ITEM = 6;
    private static final int CREATE_PLAN_ITEM = 7;

    private SimpleGestureFilter detector;

    private int currentAdditionToWeek = 0;
    private int currentPosition = 0;
    private DrawerLayout myDrawerLayout;
    private ListView myDrawerList;
    private ActionBarDrawerToggle myDrawerToggle;

    private CharSequence myDrawerTitle;
    private CharSequence myTitle;

    private String[] viewsNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        detector = new SimpleGestureFilter(this,this);

        myTitle =  getTitle();
        myDrawerTitle = getResources().getString(R.string.menu);

        viewsNames = getResources().getStringArray(R.array.views_array);
        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        myDrawerList = (ListView) findViewById(R.id.left_drawer);

        myDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, viewsNames));

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        myDrawerToggle = new ActionBarDrawerToggle(this, myDrawerLayout,
                R.string.open_menu,
                R.string.close_menu
        ) {
            public void onDrawerClosed(View view) {
                android.support.v7.app.ActionBar currentActionBar = getSupportActionBar();
                if (currentActionBar != null) {
                    setTitle(myTitle);
                }

                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                android.support.v7.app.ActionBar currentActionBar = getSupportActionBar();
                if (currentActionBar != null) {
                    setTitle(myDrawerTitle);
                }
                invalidateOptionsMenu();
            }
        };
        myDrawerLayout.setDrawerListener(myDrawerToggle);

        if (savedInstanceState == null) {
            displayView(0);
        }

        myDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        CloudApp.init("grjugmvctxmv", "7de6cb54-deb1-449b-91a8-b27d62e02690");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        if (currentPosition != 0) {
            return;
        }
        Fragment fragment = null;
        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT :
                currentAdditionToWeek--;
                fragment = new CalendarFragment(currentAdditionToWeek);
                break;
            case SimpleGestureFilter.SWIPE_LEFT :
                currentAdditionToWeek++;
                fragment = new CalendarFragment(currentAdditionToWeek);
                break;
            case SimpleGestureFilter.SWIPE_DOWN :
                break;
            case SimpleGestureFilter.SWIPE_UP :
                break;

        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    }

    @Override
    public void onDoubleTap() {

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(
                AdapterView<?> parent, View view, int position, long id
        ) {
            displayView(position);
        }
    }

    private void displayView(int position) {
        Fragment fragment = null;
        Intent intent;
        /// мне страшно от этих захардкоженых цифр, константы сделили бы этот код на порядок читабельным и сопровождаемым
        if (position >= 0 && position < ITEM_COUNT) {
            currentPosition = position;
        }
        currentPosition = position;
        switch (position) {
            case CALENDAR_ITEM:
                currentAdditionToWeek = 0;
                fragment = new CalendarFragment();
                break;
            case TASK_LIST_ITEM:
                fragment = new TaskListFragment();
                break;
            case CREATE_TASK_ITEM:
                intent = new Intent(MainActivity.this, CreateTaskActivity.class);

                startActivityForResult(intent, position);
                break;
            case CREATE_EVENT_ITEM:
                intent = new Intent(MainActivity.this, CreateEventActivity.class);

                startActivityForResult(intent, position);
                break;
            case CREATE_WEEK_ITEM:
                intent = new Intent(MainActivity.this, CreateWeekActivity.class);

                startActivityForResult(intent, position);
                break;
            case CREATE_TEMPLATE_ITEM:
                intent = new Intent(MainActivity.this, CreateTemplateActivity.class);

                startActivityForResult(intent, position);
                break;
            case DELETE_TEMPLATE_ITEM:
                intent = new Intent(MainActivity.this, DeleteTemplateActivity.class);

                startActivityForResult(intent, position);
                break;
            case CREATE_PLAN_ITEM:
                intent = new Intent(MainActivity.this, CreatePlanActivity.class);

                startActivityForResult(intent, position);
                break;
            default:
                break;
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        myDrawerList.setItemChecked(position, true);
        myDrawerList.setSelection(position);
        setTitle(viewsNames[position]);
        myDrawerLayout.closeDrawer(myDrawerList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch(currentPosition) {
            case 0:
                getMenuInflater().inflate(R.menu.calendar_fragment_menu, menu);
                break;
            case 1:
                getMenuInflater().inflate(R.menu.menu_main, menu);
                break;
            default:
                getMenuInflater().inflate(R.menu.main, menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (myDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (currentPosition == 0) {
            switch(item.getItemId()) {
                case R.id.export:
                    return false;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } else if (currentPosition == 1) {
            switch(item.getItemId()) {
                case R.id.action_deadline:
                case R.id.action_duration:
                case R.id.action_not_show_all:
                case R.id.action_show_all:
                case R.id.action_priority:
                    return false;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } else {
            switch (item.getItemId()) {
                case R.id.action_settings:
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        myTitle = title;
        android.support.v7.app.ActionBar titleBar = getSupportActionBar();
        if (titleBar != null) {
            titleBar.setTitle(myTitle);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        myDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        myDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (2 <= requestCode && requestCode <= 7) {
            displayView(0);
        }
    }
}