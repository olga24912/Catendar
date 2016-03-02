package ru.mit.au.spb.olga.catendar.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Task extends CalendarPrimitive {
    private Boolean isDone;
    private int priority;
    private GregorianCalendar deadlineTime;

    public void changeIsDone(Boolean isDone) {
        this.isDone = isDone;
    }

    public Boolean getIsDone() {
        return isDone;
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

    public long getDeadlineTimeInSecond() {
        return (deadlineTime.getTimeInMillis()/1000);
    }

    //meow
}
