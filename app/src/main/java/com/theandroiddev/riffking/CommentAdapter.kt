package com.theandroiddev.riffking

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso

/**
 * Created by jakub on 29.09.17.
 */

class CommentAdapter(private val context: Context, private val comments: List<Comment>, internal var databaseReference: DatabaseReference, private val currentUserId: String) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    protected var helper: Helper = Helper(context)
    private val liked: BooleanArray = BooleanArray(100)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_comment, parent, false)
        val holder = ViewHolder(v)

        //getUser(holder.getAdapterPosition(), holder.likeIv, holder.userTv, holder.userIv);

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        setDatabase(comments[holder.adapterPosition].threadId, comments[holder.adapterPosition].id,
                holder.likeIv, position)
        setUserName(holder.userTv, holder.userIv, holder.adapterPosition)
        holder.contentTv.text = comments[position].content
        holder.dateTv.text = comments[position].date
        holder.likesTv.text = comments[position].likes.toString()


        holder.likeIv.setOnClickListener {
            if (comments[position].userId != currentUserId) {

                if (holder.adapterPosition != -1) {
                    handleLike(comments[holder.adapterPosition].threadId, comments[holder.adapterPosition].id,
                            holder.likeIv, holder.adapterPosition)
                }


            }
        }

        holder.userIv.setOnClickListener {
            if (holder.adapterPosition != -1)
                openProfileFragment(holder.adapterPosition)
        }

        holder.userTv.setOnClickListener {
            if (holder.adapterPosition != -1)
                openProfileFragment(holder.adapterPosition)
        }

    }

    private fun handleLike(threadId: String?, commentId: String?, likeIv: ImageView, position: Int) {
        Log.e(TAG, "handleLike: thre" + threadId + "comm" + commentId)
        val userId = comments[position].userId
        if(threadId != null && commentId != null && userId != null) {
            if (!liked[position]) {
                helper.transaction(databaseReference.child("comments").child(threadId).child(commentId).child("likes"), 1)
                helper.transaction(databaseReference.child("users").child(userId).child("likes"), 1)
                databaseReference.child("commentLikes").child(currentUserId).child(commentId).setValue(true)

            } else {
                helper.transaction(databaseReference.child("comments").child(threadId).child(commentId).child("likes"), -1)
                helper.transaction(databaseReference.child("users").child(userId).child("likes"), -1)
                databaseReference.child("commentLikes").child(currentUserId).child(commentId).removeValue()

            }
        }
    }

    private fun setDatabase(threadId: String?, commentId: String?, likeIv: ImageView, position: Int) {

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (currentUserId != comments[position].userId && commentId != null) {

                    if (dataSnapshot.child("commentLikes").child(currentUserId).child(commentId)
                                    .value != null) {
                        Log.d(TAG, "onDataChangeComment: " + "already liked")
                        liked[position] = true
                        helper.setLiked(likeIv)

                    } else {
                        Log.d(TAG, "onDataChangeComment: not liked!")
                        liked[position] = false
                        helper.setUnliked(likeIv)

                    }

                } else {
                    helper.setLikeInactive(likeIv)
                }

            }


            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    override fun getItemCount(): Int {
        return comments.size
    }

    fun setUserName(userTv: TextView, userIv: ImageView, position: Int) {

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //Change color of creator's comment
                val threadId = comments[position].threadId
                val userId = comments[position].userId
                if (position >= 0 && threadId != null && userId != null) {

                    if (dataSnapshot.child("threads").child(threadId)
                                    .child("userId").value != null) {
                        if (dataSnapshot.child("threads").child(threadId)
                                        .child("userId").value == comments[position].userId) {
                            Log.d(TAG, "onDataChange: " + dataSnapshot.child("threads")
                                    .child(threadId).child("userId").value
                                    + " = " + comments[position].userId)
                            helper.highlightUser(userTv)
                        }
                    }

                    val userName: String = dataSnapshot.child("users").child(userId).child("name")
                            .value as String
                    val photoUrl: String = dataSnapshot.child("users").child(userId)
                            .child("photoUrl").value as String

                    userTv.text = userName
                    Picasso.get().load(photoUrl).into(userIv)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private fun openProfileFragment(position: Int) {

        val profileFragment = ProfileFragment()
        val bundle = Bundle()
        bundle.putString("USER_ID", comments[position].userId)
        bundle.putString("CURRENT_USER_ID", currentUserId) //Two same only in this case
        profileFragment.arguments = bundle

        val fragmentTransaction = (context as HomeActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_home, profileFragment).addToBackStack(null)
        fragmentTransaction.commit()

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userTv: TextView
        val contentTv: TextView
        val dateTv: TextView
        val likesTv: TextView
        val userIv: CircularImageView
        val likeIv: ImageView

        init {

            userTv = itemView.findViewById<View>(R.id.comment_user_tv) as TextView
            contentTv = itemView.findViewById<View>(R.id.comment_content_tv) as TextView
            dateTv = itemView.findViewById<View>(R.id.comment_date_tv) as TextView
            likesTv = itemView.findViewById<View>(R.id.comment_likes_number_tv) as TextView

            userIv = itemView.findViewById<View>(R.id.comment_user_iv) as CircularImageView
            likeIv = itemView.findViewById<View>(R.id.comment_like_iv) as ImageView

        }


    }

    companion object {
        private val TAG = "RepAdapter"
    }
}
