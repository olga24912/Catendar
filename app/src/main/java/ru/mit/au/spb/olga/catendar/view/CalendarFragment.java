package ru.mit.au.spb.olga.catendar.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import ru.mit.au.spb.olga.catendar.R;
import ru.mit.au.spb.olga.catendar.utils.CalendarToICSWriter;
import ru.mit.au.spb.olga.catendar.utils.DataBaseUtils;
import ru.mit.au.spb.olga.catendar.model.DatabaseHelper;
import ru.mit.au.spb.olga.catendar.model.Event;
import ru.mit.au.spb.olga.catendar.model.Template;
import ru.mit.au.spb.olga.catendar.model.Week;

import static java.lang.Math.min;

public class CalendarFragment extends Fragment {
    public static final int HOURS_PER_DAY = 24;
    public static final int DAYS_PER_WEEK = 7;
    public static final long HOUR_LENGTH = 60 * 60;
    public static final String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    public static final String[] months = {"January","February","March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};


    public static TextView[][] table = new TextView[HOURS_PER_DAY][DAYS_PER_WEEK];

    private static int[][] tableId = new int[HOURS_PER_DAY][DAYS_PER_WEEK];

    private static Week currentWeek = null;

    private SQLiteDatabase mSQLiteDatabase;

    private GregorianCalendar currentDate = new GregorianCalendar();

    public CalendarFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public CalendarFragment(int add) {
        currentDate = new GregorianCalendar();
        currentDate.add(Calendar.SECOND, add*7*24*60*60);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DatabaseHelper mDatabaseHelper = new DatabaseHelper(getContext(), "mydatabase14.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        Week tmpWeek = new Week(currentDate);
        currentWeek = DataBaseUtils.getWeekFromDataBaseByDate(tmpWeek.getTimeInMS(),
                mSQLiteDatabase);

        View result = setCalendarView();
        if (currentWeek != null) {
            displaySampleTemplate(currentWeek);
        }

        getActivity().setTitle(months[(currentWeek != null ? currentWeek.getStartDate().get(Calendar.MONTH) : 0)] + ", " +
                String.valueOf(currentWeek != null ? currentWeek.getStartDate().get(Calendar.YEAR) : 0));

        setHasOptionsMenu(true);
        return result;
    }


    private void displaySampleTemplate(Week w) {
        for(Template template : w.getTemplates()) {
            displayTemplate(template);
        }
    }


    public void displayTemplate(Template t) {
        ArrayList<Event> eventsToDisplay = t.getEvents();
        String name = t.getName();
        for(Event e: eventsToDisplay) {
            int day = e.getStartDate().get(java.util.Calendar.DAY_OF_WEEK);
            int hour = e.getStartDate().get(java.util.Calendar.HOUR_OF_DAY);
            int j = (day + (DAYS_PER_WEEK - 1)) % DAYS_PER_WEEK;
            int i = (hour) % HOURS_PER_DAY;
            long length = (e.getEnd() - e.getStart()) / HOUR_LENGTH;
            String nm2 = e.getText();
            if (name.length() > 5) {
                name = name.substring(0, 5);
            }
            if (nm2.length() > 5) {
                nm2 = nm2.substring(0, 5);
            }
            table[i][j].setText(name + "\n" + nm2);

            int color = getColor();
            for(int k = i; k < min(i + length, (long)HOURS_PER_DAY); k++) {
                table[k][j].setBackgroundColor(color);
                if(table[k][j].getText().equals("+\n ")) {
                    table[k][j].setText(" \n ");
                }
            }
            tableId[i][j] = e.getId();
        }
    }

    private int getColor() {
        Random randColor = new Random();
        return 0x50000000 + randColor.nextInt() % (0xffffff);
    }

    private View setCalendarView() {
        final Context context = getActivity();
        ScrollView verticalScroll = new ScrollView(context);
        ScrollView.LayoutParams verticalParams =
                new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,
                        ScrollView.LayoutParams.WRAP_CONTENT);
        verticalScroll.setLayoutParams(verticalParams);

        HorizontalScrollView horizontalScroll = new HorizontalScrollView(context);
        HorizontalScrollView.LayoutParams horizontalParams =
                new HorizontalScrollView.LayoutParams(HorizontalScrollView.LayoutParams.MATCH_PARENT,
                        HorizontalScrollView.LayoutParams.WRAP_CONTENT);
        horizontalScroll.setLayoutParams(horizontalParams);

        TableLayout calendar = new TableLayout(context);
        TableLayout.LayoutParams calendarParams =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
        calendar.setLayoutParams(calendarParams);

        TableRow topRow = new TableRow(context);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(/*TableRow.LayoutParams.MATCH_PARENT*/100,
                TableRow.LayoutParams.WRAP_CONTENT);
        topRow.setLayoutParams(rowParams);
        topRow.addView(new TextView(context), rowParams);
        GregorianCalendar dayDate = new GregorianCalendar();
        if (currentWeek != null) {
            dayDate = currentWeek.getStartDate();
        }
        for(int i = 0; i < DAYS_PER_WEEK; i++) {
            TextView curDay = new TextView(context);
            curDay.setText(days[i] + "\n" + Integer.toString(dayDate.get(Calendar.DAY_OF_MONTH)));
            topRow.addView(curDay, rowParams);
            dayDate.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.addView(topRow, calendarParams);
        for(int i = 0; i < HOURS_PER_DAY; i++) {
            TableRow curHour = new TableRow(context);
            curHour.setLayoutParams(rowParams);
            TextView curTime = new TextView(context);
            curTime.setText(((Integer)i).toString() + ":00");
            curHour.addView(curTime, rowParams);

            for(int j = 0; j < DAYS_PER_WEEK; j++) {
                TextView curDay = new TextView(context);
                final int globalI = i, globalJ = j;

                curDay.setOnClickListener(new View.OnClickListener() {
                    static final private int CREATE_EVENT = 0;
                    static final private int CHANGE_EVENT = 1;

                    @Override
                    public void onClick(View v) {
                        if (table[globalI][globalJ].getText().equals("+\n ")) {
                            Intent intent = new Intent(context, CreateEventActivity.class);

                            startActivityForResult(intent, CREATE_EVENT);
                        } else {
                            Intent intent = new Intent(context, ChangeEventActivity.class);
                            intent.putExtra("id", tableId[globalI][globalJ]);
                            startActivityForResult(intent, CHANGE_EVENT);
                        }
                    }
                });
                curDay.setText("+\n ");
                table[i][j] = curDay;
                curHour.addView(curDay, rowParams);
            }
            calendar.addView(curHour, calendarParams);
        }
        verticalScroll.addView(calendar);
        horizontalScroll.addView(verticalScroll);
        //setContentView(horizontalScroll);


        return horizontalScroll;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export:
                System.err.println("Entered export processing");

                FileSaveDialog fileSaveDialog = new FileSaveDialog(
                        getActivity(), new FileSaveDialog.FileSaveDialogListener() {
                    @Override
                    public void onChosenDir(String chosenDir) {
                        CalendarToICSWriter.exportWeekByDate(
                                currentWeek.getStartDate(), chosenDir, mSQLiteDatabase);
                    }
                });
                fileSaveDialog.chooseFile();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        currentWeek = DataBaseUtils.getWeekFromDataBaseByDate(currentWeek.getTimeInMS(),
                mSQLiteDatabase);
        displaySampleTemplate(currentWeek);

        Fragment currentFragment = this;
        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
    }

}
