package ru.mit.au.spb.olga.catendar;

import java.util.ArrayList;
import java.util.UUID;

public class Template {
    private ArrayList<Event> events;
    private UUID id;
    private String name;

    public Template(String newName, ArrayList<Event> newEvents) {
        name = newName;
        id.randomUUID();
        events.addAll(newEvents);
    }

    public void addEvent(Event newEvent) {
        events.add(newEvent);
    }
}


/*
TODO:
http://stackoverflow.com/questions/10977422/how-to-create-simple-calendar-on-android
Find out how to display Week/Template (maybe using this^^^, should look like a grid)

+

improve Week and Template(to be able simply to keep events and use)
 */