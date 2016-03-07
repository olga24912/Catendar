package ru.mit.au.spb.olga.catendar.utils;

import android.os.AsyncTask;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
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
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import io.cloudboost.CloudException;
import io.cloudboost.CloudFile;
import io.cloudboost.CloudFileCallback;
import io.cloudboost.CloudObject;
import io.cloudboost.CloudObjectArrayCallback;
import io.cloudboost.CloudObjectCallback;
import io.cloudboost.CloudQuery;
import ru.mit.au.spb.olga.catendar.model.Event;
import ru.mit.au.spb.olga.catendar.model.Week;

public class CalendarToICSWriter {

    static final Logger logger = Logger.getLogger("EXPORT_WEEK");


    @NotNull
    public static String getDefaultFileName(Week currentWeek) {
        long time = currentWeek == null ? System.currentTimeMillis() : currentWeek.getStartDateInSeconds();
        /// почему не просто дата начала недели?
        return FILENAME_PREFIX + Long.toString(time) + FILENAME_SUFFIX;
    }

    @NotNull
    private static String getFileName(String path, Week currentWeek) {
        return path + '/' + getDefaultFileName(currentWeek);
    }

    public static void exportWeek(Week currentWeek, String filePath) {

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
            saveWeekFiletoCloud(fileName);
        } catch(CloudException e) {
            /// правильнее писать в логи
            System.err.println("Failed to save the file!");
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static final String EXPORT_TABLE_NAME = "ExportedWeeks";
    private static final String WEEK_DATE_COLUMN = "weekStartDate";
    private static final String FILE_URL_COLUMN = "fileUrl";
    private static final String FILE_COLUMN = "file";
    private static final String FILENAME_PREFIX = "calendar";
    private static final String FILENAME_SUFFIX = ".ics";

    private static void initCalendar(Calendar calendar) {
        calendar.getProperties().add(new ProdId("-//Olga and Liza//Catendar 0.1//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
    }

    private static void addEvent (Event event, Calendar calendar) {
        VEvent e = new VEvent(new DateTime(event.getStartDate().getTime()),
                new DateTime(event.getEndDate().getTime()),
                event.getText());
        UidGenerator uidGen;
        /// что значит XXX? :) имелось ввиду что-то типа TODO?
        //XXX: hostInfo
        /// всегда 1? :)
        uidGen = new UidGenerator(null, "1");
        e.getProperties().add(uidGen.generateUid());
        calendar.getComponents().add(e);
    }

    private static Long getWeekDateFromFileName(String filename) {
        //the filename should necessarily be created by getDefaultFileName()
        String[] parts = filename.split("/");
        String suffix = parts[parts.length - 1];
        return Long.parseLong(suffix.substring(FILENAME_PREFIX.length(), suffix.length() - FILENAME_SUFFIX.length()));
    }

    private static class CloudFileAsyncSave extends AsyncTask<CloudFile, Void, Void> {

        @Override
        protected Void doInBackground(CloudFile... params) {
            CloudFile cloudFile = params[0];
            try {
                cloudFile.save(cloudFileCallback);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return null;
        }
    }

    private static final CloudFileCallback cloudFileCallback = new CloudFileCallback() {

        @Override
        public void done(CloudFile file, CloudException e) {
            if (e != null) {
                throw new RuntimeException(e.getMessage(), e);
            } else if (file != null) {
                try {
                    saveFileToCloudObject(file);
                } catch (CloudException | ExecutionException | InterruptedException e1) {
                    throw new RuntimeException(e1.getMessage(), e1);
                }
            }
        }
    };

    private static void saveFileToCloudObject(CloudFile file) throws CloudException, ExecutionException, InterruptedException {
        CloudObject object = getCloudObject(file);
        object.set(FILE_COLUMN, file);
        object.save(new CloudObjectCallback() {
            @Override
            public void done(CloudObject cloudObject, CloudException e) {
                if (cloudObject != null) {
                    logger.info("File information was successfully saved to the cloud");
                }
                if (e != null) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        });
    }

    private static CloudObject getCloudObject(final CloudFile file) throws ExecutionException, InterruptedException {
        final CloudQuery query = new CloudQuery(EXPORT_TABLE_NAME);
        query.include(FILE_COLUMN); //this will include the file in CloudObjects
        query.equalTo(WEEK_DATE_COLUMN, getWeekDateFromFileName(file.getFileName()));

        return new AsyncTask<CloudQuery, Void, CloudObject>() {
            @Override
            protected CloudObject doInBackground(CloudQuery... params) {
                final CloudObject[] res = new CloudObject[1];

                try {
                    query.find(new CloudObjectArrayCallback(){
                        @Override
                        public void done(CloudObject[] cloudObjects, CloudException e) throws CloudException {
                            if(cloudObjects != null) {
                                if(cloudObjects.length > 0) {
                                    res[0] = cloudObjects[0];
                                } else {
                                    res[0] = new CloudObject(EXPORT_TABLE_NAME);
                                    initCloudObject(res[0], file);
                                }
                                return;
                            }
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    });
                } catch (CloudException e) {
                    e.printStackTrace();
                }
                return res[0];
            }
        }.execute(query).get();
    }

    private static void initCloudObject(CloudObject object, CloudFile file) throws CloudException {
        object.set(FILE_URL_COLUMN, file.getFileUrl());
        object.set(WEEK_DATE_COLUMN, getWeekDateFromFileName(file.getFileName()));
    }

    private static void saveWeekFiletoCloud(String fileName) throws CloudException{
        File file = new File(fileName);
        final CloudFile cloudFileObj;
        
        try {
            cloudFileObj = new CloudFile(file, "txt");
            new CloudFileAsyncSave().execute(cloudFileObj);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
