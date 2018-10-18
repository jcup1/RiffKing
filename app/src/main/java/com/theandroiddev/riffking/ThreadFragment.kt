package com.theandroiddev.riffking

import android.content.Context
import android.content.res.Configuration
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
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.theandroiddev.riffking.HomeFragment.Companion.KEY_LAYOUT_MANAGER
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_thread.*
import java.util.*


class ThreadFragment : Fragment(), YouTubePlayer.OnInitializedListener {
    //TODO 29-09 15:20 CLEAN IT
    protected var layoutManager: RecyclerView.LayoutManager? = null
    protected var commentAdapter: CommentAdapter? = null
    protected var comments: MutableList<Comment> = mutableListOf()
    protected var currentLayoutManagerType: HomeFragment.LayoutManagerType? = null
    protected var helper: Helper? = null
    internal var youtubePlayerFragment: YouTubePlayerSupportFragment? = null
    //DATABASE
    internal var database: FirebaseDatabase? = null
    internal var databaseReference: DatabaseReference? = null
    internal var firebaseAuth: FirebaseAuth? = null
    internal var user: User? = null
    //TODO MAKE STH WITH THIS THREADID VAR
    var threadId: String? = null

    private var listener: OnFragmentInteractionListener? = null
    private var thread: Thread? = null

    val currentUserId: String?
        get() {
            Log.d(TAG, "TESTTTT: " + firebaseAuth?.currentUser?.uid)
            return firebaseAuth?.currentUser?.uid
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        comments = ArrayList()

        threadId = arguments?.getString(ARG_PARAM1)
        database = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = database?.reference

        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val threadId = threadId
                val threadUserId = thread?.userId
                val currentUserId = currentUserId

                if (threadId != null && currentUserId != null && threadUserId != null) {
                    thread = dataSnapshot.child("threads").child(threadId).getValue(Thread::class.java)
                    if (thread != null) {
                        user = dataSnapshot.child("users").child(threadUserId).getValue(User::class.java)
                        if (user != null) {
                            thread_user_tv.text = user?.name
                            Picasso.get().load(user?.photoUrl).into(thread_user_iv)

                            if (context != null) {
                                if (dataSnapshot.child("threadLikes").child(currentUserId).child(threadId)
                                                .getValue(Boolean::class.java) != null) {
                                    Log.d(TAG, "onDataChange: " + "already liked")
                                    liked = true
                                    helper?.setLiked(thread_like_iv)

                                } else {
                                    Log.d(TAG, "onDataChange: not liked!")
                                    liked = false
                                    helper?.setUnliked(thread_like_iv)

                                }

                            }
                        }
                    }

                    comments.clear()
                    val children = dataSnapshot.child("comments").child(threadId).children

                    for (child in children) {
                        val comment = child.getValue(Comment::class.java)
                        comment?.id = child.key
                        if (comment != null) {
                            comments.add(0, comment)
                        }
                    }
                    Log.d(TAG, "onDataChange1: " + comments.toString())
                    commentAdapter?.notifyDataSetChanged()
                    Log.e(TAG, "onDataChange:Comments refreshed! ")

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        loadThread()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val fragmentThreadView = inflater.inflate(R.layout.fragment_thread, container, false)

        if (arguments != null) {

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
            val databaseReference = databaseReference
            val currentUserId = currentUserId

            if (context != null && activity != null && layoutManager != null && currentUserId != null
                    && currentLayoutManagerType != null && databaseReference != null) {
                commentAdapter = CommentAdapter(context, comments, databaseReference, currentUserId)
                Log.d(TAG, "onDataChange2: " + comments.toString())

                recyclerView.isNestedScrollingEnabled = false
                recyclerView.adapter = commentAdapter

                helper?.setRecyclerViewLayoutManager(HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER, recyclerView,
                        activity, layoutManager, currentLayoutManagerType)
            }
            thread_user_tv.setOnClickListener { openProfileFragment() }

            thread_user_iv.setOnClickListener { openProfileFragment() }

            thread_like_iv.setOnClickListener {

                val threadId = threadId
                val threadUserId = thread?.userId

                if (threadUserId != currentUserId && threadUserId != null && threadId != null) {
                    val threadLikes = databaseReference?.child("threads")?.child(threadId)?.child("likes")
                    val userLikes = databaseReference?.child("users")?.child(threadUserId)?.child("likes")
                    if (threadLikes != null && userLikes != null && currentUserId != null) {

                        if (!liked) {

                            helper?.transaction(threadLikes, 1)
                            helper?.transaction(userLikes, 1)
                            databaseReference.child("threadLikes").child(currentUserId).child(threadId).setValue(true)

                        } else {
                            helper?.transaction(threadLikes, -1)
                            helper?.transaction(userLikes, -1)
                            databaseReference.child("threadLikes").child(currentUserId).child(threadId).removeValue()
                        }
                    }

                }
            }

            thread_comment_send_iv.setOnClickListener {
                if (!TextUtils.isEmpty(thread_comment_et.text.toString())) {
                    val threadId = threadId
                    if (threadId != null && currentUserId != null) {
                        val comment = Comment(threadId, currentUserId, thread_comment_et.text.toString(),
                                helper?.currentDate ?: "N/A", 0)
                        databaseReference?.child("comments")?.child(threadId)?.push()?.setValue(comment)
                        thread_comment_et.setText("")
                    }

                }
            }


            youtubePlayerFragment = YouTubePlayerSupportFragment()
            youtubePlayerFragment?.initialize(PlayerConfig.API_KEY, this)
            val fragmentManager = fragmentManager
            val fragmentTransaction = fragmentManager?.beginTransaction()

            val youtubePlayerFragment = youtubePlayerFragment
            val threadId = threadId
            if (youtubePlayerFragment != null && threadId != null) {
                fragmentTransaction?.replace(R.id.fragment_youtube_player, youtubePlayerFragment)
                fragmentTransaction?.commit()

                val threadViews = databaseReference?.child("threads")?.child(threadId)?.child("views")
                if (threadViews != null) {
                    helper?.transaction(threadViews, 1)
                } else {
                    //TODO error
                }
            }
            return fragmentThreadView

        }

        return inflater.inflate(com.theandroiddev.riffking.R.layout.fragment_thread, container, false)
    }

    private fun openProfileFragment() {

        val profileFragment = ProfileFragment()
        val bundle = Bundle()
        bundle.putString("USER_ID", thread?.userId)
        bundle.putString("CURRENT_USER_ID", currentUserId) //Two same only in this case
        profileFragment.arguments = bundle

        val fragmentTransaction = (context as HomeActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_home, profileFragment).addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun loadThread() {

        val threadId = threadId
        if (threadId != null) {
            databaseReference?.child("threads")?.child(threadId)?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    thread = dataSnapshot.getValue(Thread::class.java)

                    if (thread == null) {
                        thread_title_tv.text = "Something went wrong..."
                    } else {
                        thread_title_tv.text = thread?.title
                        setContent()
                        thread_likes_number_tv.text = thread?.likes.toString()
                        thread_views_number_tv.text = thread?.views.toString()
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }

    }

    private fun setContent() {

        thread_description.text = thread?.description
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (listener != null) {
            listener?.onFragmentInteraction(uri)
        }
    }

    override fun onPause() {

        youtubePlayerFragment?.onPause()

        //FragmentManager fm = getFragmentManager();

        //fm.beginTransaction().remove(youtubePlayerFragment).commit();
        val threadId = threadId
        if (threadId != null) {
            databaseReference?.child("threads")?.child(threadId)?.setValue(thread)
        }

        super.onPause()
    }


    override fun onResume() {
        super.onResume()

        val homeActivity = activity as HomeActivity?
        homeActivity?.fab?.visibility = View.GONE


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
        //youtubePlayerFragment.onDetach();
        super.onDetach()
        listener = null
    }


    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, b: Boolean) {
        if (!b) {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                youTubePlayer.setFullscreen(true)
            } else
                youTubePlayer.setFullscreen(false)

            //cue instead of load to stop auto-play

            youTubePlayer.loadVideo(thread?.youtubeId)
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)

        }
    }


    override fun onInitializationFailure(provider: YouTubePlayer.Provider,
                                         youTubeInitializationResult: YouTubeInitializationResult) {

        if (youTubeInitializationResult.isUserRecoverableError) {
            youTubeInitializationResult.getErrorDialog(this.activity, 1).show()
        } else {
            Toast.makeText(this.activity,
                    "YouTubePlayer.onInitializationFailure(): " + youTubeInitializationResult.toString(),
                    Toast.LENGTH_LONG).show()
        }

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
        private val TAG = "ThreadFragment"

        private val ARG_PARAM1 = "threadId"
        internal var liked: Boolean = false

        fun newInstance(param1: String, param2: String): ThreadFragment {
            val fragment = ThreadFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }

        fun expand(v: View) {
            v.measure(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            val targetHeight = v.measuredHeight

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            v.layoutParams.height = 1
            v.visibility = View.VISIBLE
            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    v.layoutParams.height = if (interpolatedTime == 1f)
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    else
                        (targetHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            // 1dp/ms
            a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
            v.startAnimation(a)
        }

        fun collapse(v: View) {
            val initialHeight = v.measuredHeight

            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime == 1f) {
                        v.visibility = View.GONE
                    } else {
                        v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            // 1dp/ms
            a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
            v.startAnimation(a)
        }
    }
}
