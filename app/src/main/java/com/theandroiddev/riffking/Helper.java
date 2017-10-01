package com.theandroiddev.riffking;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by jakub on 01.10.17.
 */

public class Helper {
    static final int SPAN_COUNT = 2;
    private static final String TAG = "Helper";
    static int YTIDLENGTH = 11;


    void transacton(final DatabaseReference ref, final int number) {
        //TODO IMPLEMENT IT IN HELPER TOO
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                int num = mutableData.getValue(Integer.class);

                num += number;

                // Set value and report transaction success
                mutableData.setValue(num);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public String getCurrentDate() {
        //TODO take care of date format

        SimpleDateFormat inputFormat = new SimpleDateFormat(
                "HH:mm MMM dd", Locale.getDefault());
        //"EEE MMM dd HH:mm:ss 'GMT' yyyy", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        //inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        return inputFormat.format(Calendar.getInstance().getTime());

    }


}
