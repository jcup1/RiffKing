package com.theandroiddev.riffking.presentation.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theandroiddev.riffking.Helper
import com.theandroiddev.riffking.R
import kotlinx.android.synthetic.main.fragment_profile_rep.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileRepFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileRepFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileRepFragment : Fragment() {

    protected var database = FirebaseDatabase.getInstance().reference

    protected var helper: Helper? = null
    private var currentUserId: String? = null
    private var userId: String? = null
    private var currentUserRep: Int? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            userId = bundle.getString("USER_ID")
            currentUserId = bundle.getString("CURRENT_USER_ID")
        }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentUserId = currentUserId
                if (currentUserId != null) {
                    currentUserRep = dataSnapshot.child("users").child(currentUserId).child("reps").getValue(Int::class.java)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile_rep, container, false)

        profile_rep_et.setText(50.toString())


        profile_rep_minus_iv.setOnClickListener {
            profile_rep_et.setText((Integer.valueOf(profile_rep_et.text.toString()) - 5).toString())
        }

        profile_rep_plus_iv.setOnClickListener {
            profile_rep_et.setText((Integer.valueOf(profile_rep_et.text.toString()) + 5).toString())
        }

        profile_rep_transfer_btn.setOnClickListener {
            if (!TextUtils.isEmpty(profile_rep_et.text.toString()) && !TextUtils.isEmpty(profile_rep_title_et.text.toString())) {
                Log.d(TAG, "transferRep: $currentUserId to $userId")
                transferRep()
            }
        }

        return rootView
    }

    private fun transferRep() {
        val currentUserRep = currentUserRep
        val userId = userId
        val currentUserId = currentUserId
        if (currentUserRep != null && userId != null && currentUserId != null) {
            if (userId != currentUserId) {

                if (Integer.valueOf(profile_rep_et.text.toString()) <= currentUserRep) {

                    val rep = Rep(Integer.valueOf(profile_rep_et.text.toString()), currentUserId, helper?.currentDate
                            ?: "N/A", profile_rep_title_et.text.toString())
                    database.child("reps").child(userId).push().setValue(rep)

                    helper?.transaction(database.child("users").child(currentUserId).child("reps"),
                            -Integer.valueOf(profile_rep_et.text.toString()))
                    helper?.transaction(database.child("users").child(userId).child("reps"),
                            Integer.valueOf(profile_rep_et.text.toString()))
                    profile_rep_title_et.setText("")
                    profile_rep_et.setText(R.string.profile_rep_value_default)
                    snackRepSent()
                } else {
                    snackRepSentFailed()
                }
            }
        }
    }

    private fun snackRepSentFailed() {
        Snackbar.make(profile_rep_layout, R.string.profile_rep_not_enough, Snackbar.LENGTH_SHORT).show()
    }

    private fun snackRepSent() {
        Snackbar.make(profile_rep_layout, R.string.profile_rep_sent, Snackbar.LENGTH_SHORT).show()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener?.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
            helper = Helper(context)
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private val TAG = "ProfileRepFragment"

        fun newInstance(param1: String, param2: String): ProfileRepFragment {

            return ProfileRepFragment()
        }
    }
}// Required empty public constructor
