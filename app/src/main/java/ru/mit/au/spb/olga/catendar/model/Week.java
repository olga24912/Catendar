package ru.mit.au.spb.olga.catendar.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Week {
    private transient GregorianCalendar startDate;
    private ArrayList<Template> templates;

    private static final int WEEK_START = Calendar.SUNDAY;
    private final Template TEMPLATE_FOR_SINGLE_EVENTS;
    public static final String SINGLE_EVENTS = "singleEvents";

    private static void toWeekStart(GregorianCalendar g) {
        g.add(Calendar.DAY_OF_WEEK, WEEK_START - g.get(Calendar.DAY_OF_WEEK));
        g.set(g.get(Calendar.YEAR), g.get(Calendar.MONTH), g.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    }

    private static GregorianCalendar formDate(GregorianCalendar startDate) {
        GregorianCalendar res = (startDate != null) ? startDate : new GregorianCalendar();
        toWeekStart(res);
        return res;
    }

    public Week() {
        this.templates = new ArrayList<>();
        templates.add(new Template(SINGLE_EVENTS));

        TEMPLATE_FOR_SINGLE_EVENTS = templates.get(0);
    }

    public Week (GregorianCalendar startDate) {
        this.startDate = formDate(startDate);

        this.templates = new ArrayList<>();
        templates.add(new Template(SINGLE_EVENTS));

        TEMPLATE_FOR_SINGLE_EVENTS = templates.get(0);
    }

    public void addTemplate(Template newTemplate) {
        templates.add(newTemplate);
    }

    public void addEvent(Event newEvent) {
        TEMPLATE_FOR_SINGLE_EVENTS.addEvent(newEvent);
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

    public ArrayList<Event> getEvents() {
        ArrayList<Event> result = new ArrayList<>();
        for(Template template: templates) {
            result.addAll(template.getEvents());
        }
        return result;
    }
}