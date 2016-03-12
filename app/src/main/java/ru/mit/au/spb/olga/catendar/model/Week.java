package ru.mit.au.spb.olga.catendar.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

public class Week {
    public static final String SINGLE_EVENTS = "";
    private static final int WEEK_START = Calendar.SUNDAY;

    @NotNull private final GregorianCalendar startDate;
    private final EventsGroup eventsGroupForSingleEvents = new EventsGroup(SINGLE_EVENTS);
    private final ArrayList<EventsGroup> eventsGroups = new ArrayList<>(Collections.singleton(eventsGroupForSingleEvents));

    /// кажется не доконца поняты рекомендации в http://www.oracle.com/technetwork/java/javase/documentation/codeconventions-141855.html#1852
    /// Думаю 1,2,3 в разъяснении не нуждаются.
    /// 4. статические поля отсортированные по "access modifier" -- сначала public, protected, package level(ничего не написано), private
    /// 5. обычные поля тоже отсортированные по "access modifier" -- сначала public, protected, package level(ничего не написано), private
    /// 6. конструкторы
    /// 7. методы предлогается групировать по функциональности
    /// но самое главное единообразие, особенно когда над кодом работает несколько человек
    public Week() {
        this.startDate = fromDate(new GregorianCalendar());
    }

    public Week (@NotNull GregorianCalendar startDate) {
        this.startDate = fromDate(startDate);
    }

    private static void toWeekStart(GregorianCalendar g) {
        g.add(Calendar.DAY_OF_WEEK, WEEK_START - g.get(Calendar.DAY_OF_WEEK));
        g.set(g.get(Calendar.YEAR), g.get(Calendar.MONTH), g.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    }

    @NotNull
    private static GregorianCalendar fromDate(@Nullable GregorianCalendar startDate) {
        GregorianCalendar res = (startDate != null) ? startDate : new GregorianCalendar();
        toWeekStart(res);
        return res;
    }

    public void addEventsGroup(@NotNull EventsGroup newEventsGroup) {
        eventsGroups.add(newEventsGroup);
    }

    public void addEvent(@NotNull Event newEvent) {
        eventsGroupForSingleEvents.addEvent(newEvent);
    }

    @NotNull
    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public long getStartDateInSeconds() {
        return this.startDate.getTimeInMillis()/1000;
    }

    @NotNull
    public ArrayList<EventsGroup> getEventsGroups() {
        return eventsGroups;
    }

    @NotNull
    public ArrayList<Event> getEvents() {
        ArrayList<Event> result = new ArrayList<>();
        for(EventsGroup eventsGroup : eventsGroups) {
            result.addAll(eventsGroup.getEvents());
        }
        return result;
    }
}