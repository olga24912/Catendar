package ru.mit.au.spb.olga.catendar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by olga on 31.10.15.
 */
public class CreateTaskActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private ArrayList<EditText> taskText = new ArrayList<>();
    private EditText parentEventText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_task);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase7.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        taskText.add((EditText) findViewById(R.id.editTaskText));
        parentEventText = (EditText)findViewById(R.id.editParentEventText);
    }

    public void onAddTaskClick(View view) {
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.LinearLayoutInCreateTask);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.gravity = Gravity.LEFT;
        layoutParams.setMargins(0, 10, 10, 10);
        EditText textView = new EditText(this);
        textView.setLayoutParams(layoutParams);
        taskText.add(textView);
        linearLayout.addView(textView);
    }

    public void onOkTaskClick(View view) {
        int parentID = 0;
        Cursor cursor = mSQLiteDatabase.query("events", new String[]{DatabaseHelper._ID, DatabaseHelper.EVENT_NAME,
                        DatabaseHelper.EVENT_PARENT_TEMPLATE},
                null, null,
                null, null, null) ;

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            String name = cursor.getString(cursor
                    .getColumnIndex(DatabaseHelper.EVENT_NAME));

            if (name.equals(String.valueOf(parentEventText.getText()))) {
                parentID = id;
            }

        }

        cursor.close();

        for (EditText currentTask : taskText) {
            ContentValues newValues = new ContentValues();

            newValues.put(DatabaseHelper.TASK_NAME_COLUMN, String.valueOf(currentTask.getText()));
            newValues.put(DatabaseHelper.TASK_PARENT_EVENT_ID, parentID);
            newValues.put(DatabaseHelper.TASK_IS_DONE, 0);

            mSQLiteDatabase.insert("tasks", null, newValues);
        }
        Intent answerIntent = new Intent();
        setResult(RESULT_OK, answerIntent);
        finish();
    }

    public void onCancelTaskClick(View view) {
        finish();
    }
}
