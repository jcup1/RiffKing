package com.theandroiddev.riffking

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by jakub on 25.07.17.
 */

@IgnoreExtraProperties
class User {

    var name: String? = null
    var details: String? = null
    var email: String? = null
    var id: String? = null
    var photoUrl: String? = null
    var likes: Int = 0
    var reps: Int = 0
    var age: Int = 0
    var followers: Int = 0
    var comments: Int = 0
    var ranking: Int = 0

    constructor()

    constructor(name: String, email: String, photoUrl: String, likes: Int, reps: Int, age: Int, followers: Int, comments: Int) {
        this.name = name
        this.email = email
        this.photoUrl = photoUrl
        this.likes = likes
        this.reps = reps
        this.age = age
        this.followers = followers
        this.comments = comments
    }

}