package ru.mit.au.spb.olga.catendar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by olga on 18.12.15.
 */
public class DeleteTemplateActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private ArrayList<CheckBox> existsTemplate = new ArrayList<>();
    private ArrayList<Integer> templateId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_delete_template);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase11.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        createCheckBox();
    }

    private void createCheckBox() {
        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TEMPLATE, new String[]{DatabaseHelper._ID,
                        DatabaseHelper.TEMPLATE_NAME,
                        DatabaseHelper.TEMPLATE_FOR_WEEK},
                null, null,
                null, null, null) ;

        while(cursor.moveToNext()) {
            String name = cursor.getString(cursor
                    .getColumnIndex(DatabaseHelper.TEMPLATE_NAME));

            int id = cursor.getInt(cursor
                    .getColumnIndex(DatabaseHelper._ID));

            int ignored = cursor.getInt(cursor
                    .getColumnIndex(DatabaseHelper.TEMPLATE_FOR_WEEK));

            if (ignored == 1) {
                continue;
            }
            if (name.equals("unknownTemplate179")) {
                mSQLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE_TEMPLATE, DatabaseHelper._ID + "=" + id, null);
            } else {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinearLayoutInDeleteTemplate);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.gravity = Gravity.LEFT;
                layoutParams.setMargins(0, 10, 10, 10);

                CheckBox newCheckBox = new CheckBox(this);
                newCheckBox.setLayoutParams(layoutParams);
                newCheckBox.setText(name);
                linearLayout.addView(newCheckBox);

                existsTemplate.add(newCheckBox);
                templateId.add(id);
            }
        }
        cursor.close();
    }

    public void onOkClickInDelete(View view) {
        for (int i = 0; i < existsTemplate.size(); i++) {
            if (existsTemplate.get(i).isChecked()) {
                mSQLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE_TEMPLATE, "_id = " + templateId.get(i), null);
            }
        }

        setResult(RESULT_OK);
        finish();
    }

}
