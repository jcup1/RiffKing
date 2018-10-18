package com.theandroiddev.riffking.presentation.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.theandroiddev.riffking.Helper
import com.theandroiddev.riffking.R
import com.theandroiddev.riffking.User
import com.theandroiddev.riffking.presentation.common.Comment
import com.theandroiddev.riffking.presentation.common.CommentAdapter
import com.theandroiddev.riffking.presentation.home.HomeFragment
import kotlinx.android.synthetic.main.fragment_profile_comments.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileCommentsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileCommentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileCommentsFragment : Fragment() {
    protected var layoutManager: RecyclerView.LayoutManager? = null
    protected var commentAdapter: CommentAdapter? = null
    protected var comments: MutableList<Comment> = mutableListOf()
    protected var currentLayoutManagerType: HomeFragment.LayoutManagerType? = null

    internal var database: DatabaseReference? = null
    internal var helper: Helper? = null

    internal var currentUser: User? = null

    private var listener: OnFragmentInteractionListener? = null

    private var userId: String? = null
    private var currentUserId: String? = null

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = User()
        currentUser = User()
        val bundle = this.arguments
        if (bundle != null) {
            userId = bundle.getString("USER_ID")
            currentUserId = bundle.getString("CURRENT_USER_ID")
        }

        comments = ArrayList()
        database = FirebaseDatabase.getInstance().reference

        database?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userId = userId
                val currentUserId = currentUserId
                if (userId != null && currentUserId != null) {
                    user = dataSnapshot.child("users").child(userId).getValue(User::class.java)
                    currentUser = dataSnapshot.child("users").child(currentUserId).getValue(User::class.java)

                    profile_comments_number_tv.text = user?.comments.toString()
                    Picasso.get().load(currentUser?.photoUrl).into(profile_comments_user_iv)

                    Log.d(TAG, "userrr: " + currentUser.toString())

                    comments.clear()
                    val children = dataSnapshot.child("comments").child(userId).children

                    for (child in children) {
                        val comment = child.getValue(Comment::class.java)
                        if (comment != null) {
                            comment.id = child.key
                            comments.add(0, comment)
                        }
                    }
                    commentAdapter?.notifyDataSetChanged()

                    // dataSnapshot.child("comments")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }

        })

    }

    override fun onResume() {
        super.onResume()
        profile_comments_number_tv.text = user?.comments.toString()
        Picasso.get().load(currentUser?.photoUrl).into(profile_comments_user_iv)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_profile_comments, container, false)

        layoutManager = LinearLayoutManager(activity)

        profile_comments_rv.isNestedScrollingEnabled = true


        currentLayoutManagerType = HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            currentLayoutManagerType = savedInstanceState
                    .getSerializable(HomeFragment.KEY_LAYOUT_MANAGER) as HomeFragment.LayoutManagerType
        }

        val context = context
        val database = database
        val currentUserId = currentUserId
        if (context != null && database != null && currentUserId != null) {

            commentAdapter = CommentAdapter(context, comments, database, currentUserId)
            Log.d(TAG, "onDataChange2: " + comments.toString())

            profile_comments_rv.isNestedScrollingEnabled = true
            profile_comments_rv.adapter = commentAdapter

            val activity = activity
            val layoutManager = layoutManager
            val currentLayoutManagerType = currentLayoutManagerType
            if (activity != null && layoutManager != null && currentLayoutManagerType != null) {
                helper?.setRecyclerViewLayoutManager(HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER, profile_comments_rv,
                        activity, layoutManager, currentLayoutManagerType)

            }
            profile_comments_send_iv.setOnClickListener {
                if (!TextUtils.isEmpty(profile_comments_content_et.text.toString())) {
                    val userId = userId
                    if (userId != null) {
                        val comment = Comment(userId, currentUserId, profile_comments_content_et.text.toString(), helper?.currentDate
                                ?: "N/A", 0)
                        database.child("comments").child(userId).push().setValue(comment)
                        profile_comments_content_et.setText("")
                        helper?.transaction(database.child("users").child(userId).child("comments"), 1)
                    }
                }
            }
        }

        return rootView
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (listener != null) {
            listener?.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
            helper = Helper(context)
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
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
        private val TAG = "ProfileCommentsFragment"

        fun newInstance(param1: String, param2: String): ProfileCommentsFragment {
            val fragment = ProfileCommentsFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
