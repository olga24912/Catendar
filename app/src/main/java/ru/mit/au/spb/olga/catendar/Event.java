package ru.mit.au.spb.olga.catendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Event {
    private String eventText;
    private GregorianCalendar eventStartDate;
    private GregorianCalendar eventEndDate;

    private int id;

    public static final String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    public Event() {
    }

    public void setId(int _id) {
        id = _id;
    }

    public int getId() {
        return id;
    }


    public String getDayOfWeekAndTime() {
        return "(" + days[eventStartDate.get(Calendar.DAY_OF_WEEK) - 1] + " "
                + eventStartDate.get(Calendar.HOUR_OF_DAY) + ":"
                + eventStartDate.get(Calendar.MINUTE) + " - "
                + eventEndDate.get(Calendar.HOUR_OF_DAY) + ":"
                + eventEndDate.get(Calendar.MINUTE) + ")";
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

    public long getStart() {
        return eventStartDate.getTimeInMillis() / 1000;
    }

    public long getEnd() {
        return eventEndDate.getTimeInMillis() / 1000;
    }

    public void setStartDate(int sTime) {
        eventStartDate = new GregorianCalendar();
        eventStartDate.setTimeInMillis((long)sTime*1000);
    }

    public void setEndDate(int sTime) {
        eventEndDate = new GregorianCalendar();
        eventEndDate.setTimeInMillis((long)sTime*1000);
    }
}
