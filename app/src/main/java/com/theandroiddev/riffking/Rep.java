package com.theandroiddev.riffking;

/**
 * Created by jakub on 01.10.17.
 */

public class Rep {

    private int rep;
    private String userId, date, title;

    public Rep() {
    }

    public Rep(int rep, String userId, String date, String title) {
        this.rep = rep;
        this.userId = userId;
        this.date = date;
        this.title = title;
    }


    public int getRep() {
        return rep;
    }

    public void setRep(int rep) {
        this.rep = rep;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
