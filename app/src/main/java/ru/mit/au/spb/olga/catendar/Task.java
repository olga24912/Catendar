package ru.mit.au.spb.olga.catendar;

/**
 * Created by olga on 18.10.15.
 */
public class Task {
    private Boolean isDone;
    private String taskText;

    public String getTaskText() {
        return taskText;
    }

    public void changeText(String text) {
        taskText = text;
    }
}
