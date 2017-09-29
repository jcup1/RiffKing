package com.theandroiddev.riffking;

/**
 * Created by jakub on 25.09.17.
 */

public class Comment {

    private String id, threadId, userId, content, date;
    private int likes;

    public Comment() {

    }

    public Comment(String threadId, String userId, String content, String date, int likes) {
        this.id = id;
        this.threadId = threadId;
        this.userId = userId;
        this.content = content;
        this.date = date;
        this.likes = likes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return threadId + "\n" + userId;
    }
}
