package ru.mit.au.spb.olga.catendar.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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
import ru.mit.au.spb.olga.catendar.view.template.ChangeTemplateActivity;
import ru.mit.au.spb.olga.catendar.view.template.CreateTemplateActivity;
import ru.mit.au.spb.olga.catendar.view.template.CreateWeekActivity;
import ru.mit.au.spb.olga.catendar.view.template.DeleteTemplateActivity;

public class MainActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener{
    enum Item {CALENDAR, TASK_LIST, CREATE_TASK, CREATE_EVENT, CREATE_WEEK,
        CREATE_TEMPLATE, DELETE_TEMPLATE, CREATE_PLAN, CHANGE_TEMPLATE}

    private SimpleGestureFilter detector;


    private int weekShift = 0;
    private Item currentOpenFragment = Item.CALENDAR;
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
    public void onSwipe(SimpleGestureFilter.Swipe_direction direction) {
        if (currentOpenFragment != Item.CALENDAR) {
            return;
        }
        Fragment fragment = null;
        switch (direction) {
            case RIGHT:
                weekShift--;
                fragment = new CalendarFragment(weekShift);
                break;
            case LEFT:
                weekShift++;
                fragment = new CalendarFragment(weekShift);
                break;
            case DOWN:
                break;
            case UP:
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
        if (position == Item.CALENDAR.ordinal()) {
            weekShift = 0;
            currentOpenFragment = Item.CALENDAR;
            fragment = new CalendarFragment();
        } else if (position == Item.TASK_LIST.ordinal()) {
            currentOpenFragment = Item.TASK_LIST;
            fragment = new TaskListFragment();
        } else if (position == Item.CREATE_TASK.ordinal()) {
            currentOpenFragment = Item.CREATE_TASK;
            intent = new Intent(MainActivity.this, CreateTaskActivity.class);
            startActivityForResult(intent, position);
        } else if (position == Item.CREATE_EVENT.ordinal()) {
            currentOpenFragment = Item.CREATE_EVENT;
            intent = new Intent(MainActivity.this, CreateEventActivity.class);
            startActivityForResult(intent, position);
        } else if (position == Item.CREATE_WEEK.ordinal()) {
            currentOpenFragment = Item.CREATE_WEEK;
            intent = new Intent(MainActivity.this, CreateWeekActivity.class);
            startActivityForResult(intent, position);
        } else if (position == Item.CREATE_TEMPLATE.ordinal()) {
            currentOpenFragment = Item.CREATE_TEMPLATE;
            intent = new Intent(MainActivity.this, CreateTemplateActivity.class);
            startActivityForResult(intent, position);
        } else if (position == Item.DELETE_TEMPLATE.ordinal()) {
            currentOpenFragment = Item.DELETE_TEMPLATE;
            intent = new Intent(MainActivity.this, DeleteTemplateActivity.class);
            startActivityForResult(intent, position);
        } else if (position == Item.CREATE_PLAN.ordinal()) {
            currentOpenFragment = Item.CREATE_PLAN;
            intent = new Intent(MainActivity.this, CreatePlanActivity.class);
            startActivityForResult(intent, position);
        } else if (position == Item.CHANGE_TEMPLATE.ordinal()) {
            currentOpenFragment = Item.CHANGE_TEMPLATE;
            intent = new Intent(MainActivity.this, ChangeTemplateActivity.class);
            startActivityForResult(intent, position);
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
        switch(currentOpenFragment) {
            case CALENDAR:
                getMenuInflater().inflate(R.menu.calendar_fragment_menu, menu);
                break;
            case TASK_LIST:
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
        if (currentOpenFragment == Item.CALENDAR) {
            switch(item.getItemId()) {
                case R.id.action_export:
                    return false;
                case R.id.action_show_exported_weeks:
                    return false;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } else if (currentOpenFragment == Item.TASK_LIST) {
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