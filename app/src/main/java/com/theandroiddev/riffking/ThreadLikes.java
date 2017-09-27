package com.theandroiddev.riffking;

/**
 * Created by jakub on 27.09.17.
 */

public class ThreadLikes {

    private String threadId, userId;

    public ThreadLikes() {
    }

    public ThreadLikes(String threadId, String userId) {
        this.threadId = threadId;
        this.userId = userId;
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
}
