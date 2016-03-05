package ru.mit.au.spb.olga.catendar.model;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EventsGroup {
    @NotNull
    private ArrayList<Event> events = new ArrayList<>();
    @NotNull
    private String name;

    public EventsGroup(@NonNull String newName) {
        name = newName;
    }

    public void addEvent(Event newEvent) {
        events.add(newEvent);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public ArrayList<Event> getEvents() {
        return events;
    }
}