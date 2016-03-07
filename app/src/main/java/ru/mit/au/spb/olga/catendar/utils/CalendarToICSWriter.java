package ru.mit.au.spb.olga.catendar.utils;

import android.os.AsyncTask;

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
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import io.cloudboost.CloudException;
import io.cloudboost.CloudFile;
import io.cloudboost.CloudFileArrayCallback;
import io.cloudboost.CloudFileCallback;
import io.cloudboost.CloudObject;
import io.cloudboost.CloudObjectArrayCallback;
import io.cloudboost.CloudObjectCallback;
import io.cloudboost.CloudQuery;
import ru.mit.au.spb.olga.catendar.model.Event;
import ru.mit.au.spb.olga.catendar.model.Week;

public class CalendarToICSWriter {

    @NotNull
    public static String getDefaultFileName(Week currentWeek) {
        long time = currentWeek == null ? System.currentTimeMillis() : currentWeek.getTimeInSeconds();
        return FILENAME_PREFIX + Long.toString(time) + FILENAME_SUFFIX;
    }

    @NotNull
    public static String getFileName(String path, Week currentWeek) {
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
        VEvent e = new VEvent(new net.fortuna.ical4j.model.DateTime(event.getStartDate().getTime()),
                new net.fortuna.ical4j.model.DateTime(event.getEndDate().getTime()),
                event.getText());
        UidGenerator uidGen;
        //XXX: null stands for hostInfo
        uidGen = new UidGenerator(null, "1");
        e.getProperties().add(uidGen.generateUid());
        calendar.getComponents().add(e);
    }

    private static Long getWeekDateFromFileName(String fileName) {
        //the fileName should necessarily be created by getDefaultFileName()
        String[] parts = fileName.split("/");
        String suffix = parts[parts.length - 1];
        return Long.parseLong(suffix.substring(FILENAME_PREFIX.length(), suffix.length() - FILENAME_SUFFIX.length()));
    }

    private static class CloudFileAsyncSave extends AsyncTask<CloudFile, Void, Void> {

        @Override
        protected Void doInBackground(CloudFile... params) {
            CloudFile cloudFile = params[0];
            try {
                cloudFile.save(cloudFileAsyncSaveCallback);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return null;
        }
    }

    private static final CloudFileCallback cloudFileAsyncSaveCallback = new CloudFileCallback() {
        @Override
        public void done(CloudFile x, CloudException e) throws CloudException {
            if (e != null) {
                throw e;
            } else if (x != null) {
                final String xName = x.getFileName();
                CloudObject cloudObject = new CloudObject(EXPORT_TABLE_NAME);
                cloudObject.set(FILE_URL_COLUMN, x.getFileUrl());
                cloudObject.set(WEEK_DATE_COLUMN, getWeekDateFromFileName(xName));

                cloudObject.save(new CloudObjectCallback() {
                    @Override
                    public void done(CloudObject x, CloudException t) {
                        if (x != null) {
                            Logger logger = Logger.getLogger("SAVE_FILE");
                            logger.info("File " + xName + " was successfully saved to the cloud");
                        }
                        if (t != null) {
                            throw new RuntimeException(t.getMessage(), t);
                        }
                    }
                });
            }
        }
    };

    private static void saveFileIntoCloudObject(final String fileName) throws CloudException {
        final File file = new File(fileName);

        final CloudObject cloudObject = new CloudObject(EXPORT_TABLE_NAME);
        CloudFile cloudFileObj = new CloudFile(file, "txt");
        cloudFileObj.setFileName(fileName);
        cloudObject.set(WEEK_DATE_COLUMN, getWeekDateFromFileName(fileName));

        saveCloudObjectWithFile(cloudObject, fileName);

        //now we should fetch this file, get its URL and set it in the DB
        //get file
        //fetch file
        //set url
        try {
            getFileByDate(getWeekDateFromFileName(fileName)).fetch(new CloudFileArrayCallback() {
                @Override
                public void done(CloudFile[] x, CloudException t) throws CloudException {
                    if(t != null) {
                        throw new RuntimeException(t.getMessage(), t);
                    }
                    if(x != null && x.length > 0) {
                        cloudObject.set(FILE_URL_COLUMN, x[0].getFileUrl());
                        saveCloudObjectWithFile(cloudObject, fileName);
                    }
                }
            });
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static 

    private static void saveCloudObjectWithFile (CloudObject cloudObject, final String fileName) throws CloudException {
        cloudObject.save(new CloudObjectCallback() {
            @Override
            public void done(CloudObject x, CloudException t) {
                if (x != null) {
                    //TODO: create a toast here
                    Logger logger = Logger.getLogger("SAVE_FILE");
                    logger.info("File " + fileName + " was successfully saved to the cloud");
                }
                if (t != null) {
                    throw new RuntimeException(t.getMessage(), t);
                }
            }
        });
    }

    private static CloudFile getFileByDate(long date) throws ExecutionException, InterruptedException {
        final CloudQuery query = new CloudQuery(EXPORT_TABLE_NAME);
        query.include("file"); //this will include the file in CloudObjects
        query.equalTo(WEEK_DATE_COLUMN, date);

        CloudFile result = new AsyncTask<CloudQuery, Void, CloudFile>() {
            @Override
            protected CloudFile doInBackground(CloudQuery... params) {
                final CloudFile[] res = new CloudFile[1];

                try {
                    query.find(new CloudObjectArrayCallback(){
                        @Override
                        public void done(CloudObject[] x, CloudException t) throws CloudException {
                            if(x != null && x.length > 0) {
                                res[0] = (CloudFile)x[0].get(FILE_COLUMN);
                            }
                        }
                    });
                } catch (CloudException e) {
                    e.printStackTrace();
                }
                return res[0];
            }
        }.execute(query).get();

        return result;
    }

    private static void saveWeekFiletoCloud(final String fileName) throws CloudException{
//TODO: check if the file for this week was saved before
        CloudQuery cloudQuery = new CloudQuery(EXPORT_TABLE_NAME);
        cloudQuery.equalTo(WEEK_DATE_COLUMN, getWeekDateFromFileName(fileName));

        new AsyncTask<CloudQuery, Void, Void>() {
            @Override
            protected Void doInBackground(CloudQuery... params) {
                try {
                    params[0].find(new CloudObjectArrayCallback() {
                        @Override
                        public void done(CloudObject[] cloudObjects, CloudException t) throws CloudException {
                            if (cloudObjects.length != 0) {
                                //TODO: update file
                                System.err.println("TODO: update");
                            } else {
                                try {
                                    saveFileIntoCloudObject(fileName);
                                } catch (Exception e) {
                                    throw new RuntimeException(e.getMessage(), e);
                                }
                            }
                        }
                    });
                } catch (CloudException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                return null;
            }
        }.execute(cloudQuery);

//        try {
//            cloudFileObj = new CloudFile(file, "txt");
//            new CloudFileAsyncSave().execute(cloudFileObj);
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage(), e);
//        }
    }
}
