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
import java.util.List;

import static com.theandroiddev.riffking.HomeFragment.KEY_LAYOUT_MANAGER;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileRepFragmentMe.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileRepFragmentMe#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileRepFragmentMe extends Fragment {
    private static final String TAG = "ProfileRepFragmentMe";
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RepAdapter mRepAdapter;
    protected List<Rep> reps;
    protected HomeFragment.LayoutManagerType mCurrentLayoutManagerType;
    DatabaseReference mDatabase;
    Helper helper;
    private RecyclerView mRecyclerView;
    private String currentUserId;
    private String userId;
    private OnFragmentInteractionListener mListener;
    private int SPAN_COUNT = 2;

    public ProfileRepFragmentMe() {
        // Required empty public constructor
    }


    public static ProfileRepFragmentMe newInstance(String param1, String param2) {
        ProfileRepFragmentMe fragment = new ProfileRepFragmentMe();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            currentUserId = bundle.getString("CURRENT_USER_ID");
        }

        reps = new ArrayList<>();

        helper = new Helper();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "REPTEST: " + currentUserId);


                reps.clear();
                Iterable<DataSnapshot> children = dataSnapshot.child("reps").child(currentUserId).getChildren();

                for (DataSnapshot child : children) {
                    Rep rep = child.getValue(Rep.class);
                    reps.add(0, rep);
                    Log.d(TAG, "REPTEST: " + rep);

                }
                mRepAdapter.notifyDataSetChanged();

                // dataSnapshot.child("comments")
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
        View rootView = inflater.inflate(R.layout.fragment_profile_rep_me, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.profile_rep_me_rv);
        mLayoutManager = new LinearLayoutManager(getActivity());


        mCurrentLayoutManagerType = HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (HomeFragment.LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }

        mRepAdapter = new RepAdapter(getContext(), reps, mDatabase, currentUserId);

        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setAdapter(mRepAdapter);

        helper.setRecyclerViewLayoutManager(HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER, mRecyclerView,
                getActivity(), mLayoutManager, mCurrentLayoutManagerType);

        return rootView;
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
