package ru.mit.au.spb.olga.catendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarActivity extends AppCompatActivity {
    public static final int HOURS_PER_DAY = 24;
    public static final int DAYS_PER_WEEK = 7;
    public static final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    public static TextView[][] table = new TextView[HOURS_PER_DAY][DAYS_PER_WEEK];
    private static Week sampleWeek = new Week();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCalendarView();

        //FIXME: just for the demo
        {
            //GregorianCalendar start = Week.formDate();//the day is the first day of a week
            GregorianCalendar start = new GregorianCalendar();
            start.set(Calendar.DAY_OF_WEEK, 0);
            GregorianCalendar end = start;
            end.add(Calendar.HOUR_OF_DAY, 1);
            ArrayList<Event> events = new ArrayList<>();

            for(int i = 1; i <= DAYS_PER_WEEK; i++) {
                start.set(Calendar.DAY_OF_WEEK, i);
                end.set(Calendar.DAY_OF_WEEK, i);

                Event event = new Event(0, start, end);
                event.setText("event #00" + Integer.toString(i));

                events.add(event);

                Log.d("Start date:", start.getTime().toString());
                Log.d("Event date:", event.getStartDate().getTime().toString());
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    protected void onCreate(Bundle savedInstanceState, ArrayList<Template> templates) {
        /***/
    }

    public void displayTemplate(Template t) {
        ArrayList<Event> eventsToDisplay = t.getEvents();
        String name = t.getName();
        for(Event e: eventsToDisplay) {
            Log.d("Event date(display): ", e.getStartDate().getTime().toString());
            GregorianCalendar d = e.getStartDate();
            int day = d.get(Calendar.DAY_OF_WEEK);
            GregorianCalendar h = e.getStartDate();
            int hour = h.get(Calendar.HOUR_OF_DAY);
            String hstr = h.toString();
            String dstr = d.toString();
            int j = (day + (DAYS_PER_WEEK - 1)) % DAYS_PER_WEEK;
            int i = (hour) % HOURS_PER_DAY;
            table[i][j].setText(name + "\n" + e.getText());
            Log.d("Event date(display): ", e.getStartDate().getTime().toString());
        }
    }

    private int createDayId(int i, int j) {
        return (i + j) * (i + j) + j;
    }

    private int getCellIdByDate(GregorianCalendar date) {
        int day = (date.get(Calendar.DAY_OF_WEEK) + (DAYS_PER_WEEK - 2)) % DAYS_PER_WEEK;
        int hour = date.get(Calendar.HOUR_OF_DAY);
        return createDayId(hour, day);
    }

    private void setCalendarView() {
        ScrollView verticalScroll = new ScrollView(this);
        ScrollView.LayoutParams verticalParams =
                new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,
                        ScrollView.LayoutParams.WRAP_CONTENT);
        verticalScroll.setLayoutParams(verticalParams);

        HorizontalScrollView horizontalScroll = new HorizontalScrollView(this);
        HorizontalScrollView.LayoutParams horizontalParams =
                new HorizontalScrollView.LayoutParams(HorizontalScrollView.LayoutParams.MATCH_PARENT,
                        HorizontalScrollView.LayoutParams.WRAP_CONTENT);
        horizontalScroll.setLayoutParams(horizontalParams);

        TableLayout calendar = new TableLayout(this);
        TableLayout.LayoutParams calendarParams =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
        calendar.setLayoutParams(calendarParams);

        TableRow topRow = new TableRow(this);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(/*TableRow.LayoutParams.MATCH_PARENT*/100,
                TableRow.LayoutParams.WRAP_CONTENT);
        topRow.setLayoutParams(rowParams);
        topRow.addView(new TextView(this), rowParams);
        for(int i = 0; i < DAYS_PER_WEEK; i++) {
            TextView curDay = new TextView(this);
            curDay.setText(days[i]);
            topRow.addView(curDay, rowParams);
        }
        calendar.addView(topRow, calendarParams);
        for(int i = 0; i < HOURS_PER_DAY; i++) {
            TableRow curHour = new TableRow(this);
            curHour.setLayoutParams(rowParams);
            TextView curTime = new TextView(this);
            curTime.setText(((Integer)i).toString() + ":00");
            curHour.addView(curTime, rowParams);

            for(int j = 0; j < DAYS_PER_WEEK; j++) {
                TextView curDay = new TextView(this);
                curDay.setOnClickListener(new View.OnClickListener() {
                    static final private int CREATE_EVENT = 0;

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CalendarActivity.this, CreateEventActivity.class);

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
        setContentView(horizontalScroll);
    }

}
