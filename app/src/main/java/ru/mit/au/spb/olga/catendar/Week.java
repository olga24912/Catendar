package ru.mit.au.spb.olga.catendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

public class Week /*implements Parcelable*/ {
    private GregorianCalendar startDate;
    private ArrayList<Template> templates;//HashMap or index 0 for singleEvents
    private UUID id;
    private static final int WEEK_START = Calendar.MONDAY;
    private static final int SINGLE_EVENTS_INDEX = 0;

    public static void toWeekStart(GregorianCalendar g) {
        g.add(Calendar.DAY_OF_WEEK, WEEK_START - Calendar.DAY_OF_WEEK);
    }

    public static GregorianCalendar formDate() {
        return formDate(null);
    }

    public static GregorianCalendar formDate(GregorianCalendar startDate) {
        GregorianCalendar res = (startDate != null) ? startDate : new GregorianCalendar();
        toWeekStart(res);
        return res;
    }

    public Week() {
        this(null);
    }

    public Week (GregorianCalendar startDate) {
        this.startDate = formDate(startDate); //"Captain's Log, Stardate 1512.2. ..."

        id.randomUUID();

        templates = new ArrayList<>();
        templates.add(new Template("singleEvents", new ArrayList<Event>()));
    }

//    protected Week(Parcel in) {
//    }
//
//    public static final Creator<Week> CREATOR = new Creator<Week>() {
//        @Override
//        public Week createFromParcel(Parcel in) {
//            return new Week(in);
//        }
//
//        @Override
//        public Week[] newArray(int size) {
//            return new Week[size];
//        }
//    };

    public void addTemplate(Template newTemplate) {
        templates.add(newTemplate);
    }

    public void addEvent(Event newEvent) {
//    adds newEvent to singleEvents
        templates.get(SINGLE_EVENTS_INDEX).addEvent(newEvent);
    }

    public long getTimeInMS() {
        return this.startDate.getTimeInMillis();
    }

    public ArrayList<Template> getTemplates() {
        return templates;
    }
}
//Week and Template have similar method addEvent and similar field id. Nothing similar more.