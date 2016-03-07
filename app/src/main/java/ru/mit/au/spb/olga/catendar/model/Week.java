package ru.mit.au.spb.olga.catendar.model;

import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Week {
    public static final String SINGLE_EVENTS = "";

    @NotNull private GregorianCalendar startDate;
    @NotNull private ArrayList<EventsGroup> eventsGroups;
    private static final int WEEK_START = Calendar.SUNDAY;
    private final EventsGroup eventsGroupForSingleEvents;

    public Week() {
        this.startDate = new GregorianCalendar();

        this.eventsGroups = new ArrayList<>();
        eventsGroups.add(new EventsGroup(SINGLE_EVENTS));

        eventsGroupForSingleEvents = eventsGroups.get(0);
    }

    public Week (@NotNull GregorianCalendar startDate) {
        this.startDate = fromDate(startDate);

        this.eventsGroups = new ArrayList<>();
        eventsGroups.add(new EventsGroup(SINGLE_EVENTS));

        eventsGroupForSingleEvents = eventsGroups.get(0);
    }

    private static void toWeekStart(GregorianCalendar g) {
        g.add(Calendar.DAY_OF_WEEK, WEEK_START - g.get(Calendar.DAY_OF_WEEK));
        g.set(g.get(Calendar.YEAR), g.get(Calendar.MONTH), g.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    }

    @NotNull
    private static GregorianCalendar fromDate(@Nullable GregorianCalendar startDate) {
        GregorianCalendar res = (startDate != null) ? startDate : new GregorianCalendar();
        toWeekStart(res);
        return res;
    }

    public void addEventsGroup(@NotNull EventsGroup newEventsGroup) {
        eventsGroups.add(newEventsGroup);
    }

    public void addEvent(@NotNull Event newEvent) {
        eventsGroupForSingleEvents.addEvent(newEvent);
    }

    @NotNull
    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public long getStartDateInSeconds() {
        return this.startDate.getTimeInMillis()/1000;
    }

    @NotNull
    public ArrayList<EventsGroup> getEventsGroups() {
        return eventsGroups;
    }

    @NotNull
    public ArrayList<Event> getEvents() {
        ArrayList<Event> result = new ArrayList<>();
        for(EventsGroup eventsGroup : eventsGroups) {
            result.addAll(eventsGroup.getEvents());
        }
        return result;
    }
}