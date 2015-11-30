package ru.mit.au.spb.olga.catendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by olga on 18.10.15.
 */
public class CreateEventActivity extends AppCompatActivity {
    private EditText eventText;

    public final static String EVENT_NAME = "ru.mit.au.spb.olga.catendar.eventName";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_event);

        eventText = (EditText)findViewById(R.id.editEventText);
    }

    public void onOkClick(View view) {
        Intent answerIntent = new Intent();

        Event createEvent = new Event();
        createEvent.changeText(String.valueOf(eventText.getText()));

        answerIntent.putExtra(EVENT_NAME, createEvent.getText());

        setResult(RESULT_OK, answerIntent);
        finish();
    }

    public void onCancelClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
