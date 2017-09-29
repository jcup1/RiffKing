package com.theandroiddev.riffking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

/**
 * Created by jakub on 22.07.17.
 */

public class GetThumbnail extends AsyncTask<String, String, Bitmap> {
    private static final String TAG = "GetThumbnail";
    public static int YTIDLENGTH = 11;
    Bitmap icon_val;
    String thumbURL;
    ImageView singleThumb;

    public GetThumbnail(String url, ImageView singleThumbnail) {

        this.thumbURL = url;
        this.singleThumb = singleThumbnail;

    }

    @Override
    protected Bitmap doInBackground(String... params) {


        String URLLink = thumbURL;
        if (URLLink.length() > YTIDLENGTH) {
            String ytId = URLLink.substring(URLLink.length() - 11, URLLink.length());
            Log.d(TAG, "doInBackground: " + ytId);
            if (ytId.length() >= YTIDLENGTH) {

                ytId = ytId.substring(0, YTIDLENGTH);

                try {
                    URL thumbnailURL = new URL("https://img.youtube.com/vi/" + ytId + "/0.jpg");
                    icon_val = BitmapFactory.decodeStream(thumbnailURL.openConnection().getInputStream());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bf) {

        super.onPostExecute(icon_val);
        singleThumb.setImageBitmap(icon_val);
    }
}
