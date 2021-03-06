package ru.mit.au.spb.olga.catendar.view.calendar;

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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import ru.mit.au.spb.olga.catendar.R;
import ru.mit.au.spb.olga.catendar.model.DatabaseHelper;
import ru.mit.au.spb.olga.catendar.model.Event;
import ru.mit.au.spb.olga.catendar.model.EventsGroup;
import ru.mit.au.spb.olga.catendar.model.Week;
import ru.mit.au.spb.olga.catendar.utils.CalendarToICSWriter;
import ru.mit.au.spb.olga.catendar.utils.DataBaseUtils;
import ru.mit.au.spb.olga.catendar.view.ShowExportedWeeksActivity;
import ru.mit.au.spb.olga.catendar.view.events.ChangeEventActivity;
import ru.mit.au.spb.olga.catendar.view.events.CreateEventActivity;

import static java.lang.Math.min;

public class CalendarFragment extends Fragment {
    public static final int HOURS_PER_DAY = 24;
    public static final int DAYS_PER_WEEK = 7;
    public static final int HOUR_LENGTH = 60 * 60;
    public static final int SECONDS_PER_WEEK = DAYS_PER_WEEK*HOURS_PER_DAY*HOUR_LENGTH;
    public static final String[] DAYS = (new java.text.DateFormatSymbols()).getShortWeekdays();
    public static final String[] MONTHS = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY",
            "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
    public TextView[][] table = new TextView[HOURS_PER_DAY][DAYS_PER_WEEK];
    private long[][] tableId = new long[HOURS_PER_DAY][DAYS_PER_WEEK];
    private Week currentWeek = null;

    private SQLiteDatabase mSQLiteDatabase;

    private GregorianCalendar currentDate = new GregorianCalendar();

    public CalendarFragment() {
    }

    @SuppressLint("ValidFragment")
    public CalendarFragment(int weekShift) {
        currentDate.add(Calendar.SECOND, weekShift*SECONDS_PER_WEEK);
    }

    @NotNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DatabaseHelper mDatabaseHelper = new DatabaseHelper(getContext());
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        Week tmpWeek = new Week(currentDate);
        currentWeek = DataBaseUtils.getWeekFromDataBaseByDate(tmpWeek.getStartDateInSeconds(),
                mSQLiteDatabase);

        View result = setCalendarView();
        if (currentWeek != null) {
            displaySampleTemplate(currentWeek);
        }

        getActivity().setTitle(MONTHS[(currentWeek != null ? currentWeek.getStartDate().get(Calendar.MONTH) : 0)] + ", " +
                String.valueOf(currentWeek != null ? currentWeek.getStartDate().get(Calendar.YEAR) : 0));

        setHasOptionsMenu(true);
        return result;
    }


    private void displaySampleTemplate(Week w) {
        for(EventsGroup eventsGroup : w.getEventsGroups()) {
            displayTemplate(eventsGroup);
        }
    }


    public void displayTemplate(EventsGroup t) {
        ArrayList<Event> eventsToDisplay = t.getEvents();
        String name = t.getName();
        int color = getColor();

        for(Event e: eventsToDisplay) {
            int day = e.getStartDate().get(java.util.Calendar.DAY_OF_WEEK);
            int hour = e.getStartDate().get(java.util.Calendar.HOUR_OF_DAY);
            int j = (day + (DAYS_PER_WEEK - 1)) % DAYS_PER_WEEK;
            int i = (hour) % HOURS_PER_DAY;
            long length = (e.getEndDateInSeconds() - e.getStartDateInSeconds()) / HOUR_LENGTH;
            String nm2 = e.getText();
            if (name.length() > 5) {
                name = name.substring(0, 5);
            }
            if (nm2.length() > 5) {
                nm2 = nm2.substring(0, 5);
            }
            table[i][j].setText(name + "\n" + nm2);

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

    @NotNull
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
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(100,
                TableRow.LayoutParams.WRAP_CONTENT);
        topRow.setLayoutParams(rowParams);
        topRow.addView(new TextView(context), rowParams);
        GregorianCalendar dayDate = new GregorianCalendar();
        if (currentWeek != null) {
            dayDate = currentWeek.getStartDate();
        }
        for(int i = 0; i < DAYS_PER_WEEK; i++) {
            TextView curDay = new TextView(context);
            curDay.setText(DAYS[i + 1] + "\n" + Integer.toString(dayDate.get(Calendar.DAY_OF_MONTH)));
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

                            GregorianCalendar date = new GregorianCalendar();
                            date.setTimeInMillis(currentWeek.getStartDateInSeconds()*1000);

                            date.add(Calendar.HOUR, globalI);
                            date.add(Calendar.DAY_OF_MONTH, globalJ - 7);
                            intent.putExtra("startTime", date.getTimeInMillis()/1000);
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
            case R.id.action_export:
                CalendarToICSWriter.exportWeek(
                        currentWeek, getActivity().getFilesDir().getPath());
                return true;
            case R.id.action_show_exported_weeks:
                Intent intent = new Intent(getActivity(), ShowExportedWeeksActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        currentWeek = DataBaseUtils.getWeekFromDataBaseByDate(currentWeek.getStartDateInSeconds(),
                mSQLiteDatabase);
        displaySampleTemplate(currentWeek);

        Fragment currentFragment = this;
        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
    }
}
