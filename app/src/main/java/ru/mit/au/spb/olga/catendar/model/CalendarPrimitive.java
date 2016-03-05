package ru.mit.au.spb.olga.catendar.model;

import org.jetbrains.annotations.NotNull;

import java.util.GregorianCalendar;


public abstract class CalendarPrimitive {
    @NotNull
    protected String text = "";
    @NotNull
    protected String comment = "";
    @NotNull
    protected GregorianCalendar startDate = new GregorianCalendar();
    @NotNull
    protected GregorianCalendar duration = new GregorianCalendar();
    @NotNull
    protected Long id = -1L;

    public void setId(@NotNull Long newId) {
        id = newId;
    }

    public void setText(@NotNull String newText) {
        text = newText;
    }

    public void setComment(@NotNull String newComment) {
        comment = newComment;
    }

    public void setDuration(int seconds) {
        duration = new GregorianCalendar();
        duration.setTimeInMillis(seconds * 1000L);
    }

    public void setDuration(@NotNull GregorianCalendar duration) {
        this.duration = duration;
    }

    public void setStartDate(@NotNull GregorianCalendar startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(int seconds) {
        startDate = new GregorianCalendar();
        startDate.setTimeInMillis(seconds * 1000L);
    }

    @NotNull
    public Long getId() {
        return id;
    }

    @NotNull
    public String getText() {
        return text;
    }

    @NotNull
    public  String getComment() {
        return comment;
    }

    public long getDurationTimeInSeconds() {
        return (duration.getTimeInMillis() / 1000);
    }

    @NotNull
    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public long getStartDateInSeconds() {
        return startDate.getTimeInMillis() / 1000;
    }
}
