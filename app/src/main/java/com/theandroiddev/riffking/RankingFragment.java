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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.theandroiddev.riffking.HomeFragment.KEY_LAYOUT_MANAGER;

public class RankingFragment extends Fragment implements HomeFragment.OnFragmentInteractionListener {
    private static final String TAG = "RankingFragment";


    protected RecyclerView mRecyclerView;
    protected RankingAdapter mRankingAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<User> users;
    protected HomeFragment.LayoutManagerType mCurrentLayoutManagerType;

    private DatabaseReference mDatabase;
    private Helper helper;

    private String currentUserId;
    private String userId;
    private OnFragmentInteractionListener mListener;


    public RankingFragment() {
        // Required empty public constructor
    }

    public static RankingFragment newInstance(String param1, String param2) {
        RankingFragment fragment = new RankingFragment();
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
        }

        helper = new Helper();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        users = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children) {
                    User user = child.getValue(User.class);
                    user.setId(child.getKey());
                    users.add(0, user);
                    Log.d(TAG, "USERSTEST" + users.toString());


                }

                mRankingAdapter.notifyDataSetChanged();

                Collections.sort(users, new Comparator<User>() {
                    @Override
                    public int compare(User o1, User o2) {
                        return Integer.valueOf(o2.getReps()).compareTo(o1.getReps());
                    }
                });

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
        View rootView = inflater.inflate(R.layout.fragment_ranking, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.ranking_rv);

        Log.d(TAG, "USERSTEST" + users.toString());
        mRankingAdapter = new RankingAdapter(getContext(), users, mDatabase, currentUserId);
        mRecyclerView.setAdapter(mRankingAdapter);
        mRankingAdapter.notifyDataSetChanged();

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (HomeFragment.LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }


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
    public void onFragmentInteraction(Uri uri) {

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
