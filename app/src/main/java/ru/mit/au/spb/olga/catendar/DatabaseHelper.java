package ru.mit.au.spb.olga.catendar;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by olga on 31.10.15.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {
    private static final String DATABASE_NAME = "mydatabase13.db";
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_TABLE_EVENT = "events";
    public static final String DATABASE_TABLE_TASK = "tasks";
    public static final String DATABASE_TABLE_WEEK = "week";
    public static final String DATABASE_TABLE_TEMPLATE = "template";
    public static final String DATABASE_TABLE_TEMPLATES_IN_WEEKS = "template_in_week";
    public static final String DATABASE_TABLE_CONTEXT = "context";
    public static final String DATABASE_TABLE_TASK_CONTEXT = "task_context";

    public static final String TEMPLATE_NAME = "name";
    public static final String TEMPLATE_FOR_WEEK = "for_week";

    public static final String WEEK_START_DATE = "start_date";

    public static final String TEMPLATES_IN_WEEKS_WEEK_ID = "week_id";
    public static final String TEMPLATES_IN_WEEKS_TEMPLATE_ID = "template_id";

    public static final String EVENT_NAME = "name";
    public static final String EVENT_PARENT_TEMPLATE = "template_id";
    public static final String EVENT_START_DATE = "start_date";
    public static final String EVENT_END_DATE = "end_date";

    public static final String TASK_NAME_COLUMN = "name";
    public static final String TASK_COMMENT = "comment";
    public static final String TASK_PRIORITY = "priority";
    public static final String TASK_DURATION = "duration";
    public static final String TASK_START_TIME = "start_time";
    public static final String TASK_DEADLINE = "deadline";
    public static final String TASK_IS_DONE = "is_done";

    public static final String CONTEXT_NAME = "context";

    public static final String TASK_CONTEXT_TASK_ID = "task_id";
    public static final String TASK_CONTEXT_CONTEXT_ID = "context_id";


    private static final String DATABASE_CREATE_CONTEXT_TABLE_SCRIPT = "create table " +
                    DATABASE_TABLE_CONTEXT + " ("
                    + BaseColumns._ID + " integer primary key autoincrement, "
                    + CONTEXT_NAME + " text not null);";

    private static final String DATABASE_CREATE_TEMPLATE_TABLE_SCRIPT = "create table " +
                    DATABASE_TABLE_TEMPLATE + " ("
                    + BaseColumns._ID + " integer primary key autoincrement, "
                    + TEMPLATE_NAME + " text not null, "
                    + TEMPLATE_FOR_WEEK + " integer);";

    private static final String DATABASE_CREATE_EVENT_TABLE_SCRIPT = "create table " +
            DATABASE_TABLE_EVENT + " ("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + EVENT_NAME + " text not null, "
            + EVENT_PARENT_TEMPLATE + " integer, "
            + EVENT_START_DATE + " integer, "
            + EVENT_END_DATE + " integer);";

    private static final String DATABASE_CREATE_TASK_TABLE_SCRIPT = "create table " +
            DATABASE_TABLE_TASK + " ("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + TASK_NAME_COLUMN + " text not null, "
            + TASK_PRIORITY + " integer, "
            + TASK_COMMENT + " text, "
            + TASK_DURATION + " integer, "
            + TASK_START_TIME + " integer, "
            + TASK_DEADLINE + " integer, "
            + TASK_IS_DONE + " integer);";

    private static final String DATABASE_CREATE_WEEK_TABLE_SCRIPT = "create table " +
            DATABASE_TABLE_WEEK + " ("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + WEEK_START_DATE + " integer);";

    private static final String DATABASE_CREATE_TEMPLATES_IN_WEEK_SCRIPT = "create table " +
            DATABASE_TABLE_TEMPLATES_IN_WEEKS + " ("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + TEMPLATES_IN_WEEKS_WEEK_ID + " integer, "
            + TEMPLATES_IN_WEEKS_TEMPLATE_ID + " integer);";

    private static final String DATABASE_CREATE_TASK_CONTEXT_SCRIPT = "create table " +
            DATABASE_TABLE_TASK_CONTEXT + " ("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + TASK_CONTEXT_CONTEXT_ID + " integer, "
            + TASK_CONTEXT_TASK_ID + " integer);";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_WEEK_TABLE_SCRIPT);
        db.execSQL(DATABASE_CREATE_TEMPLATES_IN_WEEK_SCRIPT);
        db.execSQL(DATABASE_CREATE_TEMPLATE_TABLE_SCRIPT);
        db.execSQL(DATABASE_CREATE_EVENT_TABLE_SCRIPT);
        db.execSQL(DATABASE_CREATE_TASK_TABLE_SCRIPT);
        db.execSQL(DATABASE_CREATE_CONTEXT_TABLE_SCRIPT);
        db.execSQL(DATABASE_CREATE_TASK_CONTEXT_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE_TEMPLATE + ";");
            db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE_EVENT + ";");
            db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE_TASK + ";");
            onCreate(db);
        }
    }
}
