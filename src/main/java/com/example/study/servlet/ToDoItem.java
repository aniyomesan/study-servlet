package com.example.study.servlet;

public class ToDoItem {

    private int id;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String title;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private boolean done;

    public boolean isDone() {
        return this.done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public ToDoItem() {
        // no-op.
    }

    public ToDoItem(int id, String title, boolean done) {
        this.id = id;
        this.title = title;
        this.done = done;
    }
}