package ru.mit.au.spb.olga.catendar.model;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Event {
    private String eventText;
    private GregorianCalendar eventStartDate;
    private GregorianCalendar eventDuration;

    private int id;

    private static final String[] days = new DateFormatSymbols().getShortWeekdays();

    public void setId(int dataBaseId) {
        id = dataBaseId;
    }

    public int getId() {
        return id;
    }

    public String getDayOfWeekAndTime() {
        return "(" + days[eventStartDate.get(Calendar.DAY_OF_WEEK) - 1] + " "
                + eventStartDate.get(Calendar.HOUR_OF_DAY) + ":"
                + eventStartDate.get(Calendar.MINUTE) + " - "
                + getEndDate().get(Calendar.HOUR_OF_DAY) + ":"
                + getEndDate().get(Calendar.MINUTE) + ")";
    }

    public void setText(String newText) {
        eventText = newText;
    }

    public String getText() {
        return eventText;
    }

    public GregorianCalendar getStartDate() {
        return eventStartDate;
    }

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
