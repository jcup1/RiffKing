package com.theandroiddev.riffking

class Comment(threadId: String, userId: String, content: String, date: String, var likes: Int) {

    var id: String? = null
    var threadId: String? = threadId
    var userId: String? = userId
    var content: String? = content
    var date: String? = date

    override fun toString(): String {
        return threadId + "\n" + userId
    }
}
