package ru.mit.au.spb.olga.catendar.model;

import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Week {
    @NotNull
    private transient GregorianCalendar startDate;
    @NotNull
    private ArrayList<Template> templates;

    private static final int WEEK_START = Calendar.SUNDAY;
    private final Template TEMPLATE_FOR_SINGLE_EVENTS;
    public static final String SINGLE_EVENTS = "singleEvents";

    private static void toWeekStart(GregorianCalendar g) {
        g.add(Calendar.DAY_OF_WEEK, WEEK_START - g.get(Calendar.DAY_OF_WEEK));
        g.set(g.get(Calendar.YEAR), g.get(Calendar.MONTH), g.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    }

    @NotNull
    private static GregorianCalendar formDate(@Nullable GregorianCalendar startDate) {
        GregorianCalendar res = (startDate != null) ? startDate : new GregorianCalendar();
        toWeekStart(res);
        return res;
    }

    public Week() {
        this.startDate = new GregorianCalendar();

        this.templates = new ArrayList<>();
        templates.add(new Template(SINGLE_EVENTS));

        TEMPLATE_FOR_SINGLE_EVENTS = templates.get(0);
    }

    public Week (@NotNull GregorianCalendar startDate) {
        this.startDate = formDate(startDate);

        this.templates = new ArrayList<>();
        templates.add(new Template(SINGLE_EVENTS));

        TEMPLATE_FOR_SINGLE_EVENTS = templates.get(0);
    }

    public void addTemplate(@NotNull Template newTemplate) {
        templates.add(newTemplate);
    }

    public void addEvent(@NotNull Event newEvent) {
        TEMPLATE_FOR_SINGLE_EVENTS.addEvent(newEvent);
    }

    @NotNull
    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public long getTimeInMS() {
        return this.startDate.getTimeInMillis()/1000;
    }

    @NotNull
    public ArrayList<Template> getTemplates() {
        return templates;
    }

    @NotNull
    public ArrayList<Event> getEvents() {
        ArrayList<Event> result = new ArrayList<>();
        for(Template template: templates) {
            result.addAll(template.getEvents());
        }
        return result;
    }
}