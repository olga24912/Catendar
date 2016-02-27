package ru.mit.au.spb.olga.catendar;

import android.database.sqlite.SQLiteDatabase;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class CalendarToICSWriter {

    private static void initCalendar(Calendar calendar) {
        calendar.getProperties().add(new ProdId("-//Olga and Liza//Catendar 0.1//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
    }

    private static void addEvent (Event event, Calendar calendar) {
        VEvent e = new VEvent(new net.fortuna.ical4j.model.Date(event.getStartDate().getTime()),
                new net.fortuna.ical4j.model.Date(event.getEndDate().getTime()),
                event.getText());
        UidGenerator uidGen = null;
        try {
            uidGen = new UidGenerator("1");
        } catch (SocketException e1) {
            throw new RuntimeException(e1);
        }
        e.getProperties().add(uidGen.generateUid());
        calendar.getProperties().add(e);
    }

    public static void exportWeekByTime(long weekStart, SQLiteDatabase mSQLiteDatabase) {
        Calendar calendar = new Calendar();
        initCalendar(calendar);

        Week currentWeek = DataBaseUtils.getWeekFromDataBaseByDate(weekStart,
                mSQLiteDatabase);
        ArrayList<Event> events = currentWeek.getEvents();

        for(Event event: events) {
            addEvent(event, calendar);
        }

        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream("calendar" + Long.toString(currentWeek.getTimeInMS()) + ".ics");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found", e);
        }

        CalendarOutputter outputter = new CalendarOutputter();
        try {
            outputter.output(calendar, fout);
        } catch (IOException e) {
            throw new RuntimeException("Failed to output an .ics file", e);
        } catch (ValidationException e) {
            throw new RuntimeException("Validation Exception", e);
        }
    }

    public static void exportWeekByDate (GregorianCalendar weekStart, SQLiteDatabase mSQLiteDatabase) {
        exportWeekByTime(weekStart.getTimeInMillis(), mSQLiteDatabase);
    }

    public static void exportCurrentWeek (SQLiteDatabase mSQLiteDatabase) {
        exportWeekByTime(Week.getCurrentWeekStartTime(), mSQLiteDatabase);
    }
}
