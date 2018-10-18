package com.theandroiddev.riffking.utils

import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.*
import com.theandroiddev.riffking.R
import com.theandroiddev.riffking.presentation.home.HomeFragment
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by jakub on 01.10.17.
 */

class Helper(internal var context: Context) {

    //TODO take care of date format
    //"EEE MMM dd HH:mm:ss 'GMT' yyyy", Locale.getDefault());
    //inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
    val currentDate: String
        get() {

            val inputFormat = SimpleDateFormat("HH:mm MMM dd", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("GMT+2")

            return inputFormat.format(Calendar.getInstance().time)

        }


    internal fun transaction(ref: DatabaseReference, number: Int) {
        ref.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                var num = mutableData.getValue(Int::class.java)!!
                Log.d(TAG, "doTransaction: num " + num + "number " + number)

                num += number

                // Set value and report transaction success
                mutableData.value = num
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, b: Boolean,
                                    dataSnapshot: DataSnapshot?) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError!!)
            }
        })
    }

    fun setRecyclerViewLayoutManager(layoutManagerType: HomeFragment.LayoutManagerType, mRecyclerView: RecyclerView, activity: Activity,
                                     mLayoutManager: RecyclerView.LayoutManager, mCurrentLayoutManagerType: HomeFragment.LayoutManagerType) {
        val layoutManager: RecyclerView.LayoutManager
        val currentLayoutManagerType: HomeFragment.LayoutManagerType
        var scrollPosition = 0

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.layoutManager != null) {
            scrollPosition = (mRecyclerView.layoutManager as LinearLayoutManager)
                    .findFirstCompletelyVisibleItemPosition()
        }

        when (layoutManagerType) {
            HomeFragment.LayoutManagerType.GRID_LAYOUT_MANAGER -> {
                layoutManager = GridLayoutManager(activity, SPAN_COUNT)
                currentLayoutManagerType = HomeFragment.LayoutManagerType.GRID_LAYOUT_MANAGER
            }
            HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER -> {
                layoutManager = LinearLayoutManager(activity)
                currentLayoutManagerType = HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER
            }
        }

        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.scrollToPosition(scrollPosition)
    }


    fun setLiked(threadLikeIv: ImageView?) {
        threadLikeIv?.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent))
    }

    fun setUnliked(threadLikeIv: ImageView?) {
        threadLikeIv?.setColorFilter(ContextCompat.getColor(context, R.color.darker_gray))
    }

    fun setLikeInactive(threadLikeIv: ImageView?) {
        threadLikeIv?.setColorFilter(ContextCompat.getColor(context, R.color.gray))
    }

    fun setFollowed(followIv: Button?) {
        if (followIv != null) {
            followIv.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            followIv.text = "Unfollow"
        }
    }

    fun setUnfollowed(followIv: Button?) {
        if (followIv != null) {
            followIv.setTextColor(ContextCompat.getColor(context, R.color.darker_gray))
            followIv.text = "Follow"
        }
    }

    fun highlightUser(userTv: TextView?) {
        userTv?.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
    }

    companion object {
        internal val SPAN_COUNT = 2
        private val TAG = "Helper"
        var ytIdLength = 11
    }
}
