package ru.mit.au.spb.olga.catendar.utils;

import android.database.sqlite.SQLiteDatabase;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import io.cloudboost.CloudException;
import io.cloudboost.CloudFile;
import io.cloudboost.CloudFileCallback;
import io.cloudboost.CloudObject;
import io.cloudboost.CloudObjectCallback;
import ru.mit.au.spb.olga.catendar.model.Event;
import ru.mit.au.spb.olga.catendar.model.Week;

public class CalendarToICSWriter {

    private static final String EXPORT_TABLE = "ExportedWeeks";
    private static final String WEEK_DATE_COLUMN = "weekStartDate";
    private static final String FILE_URL_COLUMN = "fileUrl";

    private static void initCalendar(Calendar calendar) {
        calendar.getProperties().add(new ProdId("-//Olga and Liza//Catendar 0.1//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
    }

    private static void addEvent (Event event, Calendar calendar) {
        VEvent e = new VEvent(new net.fortuna.ical4j.model.Date(event.getStartDate().getTime()),
                new net.fortuna.ical4j.model.Date(event.getEndDate().getTime()),
                event.getText());
        UidGenerator uidGen;
        //XXX: hostInfo
        uidGen = new UidGenerator(null, "1");
        e.getProperties().add(uidGen.generateUid());
        calendar.getComponents().add(e);
    }

    @NotNull
    public static String getDefaultFileName(Week currentWeek) {
        long time = currentWeek == null ? System.currentTimeMillis() : currentWeek.getTimeInSeconds();
        return "calendar" + Long.toString(time) + ".ics";
    }

    @NotNull
    public static String getFileName(String path, Week currentWeek) {
        return path + getDefaultFileName(currentWeek);
    }

    private static void saveWeekFiletoCloud(String fileName, final Week currentWeek) throws CloudException{
        File file = new File(fileName);
        final CloudFile cloudFileObj;
        final CloudObject cloudObject = new CloudObject(EXPORT_TABLE);
        final long weekStartTime = currentWeek.getTimeInSeconds();

//TODO: check if the file for this week was saved before
//        CloudQuery cloudQuery = new CloudQuery(EXPORT_TABLE);
//        cloudQuery.equalTo(WEEK_DATE_COLUMN, weekStartTime);
//        cloudQuery.findOne(new CloudObjectCallback() {
//            @Override
//            public void done(CloudObject cloudObject, CloudException t) throws CloudException {
//                if(cloudObject != null) {
//                    //TODO: fetch file
//                } else {
//                    //TODO: all the following below
//                }
//            }
//        });


        try {
            cloudFileObj = new CloudFile(file, "txt");
            cloudFileObj.save(new CloudFileCallback() {
                @Override
                public void done(CloudFile x, CloudException e) throws CloudException {
                    if (e != null) {
                        throw e;
                    } else if (x != null) {
                        cloudObject.set(FILE_URL_COLUMN, x.getFileUrl());
                        cloudObject.set(WEEK_DATE_COLUMN, weekStartTime);
                        cloudObject.save(new CloudObjectCallback() {
                            @Override
                            public void done(CloudObject x, CloudException t) {
                                if (x != null) {
                                    Logger logger = Logger.getLogger("SAVE_FILE");
                                    logger.info("File information was successfully saved to the cloud");
                                }
                                if (t != null) {
                                    throw new RuntimeException(t.getMessage(), t);
                                }
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void exportWeek(Week currentWeek, String filePath, SQLiteDatabase mSQLiteDatabase) {

        Calendar calendar = new Calendar();
        initCalendar(calendar);

        ArrayList<Event> events = currentWeek.getEvents();

        if(events.size() == 0) {
            //TODO: implement more intelligent handling
            System.out.println("The week connot be empty!");
            return;
        }

        for(Event event: events) {
            addEvent(event, calendar);
        }

        FileOutputStream fout;
        String fileName = getFileName(filePath, currentWeek);
        try {
            fout = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        CalendarOutputter outputter = new CalendarOutputter();
        try {
            outputter.output(calendar, fout);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        try {
            saveWeekFiletoCloud(fileName, currentWeek);
        } catch(CloudException e) {
            System.err.println("Failed to save the file!");
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
