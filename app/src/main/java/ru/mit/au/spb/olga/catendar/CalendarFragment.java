package ru.mit.au.spb.olga.catendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.GregorianCalendar;


public class CalendarFragment extends Fragment {
    public static final int HOURS_PER_DAY = 24;
    public static final int DAYS_PER_WEEK = 7;
    public static final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    public static TextView[][] table = new TextView[HOURS_PER_DAY][DAYS_PER_WEEK];
    private static Week sampleWeek = new Week();

    private static Week currentWeek = null;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setCalendarView();
//        return inflater.inflate(R.layout.fragment_calendar2, container, false);
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);

        return setCalendarView();

        //return inflater.inflate(R.layout.fragment_calendar2, container, false);

    }

    private void displaySampleTemplate() {
        //FIXME: just for the demo
        {
            //GregorianCalendar start = Week.formDate();//the day is the first day of a week
            ArrayList<Event> events = new ArrayList<>();

            for(int i = 1; i <= DAYS_PER_WEEK; i++) {
                GregorianCalendar start = new GregorianCalendar();
                start.set(java.util.Calendar.DAY_OF_WEEK, 0);
                GregorianCalendar end = start;
                end.add(java.util.Calendar.HOUR_OF_DAY, 1);

                start.set(java.util.Calendar.DAY_OF_WEEK, i);
                end.set(java.util.Calendar.DAY_OF_WEEK, i);

                Event event = new Event(0, start, end);
                event.setText("event #00" + Integer.toString(i));

                events.add(event);
            }
            Template myHometasks = new Template("myHometasks", events);
            sampleWeek.addTemplate(myHometasks);

            Event singleEvent = new Event();
            singleEvent.setText("Still alive");

            sampleWeek.addEvent(singleEvent);
        }

        for(Template template: sampleWeek.getTemplates()) {
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
            table[i][j].setText(name + "\n" + e.getText());
        }
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
        for(int i = 0; i < DAYS_PER_WEEK; i++) {
            TextView curDay = new TextView(context);
            curDay.setText(days[i]);
            topRow.addView(curDay, rowParams);
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
                curDay.setOnClickListener(new View.OnClickListener() {
                    static final private int CREATE_EVENT = 0;

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, CreateEventActivity.class);

                        startActivityForResult(intent, CREATE_EVENT);
                    }
                });
                curDay.setText("+");
                //curDay.setId(createDayId(i, j));
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

    private void getWeekDateBase(Integer id) {

    }
}
