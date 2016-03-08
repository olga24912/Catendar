package ru.mit.au.spb.olga.catendar.view.template;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ru.mit.au.spb.olga.catendar.R;
import ru.mit.au.spb.olga.catendar.model.DatabaseHelper;

public class ChangeTemplateActivity extends AppCompatActivity {
    private ArrayList<Long> templatesId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SQLiteDatabase mSQLiteDatabase;
        ArrayList<String> templatesName = new ArrayList<>();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_template);

        DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        ListView listView = (ListView) findViewById(R.id.changeTemplateListView);

        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TEMPLATE, new String[]{
                        DatabaseHelper._ID, DatabaseHelper.TEMPLATE_NAME,
                        DatabaseHelper.TEMPLATE_FOR_WEEK
                },
                null, null,
                null, null, null);


        while (cursor.moveToNext()) {
            int idTmp = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TEMPLATE_NAME));
            int ignored = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TEMPLATE_FOR_WEEK));
            if (ignored == 0) {
                templatesId.add((long) idTmp);
                templatesName.add(name);
            }
        }

        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, templatesName);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                Intent intent = new Intent(ChangeTemplateActivity.this, CreateTemplateActivity.class);
                intent.putExtra("id", templatesId.get(position));
                startActivityForResult(intent, position);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    public void onCancelChangeTemplateClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
