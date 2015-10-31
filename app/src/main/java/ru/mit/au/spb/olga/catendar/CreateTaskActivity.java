package ru.mit.au.spb.olga.catendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by olga on 31.10.15.
 */
public class CreateTaskActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_task);
    }

    public void onOkTaskClick(View view) {
        finish();
    }

    public void onCancelTaskClick(View view) {
        finish();
    }
}
