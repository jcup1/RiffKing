package com.theandroiddev.riffking;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    Context context;

    public Helper(Context context) {
        this.context = context;
    }


    void transaction(final DatabaseReference ref, final int number) {
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                int num = mutableData.getValue(Integer.class);
                Log.d(TAG, "doTransaction: num " + num + "number " + number);

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

    public void setRecyclerViewLayoutManager(HomeFragment.LayoutManagerType layoutManagerType, RecyclerView mRecyclerView, Activity activity,
                                             RecyclerView.LayoutManager mLayoutManager, HomeFragment.LayoutManagerType mCurrentLayoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(activity, SPAN_COUNT);
                mCurrentLayoutManagerType = HomeFragment.LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(activity);
                mCurrentLayoutManagerType = HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(activity);
                mCurrentLayoutManagerType = HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }


    public void setLiked(ImageView threadLikeIv) {
        if (threadLikeIv != null)
            threadLikeIv.setColorFilter(context.getResources().getColor(R.color.colorAccent));
    }

    public void setUnliked(ImageView threadLikeIv) {
        if (threadLikeIv != null)
            threadLikeIv.setColorFilter(context.getResources().getColor(R.color.darker_gray));
    }

    public void setLikeInactive(ImageView threadLikeIv) {
        if (threadLikeIv != null)
            threadLikeIv.setColorFilter(context.getResources().getColor(R.color.gray));
    }

    public void setFollowed(Button followIv) {
        if (followIv != null) {
            followIv.setTextColor(context.getResources().getColor(R.color.colorAccent));
            followIv.setText("Unfollow");
        }
    }

    public void setUnfollowed(Button followIv) {
        if (followIv != null) {
            followIv.setTextColor(context.getResources().getColor(R.color.darker_gray));
            followIv.setText("Follow");
        }
    }

    public void highlightUser(TextView userTv) {
        if (userTv != null) {
            userTv.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
    }
}
