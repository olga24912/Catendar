package ru.mit.au.spb.olga.catendar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
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
    private static Week sampleWeek = new Week();

    private static Week currentWeek = null;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private GregorianCalendar currentDate = new GregorianCalendar();

    public CalendarFragment() {
        // Required empty public constructor
    }

    public CalendarFragment(int add) {
        currentDate = new GregorianCalendar();
        currentDate.add(Calendar.SECOND, add*7*24*60*60);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mDatabaseHelper = new DatabaseHelper(getContext(), "mydatabase13.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        Week tmpWeek = new Week(currentDate);
        getWeekDateBaseByDate(tmpWeek.getTimeInMS());

        View result = setCalendarView();
        if (currentWeek != null) {
            displaySampleTemplate(currentWeek);
        }

        getActivity().setTitle(months[currentWeek.getStartDate().get(Calendar.MONTH)] + ", " +
                String.valueOf(currentWeek.getStartDate().get(Calendar.YEAR)));

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

    private int createDayId(int i, int j) {
        return (i + j) * (i + j) + j;
    }

    private int getCellIdByDate(GregorianCalendar date) {
        int day = (date.get(java.util.Calendar.DAY_OF_WEEK) + (DAYS_PER_WEEK - 2)) % DAYS_PER_WEEK;
        int hour = date.get(java.util.Calendar.HOUR_OF_DAY);
        return createDayId(hour, day);
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

    private Template getTemplateDataBase(int id) {
        Template tp = null;

        Cursor cursorTemplate = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TEMPLATE, new String[]{DatabaseHelper._ID,
        DatabaseHelper.TEMPLATE_NAME},
                null, null,
                null, null, null);

        while (cursorTemplate.moveToNext())  {
            int currentId = cursorTemplate.getInt(cursorTemplate.getColumnIndex(DatabaseHelper._ID));
            if (currentId == id) {
                String name = cursorTemplate.getString(cursorTemplate.getColumnIndex(DatabaseHelper.TEMPLATE_NAME));
                tp = new Template(name);
            }
        }

        assert(tp != null);

        cursorTemplate.close();

        Cursor cursorEvent = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_EVENT, new String[]{DatabaseHelper._ID,
        DatabaseHelper.EVENT_PARENT_TEMPLATE, DatabaseHelper.EVENT_NAME, DatabaseHelper.EVENT_START_DATE,
        DatabaseHelper.EVENT_END_DATE},
                null, null,
                null, null, null);

        while (cursorEvent.moveToNext()) {
            int tpId = cursorEvent.getInt(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_PARENT_TEMPLATE));
            if (tpId == id) {
                Event newEvent = new Event();
                String name = cursorEvent.getString(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_NAME));
                newEvent.setText(name);

                int startTime = cursorEvent.getInt(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_START_DATE));
                newEvent.setStartDate(startTime);

                int endTime = cursorEvent.getInt(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_END_DATE));
                newEvent.setEndDate(endTime);

                int evId = cursorEvent.getInt(cursorEvent.getColumnIndex(DatabaseHelper._ID));
                newEvent.setId(evId);

                tp.addEvent(newEvent);
            }
        }
        cursorEvent.close();
        return tp;
    }

    private void getWeekDataBase(int id) {
        Cursor cursorWeek = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_WEEK, new String[]{DatabaseHelper._ID, DatabaseHelper.WEEK_START_DATE},
                null, null,
                null, null, null);

        currentWeek = null;
        while (cursorWeek.moveToNext()) {
            int currentId = cursorWeek.getInt(cursorWeek.getColumnIndex(DatabaseHelper._ID));
            if (currentId == id) {
                long timeInMS = cursorWeek.getLong(cursorWeek.getColumnIndex(DatabaseHelper.WEEK_START_DATE));
                GregorianCalendar currentTime = new GregorianCalendar();
                currentTime.setTimeInMillis(timeInMS*1000 + 1);
                currentWeek = new Week(currentTime);
            }
        }
        if (currentWeek == null) {
            currentWeek = new Week();
        }

        cursorWeek.close();

        ArrayList<Integer> templatesInWeek = new ArrayList<>();

        Cursor cursorTemplateAndWeek = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TEMPLATES_IN_WEEKS, new String[]{
                DatabaseHelper._ID, DatabaseHelper.TEMPLATES_IN_WEEKS_WEEK_ID, DatabaseHelper.TEMPLATES_IN_WEEKS_TEMPLATE_ID},
                null, null,
                null, null, null);

        while (cursorTemplateAndWeek.moveToNext()) {
            int weekId = cursorTemplateAndWeek.getInt(cursorTemplateAndWeek.getColumnIndex(DatabaseHelper.TEMPLATES_IN_WEEKS_WEEK_ID));
            int templateId = cursorTemplateAndWeek.getInt(cursorTemplateAndWeek.getColumnIndex(DatabaseHelper.TEMPLATES_IN_WEEKS_TEMPLATE_ID));

            if (weekId == id) {
                templatesInWeek.add(templateId);
            }
        }

        cursorTemplateAndWeek.close();

        for (int i = 0; i < templatesInWeek.size(); i++) {
            currentWeek.addTemplate(getTemplateDataBase(templatesInWeek.get(i)));
        }

        Cursor cursorEvent = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_EVENT, new String[]{DatabaseHelper._ID,
                        DatabaseHelper.EVENT_PARENT_TEMPLATE, DatabaseHelper.EVENT_NAME, DatabaseHelper.EVENT_START_DATE,
                        DatabaseHelper.EVENT_END_DATE},
                null, null,
                null, null, null);

        while (cursorEvent.moveToNext()) {
            long msTime = cursorEvent.getLong(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_START_DATE));
            GregorianCalendar currentEvent = new GregorianCalendar();
            currentEvent.setTimeInMillis(msTime*1000);
            Week weekForEvent = new Week(currentEvent);

            long weekTimeForEvent = weekForEvent.getTimeInMS();

            GregorianCalendar nc = currentWeek.getStartDate();

            if (weekTimeForEvent == currentWeek.getTimeInMS()) {
                Event newEvent = new Event();
                String name = cursorEvent.getString(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_NAME));
                newEvent.setText(name);

                int startTime = cursorEvent.getInt(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_START_DATE));
                newEvent.setStartDate(startTime);

                int endTime = cursorEvent.getInt(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_END_DATE));
                newEvent.setEndDate(endTime);
                int evId = cursorEvent.getInt(cursorEvent.getColumnIndex(DatabaseHelper._ID));
                newEvent.setId(evId);
                currentWeek.addEvent(newEvent);
            }
        }
        cursorEvent.close();

    }

    private Integer findIdWithThisTime(long s) {
        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_WEEK, new String[]{
                        DatabaseHelper._ID, DatabaseHelper.WEEK_START_DATE
                },
                null, null, null,
                null, null);

        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            long time = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.WEEK_START_DATE));

            if (time == s) {
                cursor.close();
                return id;
            }
        }

        cursor.close();

        return null;
    }

    private void getWeekDateBaseByDate (long sTime) {
        Integer id = findIdWithThisTime(sTime);
        if (id != null) {
            getWeekDataBase(id);
        } else {
            ContentValues newValues = new ContentValues();

            newValues.put(DatabaseHelper.WEEK_START_DATE, sTime);

            mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_WEEK, null, newValues);

            id = findIdWithThisTime(sTime);
            getWeekDataBase(id);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getWeekDateBaseByDate(currentWeek.getTimeInMS());
        displaySampleTemplate(currentWeek);

        Fragment currentFragment = this;
        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
    }
}
