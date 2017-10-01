package com.theandroiddev.riffking;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.theandroiddev.riffking.HomeFragment.KEY_LAYOUT_MANAGER;


public class ProfileVideosFragment extends Fragment {
    private static final String TAG = "ProfileVideosFragment";

    protected RecyclerView mRecyclerView;
    protected ThreadAdapter mThreadAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Thread> threads;
    protected ArrayList<Thread> threadsToRemove;
    protected HomeFragment.LayoutManagerType mCurrentLayoutManagerType;
    protected Helper helper;

    private DatabaseReference mDatabase;

    private String currentUserId;
    private String userId;


    private OnFragmentInteractionListener mListener;

    public ProfileVideosFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileVideosFragment newInstance(String param1, String param2) {
        ProfileVideosFragment fragment = new ProfileVideosFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userId = bundle.getString("USER_ID");
            currentUserId = bundle.getString("CURRENT_USER_ID");
            Log.d(TAG, "BUNDLETEST " + currentUserId);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();

        threads = new ArrayList<>();
        threadsToRemove = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("threads").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                threads.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children) {
                    Thread thread = child.getValue(Thread.class);
                    if (thread.getUserId().equals(userId)) {
                        thread.setId(child.getKey());
                        threads.add(0, thread);
                    }

                }
                mThreadAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View rootView = inflater.inflate(R.layout.fragment_profile_videos, container, false);


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.profile_videos_rv);


        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (HomeFragment.LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        mThreadAdapter = new ThreadAdapter(getContext(), threads, mDatabase);
        mRecyclerView.setAdapter(mThreadAdapter);

//        if (HomeActivity.user.getEmail().equals("jakubpchmiel@gmail.com")) {
//            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//            itemTouchHelper.attachToRecyclerView(mRecyclerView);
//        }

        //TODO check why set two times
        helper.setRecyclerViewLayoutManager(HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER, mRecyclerView,
                getActivity(), mLayoutManager, mCurrentLayoutManagerType);
        return rootView;
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
            helper = new Helper(context);
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
