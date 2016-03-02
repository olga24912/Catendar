package ru.mit.au.spb.olga.catendar.model;

import org.jetbrains.annotations.NotNull;

import java.util.GregorianCalendar;

public class Event {
    @NotNull
    private String eventText = "";
    @NotNull
    private GregorianCalendar eventStartDate = new GregorianCalendar();
    @NotNull
    private GregorianCalendar eventDuration = new GregorianCalendar();

    private int id;

    public void setId(int dataBaseId) {
        id = dataBaseId;
    }

    public int getId() {
        return id;
    }

    public void setText(String newText) {
        eventText = newText;
    }

    @NotNull
    public String getText() {
        return eventText;
    }

    @NotNull
    public GregorianCalendar getStartDate() {
        return eventStartDate;
    }

    @NotNull
    public GregorianCalendar getEndDate() {
        GregorianCalendar endDate = new GregorianCalendar();
        endDate.setTimeInMillis(eventStartDate.getTimeInMillis() + eventDuration.getTimeInMillis());
        return endDate;
    }

    public long getStart() {
        return eventStartDate.getTimeInMillis() / 1000;
    }

    public long getEnd() {
        return getEndDate().getTimeInMillis() / 1000;
    }

    public void setStartDate(int seconds) {
        eventStartDate = new GregorianCalendar();
        eventStartDate.setTimeInMillis(seconds * 1000L);
    }

    public void setEventDuration(int seconds) {
        eventDuration = new GregorianCalendar();
        eventDuration.setTimeInMillis(seconds * 1000L);
    }
}
