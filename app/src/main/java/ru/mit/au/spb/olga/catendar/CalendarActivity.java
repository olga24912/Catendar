package ru.mit.au.spb.olga.catendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CalendarActivity extends AppCompatActivity {
    public static final int HOURS_PER_DAY = 24;
    public static final int DAYS_PER_WEEK = 7;
    public static final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_calendar);

        TableLayout calendar = new TableLayout(this);
        TableLayout.LayoutParams calendarParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
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
                curHour.addView(curDay, rowParams);
            }
            calendar.addView(curHour, calendarParams);
        }
        setContentView(calendar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
