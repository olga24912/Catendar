package ru.mit.au.spb.olga.catendar.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import ru.mit.au.spb.olga.catendar.model.DatabaseHelper;
import ru.mit.au.spb.olga.catendar.model.Event;
import ru.mit.au.spb.olga.catendar.model.EventsGroup;
import ru.mit.au.spb.olga.catendar.model.Week;

public class DataBaseUtils {

    private DataBaseUtils() {}

    @NotNull
    public static Week getWeekFromDataBase(Integer id, SQLiteDatabase mSQLiteDatabase) {
        Cursor cursorWeek = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_WEEK, new String[]{DatabaseHelper._ID, DatabaseHelper.WEEK_START_DATE},
                null, null,
                null, null, null);

        Week resultingWeek = null;

        while (cursorWeek.moveToNext()) {
            int currentId = cursorWeek.getInt(cursorWeek.getColumnIndex(DatabaseHelper._ID));
            if (currentId == id) {
                long timeInMS = cursorWeek.getLong(cursorWeek.getColumnIndex(DatabaseHelper.WEEK_START_DATE));
                GregorianCalendar currentTime = new GregorianCalendar();
                currentTime.setTimeInMillis(timeInMS*1000 + 1);
                resultingWeek = new Week(currentTime);
            }
        }

        if (resultingWeek == null) {
            resultingWeek = new Week();
        }

        cursorWeek.close();

        ArrayList<Integer> templatesInWeek = new ArrayList<>();

        Cursor cursorTemplate = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TEMPLATE, new String[]{
                        DatabaseHelper._ID, DatabaseHelper.TEMPLATE_FOR_WEEK,
                        DatabaseHelper.TEMPLATE_WEEK_ID, DatabaseHelper.TEMPLATE_ORIGIN_ID},
                null, null,
                null, null, null);

        while (cursorTemplate.moveToNext()) {
            int weekId = cursorTemplate.getInt(cursorTemplate.getColumnIndex(DatabaseHelper.TEMPLATE_WEEK_ID));
            int templateId = cursorTemplate.getInt(cursorTemplate.getColumnIndex(DatabaseHelper._ID));
            int forWeek = cursorTemplate.getInt(cursorTemplate.getColumnIndex(DatabaseHelper.TEMPLATE_FOR_WEEK));

            if (forWeek == 1 && weekId == id) {
                templatesInWeek.add(templateId);
            }
        }

        cursorTemplate.close();

        for (int i = 0; i < templatesInWeek.size(); i++) {
            resultingWeek.addEventsGroup(getTemplateFromDataBase(templatesInWeek.get(i), mSQLiteDatabase));
        }

        Cursor cursorEvent = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_EVENT, new String[]{DatabaseHelper._ID,
                        DatabaseHelper.EVENT_PARENT_TEMPLATE, DatabaseHelper.EVENT_NAME, DatabaseHelper.EVENT_START_DATE,
                        DatabaseHelper.EVENT_END_DATE},
                null, null,
                null, null, null);

        while (cursorEvent.moveToNext()) {
            long msTime = cursorEvent.getLong(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_START_DATE));
            GregorianCalendar currentEvent = new GregorianCalendar();
            currentEvent.setTimeInMillis(msTime*1000);
            Week weekForEvent = new Week(currentEvent);

            long weekTimeForEvent = weekForEvent.getStartDateInSeconds();

            if (weekTimeForEvent == resultingWeek.getStartDateInSeconds()) {
                Event newEvent = new Event();
                String name = cursorEvent.getString(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_NAME));
                newEvent.setText(name);

                int startTime = cursorEvent.getInt(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_START_DATE));
                newEvent.setStartDateInSeconds(startTime);

                int endTime = cursorEvent.getInt(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_END_DATE));
                newEvent.setDuration(endTime - startTime);
                int evId = cursorEvent.getInt(cursorEvent.getColumnIndex(DatabaseHelper._ID));
                newEvent.setId((long) evId);
                resultingWeek.addEvent(newEvent);
            }
        }
        cursorEvent.close();

        return resultingWeek;
    }

    @NotNull
    public static EventsGroup getTemplateFromDataBase(int id, SQLiteDatabase mSQLiteDatabase) {
        EventsGroup tp = null;

        Cursor cursorTemplate = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TEMPLATE,
                new String[]{DatabaseHelper._ID,
                        DatabaseHelper.TEMPLATE_NAME, DatabaseHelper.TEMPLATE_FOR_WEEK,
                DatabaseHelper.TEMPLATE_WEEK_ID, DatabaseHelper.TEMPLATE_ORIGIN_ID},
                null, null,
                null, null, null);

        while (cursorTemplate.moveToNext())  {
            int currentId = cursorTemplate.getInt(cursorTemplate.getColumnIndex(DatabaseHelper._ID));
            if (currentId == id) {
                String name = cursorTemplate.getString(cursorTemplate.getColumnIndex(DatabaseHelper.TEMPLATE_NAME));
                tp = new EventsGroup(name);
            }
        }

        assert(tp != null);

        cursorTemplate.close();

        Cursor cursorEvent = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_EVENT, new String[]{DatabaseHelper._ID,
                        DatabaseHelper.EVENT_PARENT_TEMPLATE, DatabaseHelper.EVENT_NAME, DatabaseHelper.EVENT_START_DATE,
                        DatabaseHelper.EVENT_END_DATE},
                null, null,
                null, null, null);

        while (cursorEvent.moveToNext()) {
            int tpId = cursorEvent.getInt(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_PARENT_TEMPLATE));
            if (tpId == id) {
                Event newEvent = new Event();
                String name = cursorEvent.getString(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_NAME));
                newEvent.setText(name);

                int startTime = cursorEvent.getInt(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_START_DATE));
                newEvent.setStartDateInSeconds(startTime);

                int endTime = cursorEvent.getInt(cursorEvent.getColumnIndex(DatabaseHelper.EVENT_END_DATE));
                newEvent.setDuration(endTime - startTime);

                int evId = cursorEvent.getInt(cursorEvent.getColumnIndex(DatabaseHelper._ID));
                newEvent.setId((long) evId);

                tp.addEvent(newEvent);
            }
        }
        cursorEvent.close();
        return tp;
    }

    @Nullable
    public static Integer findIdBySpecifiedTime(long sTime, SQLiteDatabase mSQLiteDatabase) {
        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_WEEK, new String[]{
                        DatabaseHelper._ID, DatabaseHelper.WEEK_START_DATE
                },
                null, null, null,
                null, null);

        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            long time = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.WEEK_START_DATE));

            if (time == sTime) {
                cursor.close();
                return id;
            }
        }

        cursor.close();

        return null;
    }

    @NotNull
    public static Week getWeekFromDataBaseByDate(long sTime, SQLiteDatabase mSQLiteDatabase) {
        Integer id = findIdBySpecifiedTime(sTime, mSQLiteDatabase);
        if (id != null) {
            return getWeekFromDataBase(id, mSQLiteDatabase);
        } else {
            ContentValues newValues = new ContentValues();

            newValues.put(DatabaseHelper.WEEK_START_DATE, sTime);

            mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_WEEK, null, newValues);

            return getWeekFromDataBase(findIdBySpecifiedTime(sTime, mSQLiteDatabase), mSQLiteDatabase);
        }
    }

    public static void copyEmptyTemplateToWeek(long weekId, SQLiteDatabase  mSQLiteDatabase) {
        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TEMPLATE, new String[]{
                        DatabaseHelper._ID, DatabaseHelper.TEMPLATE_FOR_WEEK,
                        DatabaseHelper.TEMPLATE_WEEK_ID,
                        DatabaseHelper.TEMPLATE_ORIGIN_ID
                },
                null, null, null,
                null, null);

        while(cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TEMPLATE_NAME));
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            int forWeek = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TEMPLATE_FOR_WEEK));

            if (forWeek == 0) {
                ContentValues twValues = new ContentValues();

                twValues.put(DatabaseHelper.TEMPLATE_NAME, name);
                twValues.put(DatabaseHelper.TEMPLATE_FOR_WEEK, 1);
                twValues.put(DatabaseHelper.TEMPLATE_WEEK_ID, weekId);
                twValues.put(DatabaseHelper.TEMPLATE_ORIGIN_ID, id);

                mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_TEMPLATE, null, twValues);
            }
        }

        cursor.close();
    }

    public static int findTIdWithThisOIdWId(int weekId, int originId, SQLiteDatabase mSQLiteDatabase) {
        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TEMPLATE, new String[]{
                        DatabaseHelper._ID, DatabaseHelper.TEMPLATE_FOR_WEEK,
                        DatabaseHelper.TEMPLATE_WEEK_ID,
                        DatabaseHelper.TEMPLATE_ORIGIN_ID
                },
                null, null, null,
                null, null);

        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            int forWeek = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TEMPLATE_FOR_WEEK));
            int wId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TEMPLATE_WEEK_ID));
            int oId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TEMPLATE_ORIGIN_ID));

            if (forWeek == 1 && wId == weekId && originId == oId) {
                return id;
            }
        }

        cursor.close();
        return 0;
    }

    public static void createWeek(long sTime, SQLiteDatabase mSQLiteDatabase) {
        ContentValues newValues = new ContentValues();

        newValues.put(DatabaseHelper.WEEK_START_DATE, sTime);

        long weekId = mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_WEEK, null, newValues);

        DataBaseUtils.copyEmptyTemplateToWeek(weekId, mSQLiteDatabase);
    }
}
