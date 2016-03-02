package ru.mit.au.spb.olga.catendar.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Template {
    private ArrayList<Event> events = new ArrayList<>();
    private String name;

    public Template(String newName) {
        name = newName;
    }

    public void addEvent(Event newEvent) {
        events.add(newEvent);
    }

    @NotNull
    public String getName() {
        return (name.equals(Week.SINGLE_EVENTS)) ? "" : name;
    }

    @NotNull
    public ArrayList<Event> getEvents() {
        return events;
    }
}