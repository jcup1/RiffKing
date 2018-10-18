package com.theandroiddev.riffking

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso

/**
 * Created by jakub on 01.10.17.
 */

class RepAdapter(private val context: Context, private val reps: List<Rep>, internal var databaseReference: DatabaseReference, private val currentUserId: String) : RecyclerView.Adapter<RepAdapter.ViewHolder>() {
    private var rep: Rep? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_rep, parent, false)
        val holder = ViewHolder(v)

        //getUser(holder.getAdapterPosition(), holder.likeIv, holder.userTv, holder.userIv);

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        setDatabase(position)
        setUserName(holder.repUserTv, holder.repUserIv, position)
        holder.repTitleTv.text = reps[position].title
        holder.repDateTv.text = reps[position].date
        holder.repTv.text = reps[position].rep.toString()


        holder.repUserIv.setOnClickListener {
            if (holder.adapterPosition != -1)
                openProfileFragment(holder.adapterPosition)
        }

        holder.repUserTv.setOnClickListener {
            if (holder.adapterPosition != -1)
                openProfileFragment(holder.adapterPosition)
        }

    }

    private fun setDatabase(position: Int) {

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.child("reps").child(currentUserId).child(reps[position].userId!!).getValue(Rep::class.java) != null) {
                    rep = dataSnapshot.child("reps").child(currentUserId).child(reps[position].userId!!).getValue(Rep::class.java)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    override fun getItemCount(): Int {
        return reps.size
    }

    fun setUserName(repUserTv: TextView, repUserIv: CircularImageView, position: Int) {

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (position >= 0) {

                    repUserTv.text = dataSnapshot.child("users").child(reps[position].userId!!).child("name").getValue(String::class.java)
                    Picasso.get().load(dataSnapshot.child("users")
                            .child(reps[position].userId!!).child("photoUrl").getValue(String::class.java))
                            .into(repUserIv)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private fun openProfileFragment(position: Int) {

        val profileFragment = ProfileFragment()
        val bundle = Bundle()
        bundle.putString("USER_ID", reps[position].userId)
        bundle.putString("CURRENT_USER_ID", currentUserId) //Two same only in this case
        profileFragment.arguments = bundle

        val fragmentTransaction = (context as HomeActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_home, profileFragment).addToBackStack(null)
        fragmentTransaction.commit()

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val repUserTv: TextView
        val repTitleTv: TextView
        val repDateTv: TextView
        val repTv: TextView
        val repUserIv: CircularImageView

        init {

            repUserTv = itemView.findViewById<View>(R.id.rep_user_tv) as TextView
            repTitleTv = itemView.findViewById<View>(R.id.rep_title_tv) as TextView
            repDateTv = itemView.findViewById<View>(R.id.rep_date_tv) as TextView
            repTv = itemView.findViewById<View>(R.id.rep_tv) as TextView
            repUserIv = itemView.findViewById<View>(R.id.rep_user_iv) as CircularImageView

        }


    }

    companion object {
        private val TAG = "RepAdapter"
    }
}
