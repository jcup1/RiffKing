package com.theandroiddev.riffking;

/**
 * Created by jakub on 19.07.17.
 */

class Thread {

    private String id, userId, title, videoUrl, description, category, date;
    private int likes, views;

    public Thread() {

    }

    public Thread(String userId, String title, String videoUrl, String description, String category, String date, int likes, int views) {
        this.userId = userId;
        this.title = title;
        this.videoUrl = videoUrl;
        this.description = description;
        this.category = category;
        this.date = date;
        this.likes = likes;
        this.views = views;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    //    public String getYoutubeId() {
//
//        if (getURL().length() > YTIDLENGTH) {
//            String ytId = getURL().substring(getURL().length() - YTIDLENGTH, getURL().length());
//            if (ytId.length() >= YTIDLENGTH) {
//
//
//                ytId = ytId.substring(0, YTIDLENGTH);
//
//                return ytId;
//            }
//
//        }
//        return getURL();
//    }

}
