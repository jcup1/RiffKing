package com.theandroiddev.riffking.presentation.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.*
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import com.theandroiddev.riffking.R
import com.theandroiddev.riffking.presentation.common.User
import com.theandroiddev.riffking.presentation.home.HomeActivity
import com.theandroiddev.riffking.utils.Helper
import kotlinx.android.synthetic.main.fragment_profile.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    internal var profileImg: CircularImageView? = null

    private var user: User? = null
    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var helper: Helper? = null
    private var isFollowed: Boolean? = false
    var userId: String? = null
    var currentUserId: String? = null
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            userId = bundle.getString("USER_ID")
            currentUserId = bundle.getString("CURRENT_USER_ID")
        }

        initUser()

    }

    private fun initUser() {

        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val userId = userId
                val currentUserId = currentUserId
                if (userId != null && currentUserId != null) {
                    user = dataSnapshot.child("users").child(userId).getValue(User::class.java)
                    if (currentUserId == userId) {
                        //so you can't follow yourself
                        profile_follow_btn.visibility = View.GONE
                    }

                    isFollowed = if (dataSnapshot.child("userFollowers").child(currentUserId).child(userId).getValue(Boolean::class.java) != null) {
                        Log.d(TAG, "onDataChange: already isFollowed!")
                        true

                    } else {
                        Log.d(TAG, "onDataChange: not isFollowed!")
                        false
                    }
                    val user = user
                    if (user != null) {
                        initData(user)
                    } else {
                        //TODO error
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    override fun onResume() {
        super.onResume()

        val homeActivity = activity as HomeActivity?
        homeActivity?.fab?.visibility = View.GONE
        val user = user
        if (profileImg != null && user != null) {
            initUser()
            initData(user)

        }

    }

    private fun initData(user: User) {

        profile_name_tv.text = user.name
        profile_desc_tv.text = user.details
        profile_email_tv.text = user.email
        profile_money_tv.text = user.reps.toString()
        profile_followers_tv.text = user.followers.toString()
        profile_likes_tv.text = user.likes.toString()

        if (isFollowed == true) {
            helper?.setFollowed(profile_follow_btn)
        } else {
            helper?.setUnfollowed(profile_follow_btn)
        }

        profile_follow_btn.setOnClickListener {
            val userId = userId
            val currentUserId = currentUserId

            if (userId != null && currentUserId != null) {
                if (isFollowed == true) {
                    mDatabase.child("use" + "rFollowers").child(currentUserId).child(userId).removeValue()

                    helper?.transaction(mDatabase.child("users").child(userId).child("followers"), -1)
                    helper?.setUnfollowed(profile_follow_btn)

                } else {
                    mDatabase.child("userFollowers").child(currentUserId).child(userId).setValue(true)

                    helper?.transaction(mDatabase.child("users").child(userId).child("followers"), 1)
                    helper?.setFollowed(profile_follow_btn)
                }

            }

            profile_pm_img.setOnClickListener {
                //TODO implement message fragments
                Toast.makeText(context, "Not Ready Yet :(", Toast.LENGTH_SHORT).show()
            }
            Picasso.get().load(user.photoUrl).into(profileImg)

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)


        tabhost.setup(activity, childFragmentManager, R.id.tab_container)

        val bundle = Bundle()
        bundle.putString("USER_ID", userId)
        bundle.putString("CURRENT_USER_ID", currentUserId)

        tabhost.addTab(tabhost.newTabSpec("profilecommentsfragment").setIndicator("Comments"),
                ProfileCommentsFragment::class.java, bundle)
        tabhost.addTab(tabhost.newTabSpec("profilevideosfragment").setIndicator("Videos"),
                ProfileVideosFragment::class.java, bundle)
        if (currentUserId == userId) {
            tabhost.addTab(tabhost.newTabSpec("profilerepfragment").setIndicator("Rep"),
                    ProfileRepFragmentMe::class.java, bundle)
        } else {
            tabhost.addTab(tabhost.newTabSpec("profilerepfragment").setIndicator("Rep"),
                    ProfileRepFragment::class.java, bundle)
        }


        return rootView
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
        private val TAG = "ProfileFragment"
        private val USER_ID = "userId"
        private val CURRENT_USER_ID = "currentUserId"

        fun newInstance(userId: String, currentUserId: String): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString(USER_ID, userId)
            args.putString(CURRENT_USER_ID, currentUserId)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
