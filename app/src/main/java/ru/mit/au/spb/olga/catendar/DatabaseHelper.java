package ru.mit.au.spb.olga.catendar;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by olga on 31.10.15.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_TABLE_CALENDAR = "calendar";
    private static final String DATABASE_TABLE_EVENT = "events";
    private static final String DATABASE_TABLE_TASK = "tasks";

    public static final String CALENDAR_NAME = "name";

    public static final String EVENT_NAME = "name";
    public static final String EVENT_PARENT_CALENDAR = "calendar_id";

    public static final String TASK_NAME_COLUMN = "name";
    public static final String TASK_PARENT_EVENT_ID = "event_id";

    private static final String DATABASE_CREATE_CALENDAR_TABLE_SCRIPT = "create table " +
                    DATABASE_TABLE_CALENDAR + " ("
                    + BaseColumns._ID + " integer primary key autoincrement, "
                    + CALENDAR_NAME + " text not null);";

    private static final String DATABASE_CREATE_EVENT_TABLE_SCRIPT = "create table " +
            DATABASE_TABLE_EVENT + " ("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + EVENT_NAME + " text not null, "
            + EVENT_PARENT_CALENDAR + " integer);";

    private static final String DATABASE_CREATE_TASK_TABLE_SCRIPT = "create table " +
            DATABASE_TABLE_TASK + " ("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + TASK_NAME_COLUMN + " text not null, "
            + TASK_PARENT_EVENT_ID + " integer);";

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
        db.execSQL(DATABASE_CREATE_CALENDAR_TABLE_SCRIPT);
        db.execSQL(DATABASE_CREATE_EVENT_TABLE_SCRIPT);
        db.execSQL(DATABASE_CREATE_TASK_TABLE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
