package ru.mit.au.spb.olga.catendar.model;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Task {
    private Boolean isDone = false;
    private String taskText = "";
    private String comment = "";
    private int priority = 5;
    private GregorianCalendar duration = new GregorianCalendar(1970, 1, 1, 2, 0);
    private GregorianCalendar startTime = new GregorianCalendar();
    private GregorianCalendar deadlineTime = new GregorianCalendar(2050, 1, 1, 2, 0);
    private Long id = 0L;

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public Long getId() {
        return this.id;
    }

    @NotNull
    public String getTaskText() {
        return taskText;
    }

    public void changeText(String text) {
        taskText = text;
    }

    public void changeIsDone(Boolean isDone) {
        this.isDone = isDone;
    }

    @NotNull
    public Boolean getIsDone() {
        return isDone;
    }

    public void setComment(String text) {
        comment = text;
    }

    @NotNull
    public  String getCommentText() {
        return comment;
    }

    public void setStartTime(GregorianCalendar startTime) {
        this.startTime = startTime;
    }

    public void setDuration(GregorianCalendar duration) {
        this.duration = duration;
    }

    @NotNull
    public String getStringDuration() {
        String res = "";
        res += String.valueOf(duration.get(Calendar.HOUR));
        res += "hours ";
        res += String.valueOf(duration.get(Calendar.MINUTE));
        res += "minutes";
        return res;
    }

    public void setDeadlineTime(GregorianCalendar deadlineTime) {
        this.deadlineTime = deadlineTime;
    }

    @NotNull
    public String getStringDeadlineTime() {
        String res = "";
        res += String.valueOf(deadlineTime.get(Calendar.DAY_OF_MONTH));
        res += ".";
        res += String.valueOf(deadlineTime.get(Calendar.MONTH));
        res += ".";
        res += String.valueOf(deadlineTime.get(Calendar.YEAR));
        return res;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public long getDeadlineTimeInSecond() {
        return (deadlineTime.getTimeInMillis()/1000);
    }

    public long getDurationTimeInSecond() {
        return (duration.getTimeInMillis()/1000);
    }

    public long getStartTimeInSecond() {
        return (startTime.getTimeInMillis()/1000);
    }
}
