package ru.mit.au.spb.olga.catendar.model;

import java.util.GregorianCalendar;


public abstract class CalendarPrimitive {
    protected String text;
    protected String comment;
    protected GregorianCalendar startDate;
    protected GregorianCalendar duration;
    protected Long id;

    public void setId(Long newId) {
        id = newId;
    }
    public Long getId() {
        return id;
    }
    public void setText(String newText) {
        text = newText;
    }
    public String getText() {
        return text;
    }
    public void setComment(String newComment) {
        comment = newComment;
    }
    public  String getComment() {
        return comment;
    }

    public void setDuration(int seconds) {
        duration = new GregorianCalendar();
        duration.setTimeInMillis(seconds * 1000L);
    }

    public void setDuration(GregorianCalendar duration) {
        this.duration = duration;
    }

    public long getDurationTimeInSeconds() {
        return (duration.getTimeInMillis() / 1000);
    }

    public void setStartDate(GregorianCalendar startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(int seconds) {
        startDate = new GregorianCalendar();
        startDate.setTimeInMillis(seconds * 1000L);
    }

    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public long getStartDateInSeconds() {
        return startDate.getTimeInMillis() / 1000;
    }

}
