package com.theandroiddev.riffking

import com.theandroiddev.riffking.Helper.Companion.ytIdLength

/**
 * Created by jakub on 19.07.17.
 */

class Thread(userId: String, title: String, videoUrl: String, description: String, category: String,
             date: String, var likes: Int, var views: Int) {

    var id: String? = null
    var userId: String? = userId
    var title: String? = title
    var videoUrl: String? = videoUrl
    var description: String? = description
    var category: String? = category
    var date: String? = date

    val youtubeId: String?
        get() {
            val videoUrl = videoUrl
            if (videoUrl != null) {
                if (videoUrl.length > ytIdLength) {
                    var youtubeId = videoUrl.substring(videoUrl.length - ytIdLength, videoUrl.length)
                    if (youtubeId.length >= ytIdLength) {

                        youtubeId = youtubeId.substring(0, ytIdLength)

                        return youtubeId
                    }

                }
            }
            return videoUrl
        }

}
