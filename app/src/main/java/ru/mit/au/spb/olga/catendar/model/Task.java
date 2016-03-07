package ru.mit.au.spb.olga.catendar.model;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

/// лучше везде использовать один набор аннотаций, для андроид приложений логично использовать из android.support.annotation
/// возможно с NotNull я запутал

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Task extends CalendarPrimitive {
    private Boolean isDone;
    private int priority;
    private GregorianCalendar deadlineTime;

    /// почему не setDone, setIsDone?
    public void changeIsDone(@NotNull Boolean isDone) {
        this.isDone = isDone;
    }

    public void setDeadlineTime(@NonNull GregorianCalendar deadlineTime) {
        this.deadlineTime = deadlineTime;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /// minor: можно просто isDone
    @NotNull
    public Boolean getIsDone() {
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

    /// в других местах "InSeconds"
    public long getDeadlineTimeInSecond() {
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
