package ru.mit.au.spb.olga.catendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by olga on 18.10.15.
 */
public class Task {
    private Boolean isDone;
    private String taskText;
    private String comment;
    private int priority;
    private GregorianCalendar duration;
    private GregorianCalendar startTime;
    private GregorianCalendar deadlineTime;
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getTaskText() {
        return taskText;
    }

    public void changeText(String text) {
        taskText = text;
    }

    public void changeIsDone(Boolean fl) {
        isDone = fl;
    }

    public Boolean getIsDone() {
        return isDone;
    }

    public void setComment(String text) {
        comment = text;
    }

    public  String getCommentText() {
        return comment;
    }

    public void setDuration(GregorianCalendar duration) {
        this.duration = duration;
    }

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
}
