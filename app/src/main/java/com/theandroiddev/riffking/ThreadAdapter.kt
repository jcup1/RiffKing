package com.theandroiddev.riffking

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Created by jakub on 18.07.17.
 */

class ThreadAdapter(private val context: Context, private val threads: ArrayList<Thread>, internal var mDatabase: DatabaseReference) : RecyclerView.Adapter<ThreadAdapter.ViewHolder>(), AsyncResponse {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_thread,
                parent, false)

        //TODO set ThumbnailIv size on start

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "Element $position set.")

        val videoUrl = threads[position].videoUrl
        if (videoUrl != null) {
            GetThumbnail(videoUrl, holder.threadThumbnailIv).execute()
        }
        holder.threadTitleTv.text = threads[position].title
        setThreadCreator(position, holder.threadUserTv, holder.threadUserIv)

        holder.itemView.setOnClickListener {
            //Toast.makeText(context, String.valueOf(position), Toast.LENGTH_SHORT).show();
            openThread(holder.adapterPosition)
        }
        holder.threadStatsTv.text = initStats(threads[position])
        holder.threadDateTv.text = initDate(threads[position])

    }

    private fun initStats(thread: Thread): String {

        return thread.views.toString() + " views " + thread.likes + " likes"

    }

    private fun initDate(thread: Thread): String? {

        return thread.date

    }

    private fun openThread(position: Int) {

        val bundle = Bundle()
        bundle.putString("threadId", threads[position].id.toString())

        val threadFragment = ThreadFragment()
        threadFragment.arguments = bundle
        try {
            val fragmentManager = (context as FragmentActivity).supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.content_home, threadFragment).addToBackStack("stack1")
            transaction.commit()

        } catch (e: ClassCastException) {
            Log.e(TAG, "cant get fragment manager")
        }

    }


    override fun getItemCount(): Int {
        return threads.size
    }

    override fun processFinish(output: Bitmap) {

    }

    fun setThreadCreator(position: Int, tv: TextView, iv: CircularImageView) {

        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //Picasso.with(iv.getContext()).cancelRequest(iv);

                tv.text = dataSnapshot.child("users").child(threads[position].userId!!).child("name").getValue(String::class.java)
                //                Picasso.with(context).load(dataSnapshot.child("users").child(threads.get(position).getUserId()).child("photoUrl").getValue(String.class))
                //                        .into(iv);
                Picasso.get().load(dataSnapshot.child("users").child(threads[position]
                        .userId!!).child("photoUrl").getValue(String::class.java)).into(iv)

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val threadTitleTv: TextView
        val threadStatsTv: TextView
        val threadUserTv: TextView
        val threadDateTv: TextView
        val threadThumbnailIv: ImageView
        val threadUserIv: CircularImageView
        val threadLayout: LinearLayout

        init {

            threadTitleTv = itemView.findViewById<View>(R.id.text_view) as TextView
            threadUserTv = itemView.findViewById<View>(R.id.nick_tv) as TextView
            threadStatsTv = itemView.findViewById<View>(R.id.stats_tv) as TextView
            threadDateTv = itemView.findViewById<View>(R.id.date_tv) as TextView
            threadUserIv = itemView.findViewById<View>(R.id.nick_iv) as CircularImageView
            threadThumbnailIv = itemView.findViewById<View>(R.id.thumbnail_img) as ImageView
            threadLayout = itemView.findViewById<View>(R.id.thread_layout) as LinearLayout
        }
    }

    companion object {
        private val TAG = "ThreadAdapter"
    }

}
