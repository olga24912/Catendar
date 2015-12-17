package ru.mit.au.spb.olga.catendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Event {
    private transient ArrayList<Task> eventTasks;
    private String eventText;
    private GregorianCalendar eventStartDate;
    private GregorianCalendar eventEndDate;

    private String startDate;
    private int id;

    public static final String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    public Event() {
        eventTasks = new ArrayList<>();
    }

    public Event(int numTasks) {
        this(numTasks, null, null);
    }

    public void setId(int _id) {
        id = _id;
    }

    public int getId() {
        return id;
    }

    public Event(int numTasks, GregorianCalendar start, GregorianCalendar end) {
        eventTasks = new ArrayList<Task>(numTasks);
        eventStartDate = (start != null) ? start : new GregorianCalendar();
        startDate = eventStartDate.getTime().toString();
        eventEndDate = (end != null) ? end : new GregorianCalendar();
    }

    public String getDayOfWeekAndTime() {
        String res = "(" + days[eventStartDate.get(Calendar.DAY_OF_WEEK) - 1] + " "
                + eventStartDate.get(Calendar.HOUR_OF_DAY) + ":"
                + eventStartDate.get(Calendar.HOUR_OF_DAY) + " - "
                + eventEndDate.get(Calendar.HOUR_OF_DAY) + ":"
                + eventEndDate.get(Calendar.HOUR_OF_DAY) + ")";
        return res;
    }

    public void setText(String newText) {
        eventText = newText;
    }

    public String getText() {
        return eventText;
    }

    public GregorianCalendar getStartDate() {
        return eventStartDate;
    }

    public long getStart() {
        return eventStartDate.getTimeInMillis() / 1000;
    }

    public long getEnd() {
        return eventEndDate.getTimeInMillis() / 1000;
    }

    public ArrayList<Task> getTaskList() {
        return eventTasks;
    }

    public void setStartDate(int sTime) {
        eventStartDate = new GregorianCalendar();
        eventStartDate.setTimeInMillis((long)sTime*1000);
    }

    public void setEndDate(int sTime) {
        eventEndDate = new GregorianCalendar();
        eventEndDate.setTimeInMillis((long)sTime*1000);
    }

    public void setStartDate(int year, int month, int day, int hours, int minutes) {
        eventStartDate = new GregorianCalendar(year, month, day, hours, minutes);
    }

    public void setEndDate(int year, int month, int day, int hours, int minutes) {
        eventEndDate = new GregorianCalendar(year, month, day, hours, minutes);
    }

    public void addTask(Task t) {
        eventTasks.add(t);
    }
}
