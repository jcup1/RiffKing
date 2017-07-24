package com.example.grazyna.riffking;

/**
 * Created by jakub on 19.07.17.
 */

class Thread {

    String title, author, comments, URL, date;
    int id, likes, views;

    public Thread(String title, String author, String comments, String URL, int id, int likes, int views, String date) {
        this.title = title;
        this.author = author;
        this.comments = comments;
        this.URL = URL;
        this.id = id;
        this.likes = likes;
        this.views = views;
        this.date = date;
    }

    public Thread(int id, String title, String author, String URL) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.URL = URL;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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


}
