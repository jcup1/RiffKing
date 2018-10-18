package com.theandroiddev.riffking.presentation.home

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.*
import com.theandroiddev.riffking.Helper
import com.theandroiddev.riffking.R
import com.theandroiddev.riffking.Utility
import com.theandroiddev.riffking.presentation.thread.Thread
import com.theandroiddev.riffking.presentation.thread.ThreadAdapter
import com.theandroiddev.riffking.presentation.upload.UploadFragment
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

class HomeFragment : Fragment() {

    private var threadAdapter: ThreadAdapter? = null
    private var layoutManager = LinearLayoutManager(activity)
    private var currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER
    private var threads: ArrayList<Thread> = arrayListOf()
    private var threadsToRemove: ArrayList<Thread> = arrayListOf()
    var helper: Helper? = null

    init {
        val context = context
        if (context != null) {
            helper = Helper(context)
        }
    }

    private var simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            val pos = viewHolder.adapterPosition
            toRemove(pos)

        }
    }

    private var database: DatabaseReference? = null
    private var listener: OnFragmentInteractionListener? = null

    private fun toRemove(position: Int) {
        showUndoSnackbar(position)
    }

    private fun showUndoSnackbar(position: Int) {
        val thread = threads[position]
        val snackbar = Snackbar
                .make(recyclerView, "REMOVED", Snackbar.LENGTH_LONG)
                .setAction("UNDO") { undoToRemove(thread, position) }
        snackbar.show()

        sendToRemoveQueue(thread, position)
    }

    private fun sendToRemoveQueue(thread: Thread, position: Int) {
        threads.removeAt(position)
        threadAdapter?.notifyItemRemoved(position)
        threadsToRemove.add(thread)
    }

    private fun undoToRemove(thread: Thread, position: Int) {
        threads.add(position, thread)
        threadAdapter?.notifyItemInserted(position)
        recyclerView.scrollToPosition(position)
        threadsToRemove.remove(thread)
    }

    fun newInstance(param1: String, param2: String, param3: String, param4: String): HomeFragment {
        val fragment = HomeFragment()
        val args = Bundle()

        fragment.arguments = args

        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance().reference

        val database = FirebaseDatabase.getInstance()
        val databaseReference = database.reference
        databaseReference.child("threads").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                threads.clear()
                val children = dataSnapshot.children

                for (child in children) {
                    val thread = child.getValue(Thread::class.java)
                    thread?.id = child.key
                    if (thread != null) {
                        threads.add(0, thread)
                    }
                }
                threadAdapter?.notifyDataSetChanged()
                Log.e(TAG, "onDataChange:QWE ")

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    override fun onPause() {
        super.onPause()
        remove()
    }

    private fun remove() {

        while (threadsToRemove.size > 0) {
            Log.d(TAG, "remove: " + threadsToRemove + " " + threadsToRemove.size)

            val threadToRemoveId = threadsToRemove[0].id
            if (threadToRemoveId != null) {
                database?.child("threads")?.child(threadToRemoveId)?.removeValue()
                threadsToRemove.removeAt(0)
            } else {
                //TODO error
            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        rootView.tag = TAG

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            currentLayoutManagerType = savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER) as LayoutManagerType
        }
        val context = context
        val database = database
        val activity = activity
        if (context != null && database != null && activity != null) {
            threadAdapter = ThreadAdapter(context, threads, database)
            recyclerView.adapter = threadAdapter

            recyclerView.setHasFixedSize(true)
            recyclerView.setItemViewCacheSize(20)
            recyclerView.isDrawingCacheEnabled = true
            recyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

            if (HomeActivity.user?.email.equals("jakubpchmiel@gmail.com")) {
                val itemTouchHelper = ItemTouchHelper(simpleCallback)
                itemTouchHelper.attachToRecyclerView(recyclerView)
            }

            helper?.setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER,
                    recyclerView, activity, layoutManager, currentLayoutManagerType)

            threadAdapter?.notifyDataSetChanged()
        }
        return rootView
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, currentLayoutManagerType)
        super.onSaveInstanceState(savedInstanceState)
    }

    private fun userIsConnected(): Boolean {
        val context = context
        return if(context != null) {
            Utility.isNetworkAvailable(context)
        } else false
        //        String textt = "";
        //
        //        if(firebaseAuth.isC() != null) {
        //            textt = firebaseAuth.getCurrentUser().getEmail();
        //        }
        //
        //        Toast.makeText(getContext(),textt , Toast.LENGTH_SHORT).show();

    }

    override fun onResume() {
        super.onResume()

        if (!userIsConnected()) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }

        //threadAdapter?.notifyDataSetChanged();

        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        //        getView().setOnKeyListener(new View.OnKeyListener() {
        //            @Override
        //            public boolean onKey(View v, int keyCode, KeyEvent event) {
        //
        //                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
        //
        //                    getActivity().finish();
        //
        //                    return true;
        //                }
        //
        //
        //                return false;
        //            }
        //        });

        //TODO shoudnt it be in onCreateView?

        val homeActivity = activity as HomeActivity?
        homeActivity?.fab?.visibility = View.VISIBLE
        homeActivity?.fab?.setImageResource(R.drawable.ic_add_24dp)
        homeActivity?.fab?.setOnClickListener { insertThread() }
    }

    private fun insertThread() {

        val uploadFragment = UploadFragment()

        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.content_home, uploadFragment).addToBackStack(null)
        transaction.commit()

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser && isResumed) {
            onResume()
        }
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

    enum class LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
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
        val KEY_LAYOUT_MANAGER = "layoutManager"
        private val TAG = "HomeFragment"
    }
}// Required empty public constructor
