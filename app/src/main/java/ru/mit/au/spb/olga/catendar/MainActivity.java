package ru.mit.au.spb.olga.catendar;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity implements SimpleGestureFilter.SimpleGestureListener{
    private SimpleGestureFilter detector;

    private int currentAdditionToWeek = 0;
    private int currentPosition = 0;
    private DrawerLayout myDrawerLayout;
    private ListView myDrawerList;
    private ActionBarDrawerToggle myDrawerToggle;

    // navigation drawer title
    private CharSequence myDrawerTitle;
    // used to store app title
    private CharSequence myTitle;

    private String[] viewsNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*if(savedInstanceState == null) {
            CalendarFragment calendar = new CalendarFragment();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, calendar).commit();
        }*/
        detector = new SimpleGestureFilter(this,this);

        myTitle =  getTitle();
        myDrawerTitle = getResources().getString(R.string.menu);

        // load slide menu items
        viewsNames = getResources().getStringArray(R.array.views_array);
        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        myDrawerList = (ListView) findViewById(R.id.left_drawer);

        myDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, viewsNames));

        // enabling action bar app icon and behaving it as toggle button
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        myDrawerToggle = new ActionBarDrawerToggle(this, myDrawerLayout,
                R.string.open_menu,
                R.string.close_menu
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(myTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(myDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        myDrawerLayout.setDrawerListener(myDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }

        myDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
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
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        Intent intent;
        switch (position) {
            case 0:
                currentPosition = 0;
                currentAdditionToWeek = 0;
                fragment = new CalendarFragment();
                break;
            case 1:
                currentPosition = 1;
                fragment = new TaskListFragment();
                break;
            case 2:
                currentPosition = 2;

                intent = new Intent(MainActivity.this, CreateTaskActivity.class);

                startActivityForResult(intent, 2);
                break;
            case 3:
                currentPosition = 3;
                intent = new Intent(MainActivity.this, CreateEventActivity.class);

                startActivityForResult(intent, 3);
                break;
            case 4:
                currentPosition = 4;
                intent = new Intent(MainActivity.this, CreateWeekActivity.class);

                startActivityForResult(intent, 4);
                break;
            case 5:
                currentPosition = 5;
                intent = new Intent(MainActivity.this, CreateTemplateActivity.class);

                startActivityForResult(intent, 5);
                break;
            case 6:
                currentPosition = 6;
                intent = new Intent(MainActivity.this, DeleteTemplateActivity.class);

                startActivityForResult(intent, 6);
                break;
            case 7:
                currentPosition = 7;
                fragment = new CompareFragment();
                break;
            default:
                break;
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        // update selected item and title, then close the drawer
        myDrawerList.setItemChecked(position, true);
        myDrawerList.setSelection(position);
        setTitle(viewsNames[position]);
        myDrawerLayout.closeDrawer(myDrawerList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (myDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if navigation drawer is opened, hide the action items
        boolean drawerOpen = myDrawerLayout.isDrawerOpen(myDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        myTitle = title;
        getSupportActionBar().setTitle(myTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        myDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        myDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (2 <= requestCode && requestCode <= 6) {
            displayView(0);
        }
    }
}