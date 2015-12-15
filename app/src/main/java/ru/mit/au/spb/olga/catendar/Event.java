package ru.mit.au.spb.olga.catendar;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class Event {
    private ArrayList<Task> eventTasks;
    private String eventText;
    private GregorianCalendar eventStartDate;
    private GregorianCalendar eventEndDate;
    private String startDate;

    public Event() {
        eventTasks = new ArrayList<>();
    }

    public Event(int numTasks) {
        this(numTasks, null, null);
    }

    public Event(int numTasks, GregorianCalendar start, GregorianCalendar end) {
        eventTasks = new ArrayList<Task>(numTasks);
        eventStartDate = (start != null) ? start : new GregorianCalendar();
        startDate = eventStartDate.getTime().toString();
        eventEndDate = (end != null) ? end : new GregorianCalendar();
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

//    public GregorianCalendar getEndDate() {
//        return eventEndDate;
//    }

    public ArrayList<Task> getTaskList() {
        return eventTasks;
    }

    public void setStartDate(int msTime) {
        eventStartDate = new GregorianCalendar();
        eventStartDate.setTimeInMillis(msTime);
    }

    public void setEndDate(int msTime) {
        eventEndDate = new GregorianCalendar();
        eventEndDate.setTimeInMillis(msTime);
    }

    public void setStartDate(int year, int month, int day, int hours, int minutes) {
        eventStartDate = new GregorianCalendar(year, month, day, hours, minutes);
    }

    public void setEventEndDate(int year, int month, int day, int hours, int minutes) {
        eventEndDate = new GregorianCalendar(year, month, day, hours, minutes);
    }

//    public void setStartDate(GregorianCalendar newStartDate) {
//        eventStartDate = newStartDate;
//        startDate = eventStartDate.getTime().toString();
//    }
//
//    public void setEndDate(GregorianCalendar newEndDate) {
//        eventEndDate = newEndDate;
//    }

    public void addTask(Task t) {
        eventTasks.add(t);
    }
}
