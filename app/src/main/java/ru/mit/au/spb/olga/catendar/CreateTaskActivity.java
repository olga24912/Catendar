package ru.mit.au.spb.olga.catendar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by olga on 05.02.16.
 */
public class CreateTaskActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener {

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private EditText taskText;

    private int priority = 5;
    private TextView priorityView;

    private EditText commentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_task);

        taskText = (EditText)findViewById(R.id.createTaskEditTextTaskText);

        commentText = (EditText)findViewById(R.id.createTaskEditTextComments);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase12.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        final SeekBar seekbar = (SeekBar)findViewById(R.id.createTaskSeekBar);
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setMax(10);
        seekbar.setProgress(5);
        priority = 5;
        priorityView = (TextView)findViewById(R.id.createTaskTextViewPriority);
        priorityView.setText(String.valueOf(priorityView.getText()) + " 5");


    }


    public void onOkClick(View view) {
        Intent answerIntent = new Intent();

        String taskTextString = String.valueOf(taskText.getText());

        String commentString = String.valueOf(commentText.getText());

        setResult(RESULT_OK, answerIntent);
        finish();
    }


    public CreateTaskActivity() {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        priority = seekBar.getProgress();
        priorityView.setText("Priority: " + priority);
    }
}
