package ru.mit.au.spb.olga.catendar;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by olga on 18.10.15.
 */
public class Event {
    private ArrayList<Task> taskForThisEvent;
    private String eventText;
    private GregorianCalendar eventStartDate;
    private GregorianCalendar eventEndDate;

    public Event() {

    }

    public void changeText(String newText) {
        eventText = newText;
    }

    public String getEventText() {
        return eventText;
    }
}
