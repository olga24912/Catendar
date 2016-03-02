package ru.mit.au.spb.olga.catendar.model;

import java.util.ArrayList;

public class Template {
    private ArrayList<Event> events;
    private String name;

    public Template(String newName) {
        name = newName;
        events = new ArrayList<>();
    }

    public void addEvent(Event newEvent) {
        events.add(newEvent);
    }

    public String getName() {
        return (name.equals(Week.SINGLE_EVENTS)) ? "" : name;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
}