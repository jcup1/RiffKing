package com.theandroiddev.riffking;

/**
 * Created by jakub on 25.07.17.
 */

class User {

    private String name, details, email;
    private int likes, reps, age, followers;


    public User(String name, int age, String details, String email, int likes, int reps, int followers) {

        this.name = name;
        this.age = age;
        this.details = details;
        this.email = email;
        this.likes = likes;
        this.reps = reps;
        this.followers = followers;

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
}