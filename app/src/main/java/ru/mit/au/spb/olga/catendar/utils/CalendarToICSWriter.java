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
            logger.info("The week connot be empty!");
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
            saveWeekFileToCloud(fileName);
        } catch(CloudException e) {
            logger.warning("Failed to save the file!");
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static void saveWeekFileToCloud(String fileName) throws CloudException{
        File file = new File(fileName);
        final CloudFile cloudFileObj;

        try {
            cloudFileObj = new CloudFile(file, "txt");
            CloudFileAsyncSave asyncSave = new CloudFileAsyncSave();
            asyncSave.execute(cloudFileObj);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
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
                logger.warning("In file callback (returned null): " + e.getMessage());
                throw new RuntimeException(e.getMessage(), e);
            } else if (file != null) {
                try {
                    //Now we can create CloudObject with corresponding data and file url.
                    //It might already exist.
                    //In that case I want to find it and rewrite.
                    //Otherwise I want to create a new one and set all two columns as I need.

                    saveFileToCloudObject(file);

                } catch (CloudException | ExecutionException | InterruptedException e1) {
                    logger.warning("In file callback: " + e1.getMessage());
                    throw new RuntimeException(e1.getMessage(), e1);
                }
            }
        }
    };

    private static void saveFileToCloudObject(CloudFile file) throws CloudException, ExecutionException, InterruptedException {
        CloudObject object = getCloudObject(file);
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
        query.equalTo(WEEK_DATE_COLUMN, getWeekDateFromFileName(file.getFileName()));

        final CloudObject[] res = new CloudObject[1];
        final Object SYNC_OBJ = new Object();

        try {
            query.find(new CloudObjectArrayCallback(){
                @Override
                public void done(CloudObject[] cloudObjects, CloudException e) throws CloudException {
                    if(cloudObjects != null) {
                        if(cloudObjects.length > 0) {
                            synchronized (SYNC_OBJ) {
                                res[0] = cloudObjects[0];
                                res[0].set(FILE_URL_COLUMN, file.getFileUrl());
                                SYNC_OBJ.notify();
                            }
                        } else {
                            synchronized (SYNC_OBJ) {
                                res[0] = new CloudObject(EXPORT_TABLE_NAME);
                                res[0].set(FILE_URL_COLUMN, file.getFileUrl());
                                res[0].set(WEEK_DATE_COLUMN, getWeekDateFromFileName(file.getFileName()));
                                SYNC_OBJ.notify();
                            }
                        }
                        return;
                    }
                    logger.warning("Error while find query");
                    throw new RuntimeException(e.getMessage(), e);
                }
            });
        } catch (CloudException e) {
            e.printStackTrace();
        }

        synchronized (SYNC_OBJ) {
            while (res[0] == SYNC_OBJ) {
                if (res[0] == SYNC_OBJ) {
                    SYNC_OBJ.wait();
                }
            }
        }

        return res[0];
    }
/*
    private static void initCloudObject(CloudObject object, CloudFile file) throws CloudException {
        object.set(FILE_URL_COLUMN, file.getFileUrl());
        object.set(WEEK_DATE_COLUMN, getWeekDateFromFileName(file.getFileName()));
    }
*/


    private static final String EXPORT_TABLE_NAME = "ExportedWeeks";
    private static final String WEEK_DATE_COLUMN = "weekStartDate";
    private static final String FILE_URL_COLUMN = "fileUrl";
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

    public static ArrayList<String> getUrlsFromCloud () {
        final CloudObject object = new CloudObject(EXPORT_TABLE_NAME);
        Object res = object.get(FILE_URL_COLUMN);
        return (ArrayList<String>) res;
    }
}
