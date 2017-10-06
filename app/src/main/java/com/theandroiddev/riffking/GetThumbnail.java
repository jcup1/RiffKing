package com.theandroiddev.riffking;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

import static com.theandroiddev.riffking.Helper.YTIDLENGTH;

/**
 * Created by jakub on 22.07.17.
 */

public class GetThumbnail extends AsyncTask<String, String, Bitmap> {
    private static final String TAG = "GetThumbnail";
    Bitmap icon_val;
    String thumbURL;
    ImageView singleThumb;
    Context context;
    URL thumbnailURL;

    public GetThumbnail(String url, ImageView singleThumbnail, Context context) {

        this.thumbURL = url;
        this.singleThumb = singleThumbnail;
        this.context = context;

    }

    @Override
    protected Bitmap doInBackground(String... params) {


        String URLLink = thumbURL;
        if (URLLink.length() > YTIDLENGTH) {
            String ytId = URLLink.substring(URLLink.length() - YTIDLENGTH, URLLink.length());
            Log.d(TAG, "doInBackground: " + ytId);
            if (ytId.length() >= YTIDLENGTH) {

                ytId = ytId.substring(0, YTIDLENGTH);

                try {
                    //thumbnailURL = new URL("https://img.youtube.com/vi/" + ytId + "/0.jpg");
                    thumbnailURL = new URL("https://img.youtube.com/vi/" + ytId + "/mqdefault.jpg");
                    //icon_val = BitmapFactory.decodeStream(thumbnailURL.openConnection().getInputStream());

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
        Picasso.with(context).load(String.valueOf(thumbnailURL))
                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .resize(singleThumb.getWidth(), singleThumb.getWidth() * 9 / 16)
                .into(singleThumb);
        singleThumb.setImageBitmap(icon_val);
    }
}
