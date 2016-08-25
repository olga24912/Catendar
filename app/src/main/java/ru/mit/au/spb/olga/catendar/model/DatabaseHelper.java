package ru.mit.au.spb.olga.catendar.model;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {
    private static final String DATABASE_NAME = "mydatabase15.db";
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_TABLE_EVENT = "events";
    public static final String DATABASE_TABLE_TASK = "tasks";
    public static final String DATABASE_TABLE_WEEK = "week";
    public static final String DATABASE_TABLE_TEMPLATE = "template";
    public static final String DATABASE_TABLE_TASK_HEAP = "task_heap";
    public static final String DATABASE_TABLE_HEAP = "heap";

    public static final String TEMPLATE_NAME = "name";
    public static final String TEMPLATE_FOR_WEEK = "for_week";
    public static final String TEMPLATE_WEEK_ID = "week_id";
    public static final String TEMPLATE_ORIGIN_ID = "origin_id";

    public static final String WEEK_START_DATE = "start_date";

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

    public static final String HEAP_NAME = "name";
    public static final String HEAP_DATE = "date";

    public static final String TASK_HEAP_TASK_ID = "task_id";
    public static final String TASK_HEAP_HEAP_ID = "heap_id";

    private static final String DATABASE_CREATE_HEAP_TABLE_SCRIPT = "create table " +
            DATABASE_TABLE_HEAP + " ("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + HEAP_DATE + " integer, "
            + HEAP_NAME + " text not null);";

    private static final String DATABASE_CREATE_TASK_HEAP_TABLE_SCRIPT = "create table " +
            DATABASE_TABLE_TASK_HEAP + " ("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + TASK_HEAP_HEAP_ID + " integer, "
            + TASK_HEAP_TASK_ID + " integer);";

    private static final String DATABASE_CREATE_TEMPLATE_TABLE_SCRIPT = "create table " +
                    DATABASE_TABLE_TEMPLATE + " ("
                    + BaseColumns._ID + " integer primary key autoincrement, "
                    + TEMPLATE_NAME + " text not null, "
                    + TEMPLATE_FOR_WEEK + " integer, "
                    + TEMPLATE_WEEK_ID + " integer, "
                    + TEMPLATE_ORIGIN_ID + " integer);";

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

    public DatabaseHelper(Context context) {
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
        db.execSQL(DATABASE_CREATE_TEMPLATE_TABLE_SCRIPT);
        db.execSQL(DATABASE_CREATE_EVENT_TABLE_SCRIPT);
        db.execSQL(DATABASE_CREATE_TASK_TABLE_SCRIPT);
        db.execSQL(DATABASE_CREATE_TASK_HEAP_TABLE_SCRIPT);
        db.execSQL(DATABASE_CREATE_HEAP_TABLE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
