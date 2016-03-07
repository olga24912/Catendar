package ru.mit.au.spb.olga.catendar.model;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Task extends CalendarPrimitive {
    private Boolean isDone;
    private int priority;
    private GregorianCalendar deadlineTime;

    public void setIsDone(@NotNull Boolean isDone) {
        this.isDone = isDone;
    }

    public void setDeadlineTime(@NotNull GregorianCalendar deadlineTime) {
        this.deadlineTime = deadlineTime;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @NotNull
    public Boolean IsDone() {
        return isDone;
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

    public long getDeadlineTimeInSeconds() {
        return (deadlineTime.getTimeInMillis()/1000);
    }

    public String getStringDuration() {
        String res = "";
        res += String.valueOf(duration.get(Calendar.HOUR));
        res += "hours ";
        res += String.valueOf(duration.get(Calendar.MINUTE));
        res += "minutes";
        return res;
    }

    public int getPriority() {
        return priority;
    }
}
