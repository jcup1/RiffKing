package com.theandroiddev.riffking

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
import com.theandroiddev.riffking.HomeFragment.Companion.KEY_LAYOUT_MANAGER
import kotlinx.android.synthetic.main.fragment_profile_videos.*
import java.util.*


class ProfileVideosFragment : Fragment() {

    protected var threadAdapter: ThreadAdapter? = null
    protected var layoutManager: RecyclerView.LayoutManager? = null
    protected var threads: ArrayList<Thread> = arrayListOf()
    protected var threadsToRemove: ArrayList<Thread> = arrayListOf()
    protected var currentLayoutManagerType: HomeFragment.LayoutManagerType? = null
    protected var helper: Helper? = null

    private var database: DatabaseReference? = null

    private var currentUserId: String? = null
    private var userId: String? = null


    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            userId = bundle.getString("USER_ID")
            currentUserId = bundle.getString("CURRENT_USER_ID")
            Log.d(TAG, "BUNDLETEST " + currentUserId)
        }
        database = FirebaseDatabase.getInstance().reference

        threads = ArrayList()
        threadsToRemove = ArrayList()

        val database = FirebaseDatabase.getInstance()
        val databaseReference = database.reference
        databaseReference.child("threads").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                threads.clear()
                val children = dataSnapshot.children

                for (child in children) {
                    val thread = child.getValue(Thread::class.java)
                    if (thread != null) {
                        if (thread.userId == userId) {
                            thread.id = child.key
                            threads.add(0, thread)
                        }
                    }

                }
                threadAdapter?.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_profile_videos, container, false)

        layoutManager = LinearLayoutManager(activity)
        currentLayoutManagerType = HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            currentLayoutManagerType = savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER) as HomeFragment.LayoutManagerType
        }
        val context = context
        val activity = activity
        val layoutManager = layoutManager
        val currentLayoutManagerType = currentLayoutManagerType
        val database = database

        if (context != null && activity != null && database != null && layoutManager != null && currentLayoutManagerType != null) {
            threadAdapter = ThreadAdapter(context, threads, database)
            profile_videos_rv.adapter = threadAdapter

            //        if (HomeActivity.user.getEmail().equals("jakubpchmiel@gmail.com")) {
            //            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            //            itemTouchHelper.attachToRecyclerView(recyclerView);
            //        }
            helper?.setRecyclerViewLayoutManager(HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER, profile_videos_rv,
                    activity, layoutManager, currentLayoutManagerType)
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
        private val TAG = "ProfileVideosFragment"

        fun newInstance(param1: String, param2: String): ProfileVideosFragment {
            val fragment = ProfileVideosFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
