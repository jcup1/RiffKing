package com.theandroiddev.riffking.presentation.thread

import android.graphics.Bitmap
import android.graphics.Color.GRAY
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.theandroiddev.riffking.utils.Helper.Companion.ytIdLength
import java.io.IOException
import java.net.URL

/**
 * Created by jakub on 22.07.17.
 */

class GetThumbnail(private var thumbURL: String,
                   private var singleThumb: ImageView) : AsyncTask<String, String, Bitmap>() {
    private var icon_val: Bitmap? = null
    private var thumbnailURL: URL? = null
    private var layoutWidth: Int = 0

    override fun doInBackground(vararg params: String): Bitmap? {

        val urlLink = thumbURL
        if (urlLink.length > ytIdLength) {
            var ytId = urlLink.substring(urlLink.length - ytIdLength, urlLink.length)
            Log.d(TAG, "doInBackground: $ytId")
            if (ytId.length >= ytIdLength) {

                ytId = ytId.substring(0, ytIdLength)

                try {
                    //thumbnailURL = new URL("https://img.youtube.com/vi/" + ytId + "/0.jpg");
                    thumbnailURL = URL("https://img.youtube.com/vi/$ytId/mqdefault.jpg")
                    //icon_val = BitmapFactory.decodeStream(thumbnailURL.openConnection().getInputStream());

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

        }

        return null
    }

    override fun onPostExecute(bf: Bitmap) {

        super.onPostExecute(icon_val)

        Picasso.get().load(thumbnailURL.toString())
                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .resize(singleThumb.width, singleThumb.width * 9 / 16)
                .placeholder(ColorDrawable(GRAY))
                .into(singleThumb)
    }

    companion object {
        private val TAG = "GetThumbnail"
    }
}
