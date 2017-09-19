package com.theandroiddev.riffking;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jakub on 19.07.17.
 */

class Thread {

    String id, title, author, comments, URL, date, content;
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

    public Thread(String title, String author, String URL, String content) {
        this.title = title;
        this.author = author;
        this.URL = URL;
        this.content = content;
        this.likes = 0;
        this.views = 0;
        this.date = getCurrentDate();
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

    public String getCurrentDate() {
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime.toString();
    }
}
