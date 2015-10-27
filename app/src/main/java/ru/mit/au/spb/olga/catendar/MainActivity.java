package ru.mit.au.spb.olga.catendar;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button calendarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

//        calendarButton = (Button) findViewById(R.id.calendarButton);
//        View.OnClickListener oclCalendarButton = new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
//                startActivity(intent);
//            }
//        };
//        calendarButton.setOnClickListener(oclCalendarButton);

        //LinearLayout playgroundLayout = (LinearLayout) findViewById(R.id.playgroundLayout);
/*        LinearLayout verticalLayout = new LinearLayout(this);
        verticalLayout.setOrientation(LinearLayout.VERTICAL);
        Button newButton = new Button(MainActivity.this); // 'this' works as well
        newButton.setText("I'm a brand new button");
        LinearLayout.LayoutParams purum = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                400
        );
        verticalLayout.addView(newButton, purum);
        setContentView(verticalLayout);
        //playgroundLayout.addView(verticalLayout);

        TextView v = new TextView(this);
        v.setText("Hello playground!");
        verticalLayout.addView(v);

        new TableRow(this);*/
        //setTitle("Котендарь");


        LinearLayout playground = new LinearLayout(this);
        playground.setOrientation(LinearLayout.VERTICAL);
        for(int j = 0; j < 10; j++) {
            LinearLayout playgroundItem = new LinearLayout(this);
            for (int i = 0; i < 10; i++) {
                final Integer a = i;
                final Integer b = j;
                Button simpleButton = new Button(this);
                simpleButton.setText(((Integer) (i * j)).toString());
                simpleButton.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (((Button) v).getText() == "=P") {
                            ((Button) v).setText(((Integer) (a * b)).toString());
                        } else {
                            ((Button) v).setText("=P");
                        }
                    }
                });
                playgroundItem.addView(simpleButton);
            }
            playground.addView(playgroundItem);
        }
        setContentView(playground);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    static final private int CREATE_EVENT = 0;
    public void onCreateEventClick(View view) {
        Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);

        startActivityForResult(intent, CREATE_EVENT);
    }


    public void onCalendarClick(View view) {

    }



    private ArrayList<String> eventList = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        ListView listOfEvent = (ListView) findViewById(R.id.listView);
//
//        if (requestCode == CREATE_EVENT) {
//            if (resultCode == RESULT_OK) {
//                eventList.add(data.getStringExtra(CreateEventActivity.EVENT_NAME));
//
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                        android.R.layout.simple_list_item_1, eventList);
//
//                listOfEvent.setAdapter(adapter);
//            }
//        }
    }
}
