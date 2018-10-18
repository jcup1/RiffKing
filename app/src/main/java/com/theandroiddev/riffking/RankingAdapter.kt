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

class RankingAdapter internal constructor(private val context: Context, private val users: List<User>, private val databaseReference: DatabaseReference, private val currentUserId: String) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {
    protected var helper: Helper = Helper(context)
    internal var comment: Comment? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_ranking, parent, false)
        val holder = ViewHolder(v)

        //getUser(holder.getAdapterPosition(), holder.likeIv, holder.userTv, holder.userIv);

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        setDatabase(users[holder.adapterPosition].id, position)

        setUser(holder.rankingUserTv, holder.rankingUserIv, holder.rankingRepTv,
                holder.rankingNumber, holder.rankingLogo, holder.adapterPosition)

        holder.rankingUserIv.setOnClickListener {
            if (holder.adapterPosition != -1)
                openProfileFragment(holder.adapterPosition)
        }

        holder.rankingUserTv.setOnClickListener {
            if (holder.adapterPosition != -1)
                openProfileFragment(holder.adapterPosition)
        }

    }

    private fun setDatabase(userId: String?, position: Int) {

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    override fun getItemCount(): Int {
        return users.size
    }

    private fun setUser(userTv: TextView, userIv: ImageView, repTv: TextView, numTv: TextView, logoIv: CircularImageView, position: Int) {

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                if (currentUserId == users[position].id) {
                    helper.highlightUser(userTv)
                }
                Log.d(TAG, "USERSTESTTT: " + position + " " + users.size)
                userTv.text = dataSnapshot.child("users").child(users[position].id!!).child("name").getValue(String::class.java)
                repTv.text = dataSnapshot.child("users").child(users[position].id!!).child("reps").getValue(Int::class.java).toString()
                Picasso.get().load(dataSnapshot.child("users")
                        .child(users[position].id!!).child("photoUrl").getValue(String::class.java))
                        .into(userIv)

                if (users[position].ranking == 1) {
                    userTv.textSize = 18f
                    repTv.textSize = 18f

                    helper.highlightUser(userTv)
                    helper.highlightUser(repTv)
                    logoIv.setBackgroundResource(R.drawable.logo)
                } else {
                    numTv.text = users[position].ranking.toString()
                }
            }


            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private fun openProfileFragment(position: Int) {

        val profileFragment = ProfileFragment()
        val bundle = Bundle()
        bundle.putString("USER_ID", users[position].id)
        bundle.putString("CURRENT_USER_ID", currentUserId)
        profileFragment.arguments = bundle

        val fragmentTransaction = (context as HomeActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_home, profileFragment).addToBackStack(null)
        fragmentTransaction.commit()

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val rankingUserTv: TextView
        val rankingRepTv: TextView
        val rankingNumber: TextView
        val rankingUserIv: CircularImageView
        val rankingLogo: CircularImageView

        init {

            rankingUserTv = itemView.findViewById<View>(R.id.ranking_user_tv) as TextView
            rankingRepTv = itemView.findViewById<View>(R.id.ranking_rep_tv) as TextView
            rankingNumber = itemView.findViewById<View>(R.id.ranking_number) as TextView
            rankingUserIv = itemView.findViewById<View>(R.id.ranking_user_iv) as CircularImageView
            rankingLogo = itemView.findViewById<View>(R.id.ranking_logo) as CircularImageView

        }

    }

    companion object {
        private val TAG = "RepAdapter"
    }
}
