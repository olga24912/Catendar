package ru.mit.au.spb.olga.catendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Week /*implements Parcelable*/ {
    private transient GregorianCalendar startDate;
    private ArrayList<Template> templates;//HashMap or index 0 for singleEvents

    private static final int WEEK_START = Calendar.SUNDAY;
    private static final int SINGLE_EVENTS_INDEX = 0;
    public static final String SINGLE_EVENTS = "singleEvents";

    public static void toWeekStart(GregorianCalendar g) {
        g.add(Calendar.DAY_OF_WEEK, WEEK_START - g.get(Calendar.DAY_OF_WEEK));
        g.set(g.get(Calendar.YEAR), g.get(Calendar.MONTH), g.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
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

        templates = new ArrayList<>();
        templates.add(new Template(SINGLE_EVENTS, new ArrayList<Event>()));
    }


    public void addTemplate(Template newTemplate) {
        templates.add(newTemplate);
    }

    public void addEvent(Event newEvent) {
        templates.get(SINGLE_EVENTS_INDEX).addEvent(newEvent);
    }

    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public long getTimeInMS() {
        return this.startDate.getTimeInMillis()/1000;
    }

    public ArrayList<Template> getTemplates() {
        return templates;
    }
}
