package ru.mit.au.spb.olga.catendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Week {
    private Date date;
    private ArrayList<Template> templates;//HashMap or index 0 for singleEvents
    private UUID id;

    public Week (Date startDate) {
        date = startDate;//current date if none provided ?
        id.randomUUID();
        templates.add(new Template("singleEvents", new ArrayList<Event>()));
    }

    public void addTemplate(Template newTemplate) {
        templates.add(newTemplate);
    }

    public void addEvent(Event newEvent) {
//adds newEvent to singleEvents
    }
}
//Week and Template have similar method addEvent and similar field id. Nothing similar more.