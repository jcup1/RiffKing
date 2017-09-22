package com.theandroiddev.riffking;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ThreadFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ThreadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThreadFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "threadId";
    private static final String TAG = "ThreadFragment";
    TextView position_tv;
    YouTubePlayerSupportFragment mYoutubePlayerFragment;
    TextView threadTitleTv, threadContentTv, threadLikesNumberTv, threadTodo;
    ImageView threadLikeIv;
    private DatabaseReference database;
    // TODO: Rename and change types of parameters
    private String threadId;
    private OnFragmentInteractionListener mListener;
    private String getThreadURL = "http://theandroiddev.com/get_thread.php";
    private Thread thread;
    //TODO IMPLEMENT WAS RESTORED
    private boolean wasRestored = false;
    private int dummyLikesNumber = 0;

    public ThreadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThreadFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        database = FirebaseDatabase.getInstance().getReference();
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
                    thread.setLikes(thread.getLikes() + 1);
                    threadLikesNumberTv.setText(String.valueOf(thread.getLikes()));
                    //((HomeActivity)getActivity()).addLike(thread.getId());
                    if (thread.getLikes() >= 2) {
                        Snackbar snackbar = Snackbar.make(getView(), "It doesn't work well. Punch developer", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Punch hard", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(getContext(), "Hard Punch Failed. Your Browser History Shared", Toast.LENGTH_LONG).show();
                                    }
                                });
                        snackbar.show();
                    }

                }
            });

            //Toast.makeText(getContext(), threadId, Toast.LENGTH_SHORT).show();

            mYoutubePlayerFragment = new YouTubePlayerSupportFragment();
            mYoutubePlayerFragment.initialize(PlayerConfig.API_KEY, this);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_youtube_player, mYoutubePlayerFragment);
            fragmentTransaction.commit();

            //mYoutubeVideoTitle = (TextView)fragmentThreadView.findViewById(R.id.fragment_youtube_title);
            //mYoutubeVideoDescription = (TextView)fragmentThreadView.findViewById(R.id.fragment_youtube_description);

            //mYoutubeVideoTitle.setText(thread.getTitle());
            //mYoutubeVideoDescription.setText(thread.getContent());

            //VideoFragment.setTextToShare(thread.getURL());


            return fragmentThreadView;


//            YouTubePlayerSupportFragment youTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
//            youTubePlayerSupportFragment.initialize(PlayerConfig.API_KEY, new YouTubePlayer.OnInitializedListener() {
//                @Override
//                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                    if(!b) {
//                        yPlayer = youTubePlayer;
//                        yPlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
//                        yPlayer.loadVideo(thread.getURL());
//                        yPlayer.play();
//                    }
//                }
//
//                @Override
//                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//                    String errorMsg = youTubeInitializationResult.toString();
//                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
//                    Log.d(TAG, "onInitializationFailure: " + errorMsg);
//                }
//            });



        }

        return inflater.inflate(com.theandroiddev.riffking.R.layout.fragment_thread, container, false);
    }


    private void loadThread() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("threads").child(threadId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                thread = dataSnapshot.getValue(Thread.class);

                if (thread.getTitle() == null)
                    thread.setTitle("TITLE WAS WRONG");
                threadTitleTv.setText(thread.getTitle());
                threadContentTv.setText(thread.getContent());
                threadLikesNumberTv.setText(String.valueOf(thread.getLikes()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//
//        position_tv = (TextView) getView().findViewById(R.id.position_tv);
//        position_tv.setText(position);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
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
