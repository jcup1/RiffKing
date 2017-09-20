package com.theandroiddev.riffking;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jakub on 19.07.17.
 */

class Thread {
    private static final String TAG = "Thread";

    String id, title, author, email, comments, URL, date, content;
    int likes, views;

    public Thread(String title, String author, String comments, String URL, String id, int likes, int views, String date) {
        this.title = title;
        this.author = author;
        this.comments = comments;
        this.URL = URL;
        this.id = id;
        this.likes = likes;
        this.views = views;
        this.date = date;
    }

    public Thread(String title, String author, String email, String URL, String content, String date) {
        this.title = title;
        this.author = author;
        this.email = email;
        this.URL = URL;
        this.content = content;
        this.likes = 0;
        this.views = 0;
        this.date = date;
    }

    public Thread() {


    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFormattedDate() {

        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        //TODO FORMAT MORE http://www.ocpsoft.org/prettytime/

        Date date = null;
        try {
            date = outputFormat.parse(getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            return date.toString();
        }
        Log.e(TAG, "getFormattedDate: DATE ERROR!");
        return getDate();
    }

}