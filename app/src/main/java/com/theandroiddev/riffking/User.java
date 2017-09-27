package com.theandroiddev.riffking;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by jakub on 25.07.17.
 */

@IgnoreExtraProperties
class User {

    private String name, details, email, id, photoUrl;
    private int likes, reps, age, followers;

    public User() {
    }

    public User(String name, String email, String photoUrl, int likes, int reps, int age, int followers) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.likes = likes;
        this.reps = reps;
        this.age = age;
        this.followers = followers;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}