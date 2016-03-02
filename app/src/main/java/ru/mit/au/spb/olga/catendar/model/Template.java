package ru.mit.au.spb.olga.catendar.model;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Template {
    @NotNull
    private ArrayList<Event> events = new ArrayList<>();
    @NotNull
    private String name;

    public Template(@NonNull String newName) {
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