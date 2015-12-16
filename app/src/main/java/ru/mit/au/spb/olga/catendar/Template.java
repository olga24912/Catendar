package ru.mit.au.spb.olga.catendar;

import java.util.ArrayList;
import java.util.UUID;

public class Template {
    private ArrayList<Event> events;
    private String name;

    public Template(String newName) {
        name = newName;
        events = new ArrayList<>();
    }

    public Template(String newName, ArrayList<Event> newEvents) {
        name = newName;
        events = new ArrayList<>(newEvents);
    }

    public void addEvent(Event newEvent) {
        events.add(newEvent);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
}


/*
TODO:
http://stackoverflow.com/questions/10977422/how-to-create-simple-calendar-on-android
Find out how to display Week/Template (maybe using this^^^, should look like a grid)

+

improve Week and Template(to be able simply to keep events and use)
 */