package com.example.grazyna.riffking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by grazyna on 2017-07-15.
 */

 class MySingleton {

    private MySingleton mInstance;
    private Context context;
    private RequestQueue requestQueue;

    private MySingleton(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public synchronized MySingleton getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {

        requestQueue.add(request);


    }

}