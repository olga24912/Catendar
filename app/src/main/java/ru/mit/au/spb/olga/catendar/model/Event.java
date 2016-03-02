package ru.mit.au.spb.olga.catendar.model;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Event extends CalendarPrimitive {

    private static final String[] days = new DateFormatSymbols().getShortWeekdays();

    public String getDayOfWeekAndTime() {
        return "(" + days[startDate.get(Calendar.DAY_OF_WEEK) - 1] + " "
                + startDate.get(Calendar.HOUR_OF_DAY) + ":"
                + startDate.get(Calendar.MINUTE) + " - "
                + getEndDate().get(Calendar.HOUR_OF_DAY) + ":"
                + getEndDate().get(Calendar.MINUTE) + ")";
    }

    public GregorianCalendar getEndDate() {
        GregorianCalendar endDate = new GregorianCalendar();
        endDate.setTimeInMillis(startDate.getTimeInMillis() + duration.getTimeInMillis());
        return endDate;
    }

    public long getEndDateInSeconds() {
        return getEndDate().getTimeInMillis() / 1000;
    }
}
