package com.theandroiddev.riffking;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class ThreadFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
    private static final String TAG = "ThreadFragment";

    private static final String ARG_PARAM1 = "threadId";
    static boolean liked;
    YouTubePlayerSupportFragment mYoutubePlayerFragment;
    TextView threadTitleTv, threadContentTv, threadLikesNumberTv;
    ImageView threadLikeIv;
    //DATABASE
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    private String threadId;
    private OnFragmentInteractionListener mListener;
    private Thread thread;
    private boolean postLiked;

    public ThreadFragment() {
    }

    public static ThreadFragment newInstance(String param1, String param2) {
        ThreadFragment fragment = new ThreadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        threadId = getArguments().getString(ARG_PARAM1);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = database.getReference();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thread = dataSnapshot.child("threads").child(threadId).getValue(Thread.class);
                databaseReference.child("threads").child(threadId).setValue(thread);

                if (dataSnapshot.child("threadLikes").child(getCurrentUserId()).child(getThreadId()).getValue(Boolean.class) != null) {
                    Log.d(TAG, "onDataChange: " + "already liked");
                    liked = true;
                    threadLikeIv.setColorFilter(Color.BLUE);

                } else {
                    Log.d(TAG, "onDataChange: not liked!");
                    liked = false;
                    threadLikeIv.setColorFilter(Color.BLACK);

                }

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

            threadTitleTv = (TextView) fragmentThreadView.findViewById(R.id.thread_title_tv);
            threadLikesNumberTv = (TextView) fragmentThreadView.findViewById(R.id.thread_likes_number_tv);
            threadContentTv = (TextView) fragmentThreadView.findViewById(R.id.thread_description);
            threadLikeIv = (ImageView) fragmentThreadView.findViewById(R.id.thread_like_iv);

            threadLikeIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!liked) {
                        addLike(databaseReference.child("threads").child(threadId).child("likes"));
                        databaseReference.child("threadLikes").child(getCurrentUserId()).child(threadId).setValue(true);

                    } else {
                        removeLike(databaseReference.child("threads").child(threadId).child("likes"));
                        databaseReference.child("threadLikes").child(getCurrentUserId()).child(threadId).removeValue();
                    }

                }
            });

            mYoutubePlayerFragment = new YouTubePlayerSupportFragment();
            mYoutubePlayerFragment.initialize(PlayerConfig.API_KEY, this);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_youtube_player, mYoutubePlayerFragment);
            fragmentTransaction.commit();

            return fragmentThreadView;

        }

        return inflater.inflate(com.theandroiddev.riffking.R.layout.fragment_thread, container, false);
    }

    private void addLike(final DatabaseReference postRef) {
        //TODO ATTACH TO ONCLICK AND CHECK IS USER LOGGED IN...
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                int likes = mutableData.getValue(Integer.class);

                likes++;

                // Set value and report transaction success
                mutableData.setValue(likes);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void removeLike(final DatabaseReference postRef) {
        //TODO ATTACH TO ONCLICK AND CHECK IS USER LOGGED IN...
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                int likes = mutableData.getValue(Integer.class);

                likes--;

                mutableData.setValue(likes);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
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

        FragmentManager fm = getFragmentManager();

        fm.beginTransaction().remove(mYoutubePlayerFragment).commit();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_youtube_player, mYoutubePlayerFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mYoutubePlayerFragment.onDetach();
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            //cue instead of load to stop auto-play
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                youTubePlayer.setFullscreen(true);
            } else youTubePlayer.setFullscreen(false);
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
