package com.theandroiddev.riffking.presentation.profile

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theandroiddev.riffking.R
import com.theandroiddev.riffking.presentation.home.HomeFragment
import com.theandroiddev.riffking.presentation.home.HomeFragment.Companion.KEY_LAYOUT_MANAGER
import com.theandroiddev.riffking.utils.Helper
import kotlinx.android.synthetic.main.fragment_profile_rep_me.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileRepFragmentMe.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileRepFragmentMe.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileRepFragmentMe : Fragment() {
    protected var layoutManager: RecyclerView.LayoutManager? = null
    protected var repAdapter: RepAdapter? = null
    protected var reps: MutableList<Rep> = arrayListOf()
    protected var currentLayoutManagerType: HomeFragment.LayoutManagerType? = null
    internal var database = FirebaseDatabase.getInstance().reference
    internal var helper: Helper? = null
    private var currentUserId: String? = null
    private val userId: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private val SPAN_COUNT = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            currentUserId = bundle.getString("CURRENT_USER_ID")
        }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d(TAG, "REPTEST: " + currentUserId!!)

                reps.clear()
                val children = dataSnapshot.child("reps").child(currentUserId!!).children

                for (child in children) {
                    val rep = child.getValue(Rep::class.java)
                    if (rep != null) {
                        reps.add(0, rep)
                        Log.d(TAG, "REPTEST: " + rep)
                    }

                }
                repAdapter?.notifyDataSetChanged()

                // dataSnapshot.child("comments")
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }


        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_profile_rep_me, container, false)

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
        val currentUserId = currentUserId
        if (context != null && activity != null && layoutManager != null && currentLayoutManagerType != null && currentUserId != null) {
            repAdapter = RepAdapter(context, reps, database, currentUserId)

            profile_rep_me_rv.isNestedScrollingEnabled = true
            profile_rep_me_rv.adapter = repAdapter

            helper?.setRecyclerViewLayoutManager(HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER, profile_rep_me_rv,
                    activity, layoutManager, currentLayoutManagerType)
        }
        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
            helper = Helper(context)
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
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
        private val TAG = "ProfileRepFragmentMe"


        fun newInstance(param1: String, param2: String): ProfileRepFragmentMe {
            val fragment = ProfileRepFragmentMe()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
