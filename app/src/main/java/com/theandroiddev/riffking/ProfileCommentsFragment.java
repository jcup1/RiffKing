package com.theandroiddev.riffking;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileCommentsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileCommentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileCommentsFragment extends Fragment {
    private static final String TAG = "ProfileCommentsFragment";
    protected RecyclerView.LayoutManager mLayoutManager;
    protected CommentAdapter mCommentAdapter;
    protected List<Comment> comments;
    protected HomeFragment.LayoutManagerType mCurrentLayoutManagerType;
    TextView profileCommentsNumberTv;
    CircularImageView profileCommentsUserIv;
    EditText profileCommentsContentEt;
    RecyclerView mRecyclerView;
    ImageView profileCommentsSendIv;
    DatabaseReference mDatabase;
    Helper helper;

    User currentUser;

    private OnFragmentInteractionListener mListener;

    //TODO send and retrieve it
    private String currentUserId;
    private String userId;

    private User user;

    public ProfileCommentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileCommentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileCommentsFragment newInstance(String param1, String param2) {
        ProfileCommentsFragment fragment = new ProfileCommentsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = new User();
        currentUser = new User();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userId = bundle.getString("USER_ID");
            currentUserId = bundle.getString("CURRENT_USER_ID");
        }

        comments = new ArrayList<>();
        helper = new Helper();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user = dataSnapshot.child("users").child(userId).getValue(User.class);
                currentUser = dataSnapshot.child("users").child(currentUserId).getValue(User.class);

                profileCommentsNumberTv.setText(String.valueOf(user.getComments()));
                Picasso.with(getContext()).load(currentUser.getPhotoUrl()).into(profileCommentsUserIv);

                Log.d(TAG, "userrr: " + currentUser.toString());

                // TODO get data
                comments.clear();
                Iterable<DataSnapshot> children = dataSnapshot.child("comments").child(userId).getChildren();

                for (DataSnapshot child : children) {
                    Comment comment = child.getValue(Comment.class);
                    comment.setId(child.getKey());
                    comments.add(0, comment);

                }
                mCommentAdapter.notifyDataSetChanged();

                // dataSnapshot.child("comments")
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }

    @Override
    public void onResume() {
        super.onResume();
        profileCommentsNumberTv.setText(String.valueOf(user.getComments()));
        Picasso.with(getContext()).load(currentUser.getPhotoUrl()).into(profileCommentsUserIv);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile_comments, container, false);

        profileCommentsNumberTv = (TextView) rootView.findViewById(R.id.profile_comments_number_tv);
        profileCommentsUserIv = (CircularImageView) rootView.findViewById(R.id.profile_comments_user_iv);
        profileCommentsContentEt = (EditText) rootView.findViewById(R.id.profile_comments_content_et);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.profile_comments_rv);
        profileCommentsSendIv = (ImageView) rootView.findViewById(R.id.profile_comments_send_iv);

        mLayoutManager = new LinearLayoutManager(getActivity());


        mCurrentLayoutManagerType = HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (HomeFragment.LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        setRecyclerViewLayoutManager(HomeFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER);

        mCommentAdapter = new CommentAdapter(getContext(), comments, mDatabase, currentUserId);
        Log.d(TAG, "onDataChange2: " + comments.toString());

        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setAdapter(mCommentAdapter);

        //CLICKS

        profileCommentsSendIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(profileCommentsContentEt.getText().toString())) {

                    Comment comment = new Comment(userId, currentUserId, profileCommentsContentEt.getText().toString(), helper.getCurrentDate(), 0);
                    mDatabase.child("comments").child(userId).push().setValue(comment);
                    profileCommentsContentEt.setText("");
                    helper.transacton(mDatabase.child("users").child(userId).child("comments"), 1);

                }
            }
        });

        return rootView;
    }

    public void setRecyclerViewLayoutManager(HomeFragment.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), Helper.SPAN_COUNT);
                mCurrentLayoutManagerType = HomeFragment.LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:

                break;
            default:

        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
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
