package com.theandroiddev.riffking;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.theandroiddev.riffking.HomeFragment.KEY_LAYOUT_MANAGER;

public class ThreadFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
    private static final String TAG = "ThreadFragment";

    private static final String ARG_PARAM1 = "threadId";
    static boolean liked;
    //TODO 29-09 15:20 CLEAN IT
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected CommentAdapter mCommentAdapter;
    protected List<Comment> comments;
    protected HomeFragment.LayoutManagerType mCurrentLayoutManagerType;
    protected Helper helper;
    YouTubePlayerSupportFragment mYoutubePlayerFragment;
    TextView threadTitleTv, threadContentTv, threadLikesNumberTv, threadViewsNumberTv, threadUserTv;
    CustomEditText threadCommentEt;
    ImageView threadLikeIv, threadCommentSendIv;
    CircularImageView threadUserIv;
    //DATABASE
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    User user;
    FrameLayout youtubeFrameLayout;
    private String threadId;
    private OnFragmentInteractionListener mListener;
    private Thread thread;



    public ThreadFragment() {
    }

    public static ThreadFragment newInstance(String param1, String param2) {
        ThreadFragment fragment = new ThreadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static void expand(final View v) {
        v.measure(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? FrameLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        comments = new ArrayList<>();

        threadId = getArguments().getString(ARG_PARAM1);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = database.getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                thread = dataSnapshot.child("threads").child(threadId).getValue(Thread.class);
                if (thread != null) {
                    user = dataSnapshot.child("users").child(thread.getUserId()).getValue(User.class);
                    if (user != null) {
                        threadUserTv.setText(user.getName());
                        Picasso.with(getContext()).load(user.getPhotoUrl()).into(threadUserIv);

                        if (getContext() != null) {
                            if (dataSnapshot.child("threadLikes").child(getCurrentUserId()).child(getThreadId()).getValue(Boolean.class) != null) {
                                Log.d(TAG, "onDataChange: " + "already liked");
                                liked = true;
                                helper.setLiked(threadLikeIv);

                            } else {
                                Log.d(TAG, "onDataChange: not liked!");
                                liked = false;
                                helper.setUnliked(threadLikeIv);


                            }

                        }
                    }
                }

                //comments.clear();
                Iterable<DataSnapshot> children = dataSnapshot.child("comments").child(threadId).getChildren();

                for (DataSnapshot child : children) {
                    Comment comment = child.getValue(Comment.class);
                    comment.setId(child.getKey());
                    comments.add(0, comment);

                }
                Log.d(TAG, "onDataChange1: " + comments.toString());
                mCommentAdapter.notifyDataSetChanged();
                Log.e(TAG, "onDataChange:Comments refreshed! ");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        loadThread();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentThreadView = inflater.inflate(R.layout.fragment_thread, container, false);


        if (getArguments() != null) {

            mRecyclerView = (RecyclerView) fragmentThreadView.findViewById(R.id.thread_comments_rv);
            mLayoutManager = new LinearLayoutManager(getActivity());


            mCurrentLayoutManagerType = HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
            if (savedInstanceState != null) {
                // Restore saved layout manager type.
                mCurrentLayoutManagerType = (HomeFragment.LayoutManagerType) savedInstanceState
                        .getSerializable(KEY_LAYOUT_MANAGER);
            }

            mCommentAdapter = new CommentAdapter(getContext(), comments, databaseReference, getCurrentUserId());
            Log.d(TAG, "onDataChange2: " + comments.toString());

            mRecyclerView.setNestedScrollingEnabled(false);
            mRecyclerView.setAdapter(mCommentAdapter);

            helper.setRecyclerViewLayoutManager(HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER, mRecyclerView,
                    getActivity(), mLayoutManager, mCurrentLayoutManagerType);

            threadTitleTv = (TextView) fragmentThreadView.findViewById(R.id.thread_title_tv);
            threadLikesNumberTv = (TextView) fragmentThreadView.findViewById(R.id.thread_likes_number_tv);
            threadViewsNumberTv = (TextView) fragmentThreadView.findViewById(R.id.thread_views_number_tv);
            threadContentTv = (TextView) fragmentThreadView.findViewById(R.id.thread_description);
            threadLikeIv = (ImageView) fragmentThreadView.findViewById(R.id.thread_like_iv);
            threadCommentSendIv = (ImageView) fragmentThreadView.findViewById(R.id.thread_comment_send_iv);
            threadCommentEt = (CustomEditText) fragmentThreadView.findViewById(R.id.thread_comment_et);
            threadUserTv = (TextView) fragmentThreadView.findViewById(R.id.thread_user_tv);
            threadUserIv = (CircularImageView) fragmentThreadView.findViewById(R.id.thread_user_iv);
            youtubeFrameLayout = (FrameLayout) fragmentThreadView.findViewById(R.id.fragment_youtube_player);


            threadUserTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openProfileFragment();
                }
            });

            threadUserIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openProfileFragment();
                }
            });

            threadLikeIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!thread.getUserId().equals(getCurrentUserId())) {
                    if (!liked) {
                        helper.transaction(databaseReference.child("threads").child(threadId).child("likes"), 1);
                        helper.transaction(databaseReference.child("users").child(thread.getUserId()).child("likes"), 1);
                        databaseReference.child("threadLikes").child(getCurrentUserId()).child(threadId).setValue(true);

                    } else {
                        helper.transaction(databaseReference.child("threads").child(threadId).child("likes"), -1);
                        helper.transaction(databaseReference.child("users").child(thread.getUserId()).child("likes"), -1);
                        databaseReference.child("threadLikes").child(getCurrentUserId()).child(threadId).removeValue();
                    }

                }
                }
            });

            threadCommentSendIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(threadCommentEt.getText().toString())) {
                        Comment comment = new Comment(threadId, getCurrentUserId(), threadCommentEt.getText().toString(), helper.getCurrentDate(), 0);
                        databaseReference.child("comments").child(getThreadId()).push().setValue(comment);
                        threadCommentEt.setText("");

                    }
                }
            });


            mYoutubePlayerFragment = new YouTubePlayerSupportFragment();
            mYoutubePlayerFragment.initialize(PlayerConfig.API_KEY, this);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_youtube_player, mYoutubePlayerFragment);
            fragmentTransaction.commit();

            helper.transaction(databaseReference.child("threads").child(threadId).child("views"), 1);

            return fragmentThreadView;

        }

        return inflater.inflate(com.theandroiddev.riffking.R.layout.fragment_thread, container, false);
    }

    private void openProfileFragment() {

        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", thread.getUserId());
        bundle.putString("CURRENT_USER_ID", getCurrentUserId()); //Two same only in this case
        profileFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = ((HomeActivity) getContext()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_home, profileFragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void loadThread() {

        databaseReference.child("threads").child(threadId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                thread = dataSnapshot.getValue(Thread.class);

                if (thread == null) {
                    threadTitleTv.setText("Something went wrong...");
                } else {
                    threadTitleTv.setText(thread.getTitle());
                    setContent();
                    threadLikesNumberTv.setText(String.valueOf(thread.getLikes()));
                    threadViewsNumberTv.setText(String.valueOf(thread.getViews()));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setContent() {

        threadContentTv.setText(thread.getDescription());
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onPause() {

        mYoutubePlayerFragment.onPause();

        //FragmentManager fm = getFragmentManager();

        //fm.beginTransaction().remove(mYoutubePlayerFragment).commit();

        databaseReference.child("threads").child(threadId).setValue(thread);


        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();

        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.fab.setVisibility(View.GONE);



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            helper = new Helper(context);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        //mYoutubePlayerFragment.onDetach();
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                youTubePlayer.setFullscreen(true);
            } else youTubePlayer.setFullscreen(false);

            //cue instead of load to stop auto-play

            youTubePlayer.loadVideo(thread.getYoutubeId());
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

        }
    }


    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this.getActivity(), 1).show();
        } else {
            Toast.makeText(this.getActivity(),
                    "YouTubePlayer.onInitializationFailure(): " + youTubeInitializationResult.toString(),
                    Toast.LENGTH_LONG).show();
        }

    }


    public String getCurrentUserId() {
        Log.d(TAG, "TESTTTT: " + firebaseAuth.getCurrentUser().getUid());
        return firebaseAuth.getCurrentUser().getUid();
    }

    public String getThreadId() {
        //TODO MAKE STH WITH THIS THREADID VAR
        return threadId;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
