package com.theandroiddev.riffking;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USER_ID = "userId";
    private static final String CURRENT_USER_ID = "currentUserId";
    private static final String ARG_PARAM2 = "currentUserId";
    CircularImageView profileImg, profileMoneyImg, profileFollowersImg, profileMessagesImg;
    ImageView profilePmImg;
    TextView profileNameTv, profileDescTv, profileEmailTv, profileMoneyTv, profileFollowersTv, profileLikesTv;
    Button profileFollowBtn;
    User user;
    DatabaseReference mDatabase;
    Helper helper;
    Boolean followed = false;
    // TODO: Rename and change types of parameters
    private String userId;
    private String currentUserId;
    private FragmentTabHost tabHost;
    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String userId, String currentUserId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, userId);
        args.putString(CURRENT_USER_ID, currentUserId);
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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        helper = new Helper();
        initUser();


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        profileImg = (CircularImageView) getView().findViewById(R.id.profile_img);
        profileNameTv = (TextView) getView().findViewById(R.id.profile_name_tv);
        profileDescTv = (TextView) getView().findViewById(R.id.profile_desc_tv);
        profileEmailTv = (TextView) getView().findViewById(R.id.profile_email_tv);
        profileMoneyTv = (TextView) getView().findViewById(R.id.profile_money_tv);
        profileFollowersTv = (TextView) getView().findViewById(R.id.profile_followers_tv);
        profileLikesTv = (TextView) getView().findViewById(R.id.profile_likes_tv);
        profileFollowBtn = (Button) getView().findViewById(R.id.profile_follow_btn);
        profilePmImg = (ImageView) getView().findViewById(R.id.profile_pm_img);


        //getUser(String.valueOf(SharedPrefManager.getInstance(getContext()).getId()));

    }

    private void initUser() {

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.child("users").child(userId).getValue(User.class);
                if (currentUserId.equals(userId)) {
                    //so you can't follow yourself
                    profileFollowBtn.setVisibility(View.GONE);
                }

                if (dataSnapshot.child("userFollowers").child(currentUserId).child(userId).getValue(Boolean.class) != null) {
                    Log.d(TAG, "onDataChange: already followed!");
                    followed = true;

                } else {
                    Log.d(TAG, "onDataChange: not followed!");
                    followed = false;
                }
                initData(user);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.fab.setVisibility(View.GONE);
        if (profileImg != null && user != null) {
            initUser();
            initData(user);

        }

    }

    private void initData(User user) {

        profileNameTv.setText(user.getName());
        profileDescTv.setText(user.getDetails());
        profileEmailTv.setText(user.getEmail());
        profileMoneyTv.setText(String.valueOf(user.getReps()));
        profileFollowersTv.setText(String.valueOf(user.getFollowers()));
        profileLikesTv.setText(String.valueOf(user.getLikes()));

        if (followed) {
            profileFollowBtn.setText("Unfollow");
            profileFollowBtn.setTextColor(Color.BLUE);
        } else {
            profileFollowBtn.setText("Follow");
            profileFollowBtn.setTextColor(Color.BLACK);
        }

        profileFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followed) {
                    mDatabase.child("userFollowers").child(currentUserId).child(userId).removeValue();

                    helper.transacton(mDatabase.child("users").child(userId).child("followers"), -1);
                    profileFollowBtn.setText("Unfollow");
                    profileFollowBtn.setTextColor(Color.BLUE);

                } else {
                    mDatabase.child("userFollowers").child(currentUserId).child(userId).setValue(true);

                    helper.transacton(mDatabase.child("users").child(userId).child("followers"), 1);
                    profileFollowBtn.setText("Follow");
                    profileFollowBtn.setTextColor(Color.BLACK);
                }
            }
        });

        profilePmImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO implement message fragments
                Toast.makeText(getContext(), "Not Ready Yet :(", Toast.LENGTH_SHORT).show();
            }
        });
        Picasso.with(getContext()).load(user.getPhotoUrl()).into(profileImg);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);


        tabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        tabHost.setup(getActivity(), getChildFragmentManager(), R.id.tab_container);

        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", userId);
        bundle.putString("CURRENT_USER_ID", currentUserId);

        tabHost.addTab(tabHost.newTabSpec("profilecommentsfragment").setIndicator("Comments"),
                ProfileCommentsFragment.class, bundle);
        tabHost.addTab(tabHost.newTabSpec("profilevideosfragment").setIndicator("Videos"),
                ProfileVideosFragment.class, bundle);
        if (currentUserId.equals(userId)) {
            tabHost.addTab(tabHost.newTabSpec("profilerepfragment").setIndicator("Rep"),
                    ProfileRepFragmentMe.class, bundle);
        } else {
            tabHost.addTab(tabHost.newTabSpec("profilerepfragment").setIndicator("Rep"),
                    ProfileRepFragment.class, bundle);
        }


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

    public String getUserId() {
        return userId;
    }

    public String getCurrentUserId() {
        return currentUserId;
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
