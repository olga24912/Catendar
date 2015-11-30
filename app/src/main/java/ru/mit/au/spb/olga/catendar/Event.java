package ru.mit.au.spb.olga.catendar;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class Event {
//<<<<<<< HEAD
    private ArrayList<Task> eventTasks;
//=======
//    private ArrayList<Task> taskForThisEvent = new ArrayList<>();
//>>>>>>> d644f43949dfc44623f5eb48c0e1d48ac3a893e8
    private String eventText;
    private GregorianCalendar eventStartDate;
    private GregorianCalendar eventEndDate;

    public Event() {
        eventTasks = new ArrayList<Task>();
        eventStartDate = new GregorianCalendar();
        eventEndDate = new GregorianCalendar();
    }

    public void changeText(String newText) {
        eventText = newText;
    }

    public String getText() {
        return eventText;
    }
//<<<<<<< HEAD

    public GregorianCalendar getStartDate() {
        return eventStartDate;
    }
//=======
    public ArrayList<Task> getTaskList() {
        return eventTasks;
    }

    public void addTask(Task t) {
        eventTasks.add(t);
//>>>>>>> d644f43949dfc44623f5eb48c0e1d48ac3a893e8
    }
}
