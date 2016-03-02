package ru.mit.au.spb.olga.catendar.model;

import java.util.GregorianCalendar;

public class Event extends CalendarPrimitive {
    public GregorianCalendar getEndDate() {
        GregorianCalendar endDate = new GregorianCalendar();
        endDate.setTimeInMillis(startDate.getTimeInMillis() + duration.getTimeInMillis());
        return endDate;
    }

    public long getEndDateInSeconds() {
        return getEndDate().getTimeInMillis() / 1000;
    }
}
