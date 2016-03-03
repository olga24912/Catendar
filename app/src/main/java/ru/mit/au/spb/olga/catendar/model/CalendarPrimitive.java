package ru.mit.au.spb.olga.catendar.model;

import org.jetbrains.annotations.NotNull;

import java.util.GregorianCalendar;


public abstract class CalendarPrimitive {
    protected String text = "";
    protected String comment = "";
    protected GregorianCalendar startDate = new GregorianCalendar();
    protected GregorianCalendar duration = new GregorianCalendar();
    protected Long id = -1L;

    public void setId(@NotNull Long newId) {
        id = newId;
    }

    @NotNull
    public Long getId() {
        return id;
    }

    public void setText(@NotNull String newText) {
        text = newText;
    }

    @NotNull
    public String getText() {
        return text;
    }

    public void setComment(@NotNull String newComment) {
        comment = newComment;
    }

    @NotNull
    public  String getComment() {
        return comment;
    }

    public void setDuration(int seconds) {
        duration = new GregorianCalendar();
        duration.setTimeInMillis(seconds * 1000L);
    }

    public void setDuration(@NotNull GregorianCalendar duration) {
        this.duration = duration;
    }

    public long getDurationTimeInSeconds() {
        return (duration.getTimeInMillis() / 1000);
    }

    public void setStartDate(@NotNull GregorianCalendar startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(int seconds) {
        startDate = new GregorianCalendar();
        startDate.setTimeInMillis(seconds * 1000L);
    }

    @NotNull
    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public long getStartDateInSeconds() {
        return startDate.getTimeInMillis() / 1000;
    }
}
