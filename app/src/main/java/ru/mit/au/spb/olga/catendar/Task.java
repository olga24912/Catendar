package ru.mit.au.spb.olga.catendar;

/**
 * Created by olga on 18.10.15.
 */
public class Task {
    private Boolean isDone;
    private String taskText;
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getTaskText() {
        return taskText;
    }

    public void changeText(String text) {
        taskText = text;
    }

    public void changeIsDone(Boolean fl) {
        isDone = fl;
    }

    public Boolean getIsDone() {
        return isDone;
    }
}
