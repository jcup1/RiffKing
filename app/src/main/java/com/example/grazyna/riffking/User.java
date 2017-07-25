package com.example.grazyna.riffking;

/**
 * Created by jakub on 25.07.17.
 */

class User {

    private String name, age, details, followers;
    private int email, likes, reps;


    public User(String name, String age, String details, int email, int likes, int reps, String followers) {

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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public int getEmail() {
        return email;
    }

    public void setEmail(int email) {
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
}
