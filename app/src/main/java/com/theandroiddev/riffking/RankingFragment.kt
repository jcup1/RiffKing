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
import kotlinx.android.synthetic.main.fragment_ranking.*
import java.util.ArrayList
import kotlin.Comparator
import kotlin.RuntimeException
import kotlin.String
import kotlin.toString


class RankingFragment : Fragment(), HomeFragment.OnFragmentInteractionListener {


    protected var rankingAdapter: RankingAdapter? = null
    protected var layoutManager: RecyclerView.LayoutManager? = null
    protected var users: MutableList<User> = mutableListOf()
    protected var currentLayoutManagerType: HomeFragment.LayoutManagerType? = null

    private var database: DatabaseReference? = null
    private var helper: Helper? = null

    private var currentUserId: String? = null
    private var userId: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            userId = bundle.getString("USER_ID")
            currentUserId = bundle.getString("CURRENT_USER_ID")
        }

        database = FirebaseDatabase.getInstance().reference

        users = ArrayList()

        val database = FirebaseDatabase.getInstance()
        val databaseReference = database.reference
        databaseReference.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users.clear()
                val children = dataSnapshot.children

                for (child in children) {
                    val user = child.getValue(User::class.java)
                    if (user != null) {
                        user.id = child.key
                        users.add(0, user)
                        Log.d(TAG, "USERSTEST" + users.toString())

                    }

                }

                users.sortWith(Comparator { o1, o2 -> Integer.valueOf(o2.reps).compareTo(o1.reps) })

                for (i in users.indices) {
                    users[i].ranking = i + 1
                }

                rankingAdapter?.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_ranking, container, false)

        Log.d(TAG, "USERSTEST" + users.toString())

        val context = context
        val database = database
        val currentUserId = currentUserId
        if (context != null && database != null && currentUserId != null) {

            rankingAdapter = RankingAdapter(context, users, database, currentUserId)
            ranking_rv.adapter = rankingAdapter
            rankingAdapter?.notifyDataSetChanged()

            layoutManager = LinearLayoutManager(activity)
            currentLayoutManagerType = HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER
            if (savedInstanceState != null) {
                // Restore saved layout manager type.
                currentLayoutManagerType = savedInstanceState
                        .getSerializable(KEY_LAYOUT_MANAGER) as HomeFragment.LayoutManagerType
            }

            val activity = activity
            val layoutManager = layoutManager
            val currentLayoutManagerType = currentLayoutManagerType

            if (activity != null && layoutManager != null && currentLayoutManagerType != null) {
                helper?.setRecyclerViewLayoutManager(HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER, ranking_rv,
                        activity, layoutManager, currentLayoutManagerType)

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

    override fun onFragmentInteraction(uri: Uri) {

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
        private val TAG = "RankingFragment"

        fun newInstance(param1: String, param2: String): RankingFragment {
            val fragment = RankingFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
