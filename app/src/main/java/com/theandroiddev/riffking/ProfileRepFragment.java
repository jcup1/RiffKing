package com.theandroiddev.riffking;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileRepFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileRepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileRepFragment extends Fragment {
    private static final String TAG = "ProfileRepFragment";

    protected EditText profileRepTitleEt, profileRepEt;
    protected ImageView profileRepMinusIv, profileRepPlusIv;
    protected Button profileRepTransferBtn;
    protected int number = 50;

    protected DatabaseReference mDatabase;
    protected Helper helper;
    private String currentUserId;
    private String userId;

    private OnFragmentInteractionListener mListener;

    public ProfileRepFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileRepFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileRepFragment newInstance(String param1, String param2) {
        ProfileRepFragment fragment = new ProfileRepFragment();

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

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_rep, container, false);

        profileRepTitleEt = (EditText) rootView.findViewById(R.id.profile_rep_title_et);
        profileRepEt = (EditText) rootView.findViewById(R.id.profile_rep_et);
        profileRepMinusIv = (ImageView) rootView.findViewById(R.id.profile_rep_minus_iv);
        profileRepPlusIv = (ImageView) rootView.findViewById(R.id.profile_rep_plus_iv);
        profileRepTransferBtn = (Button) rootView.findViewById(R.id.profile_rep_transfer_btn);

        profileRepEt.setText(String.valueOf(number));


        profileRepMinusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = Integer.valueOf(profileRepEt.getText().toString());
                number -= 5;
                profileRepEt.setText(String.valueOf(number));
            }
        });

        profileRepPlusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = Integer.valueOf(profileRepEt.getText().toString());
                number += 5;
                profileRepEt.setText(String.valueOf(number));
            }
        });

        profileRepTransferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO LETS END IT...
                if (!TextUtils.isEmpty(profileRepEt.getText().toString()) && !TextUtils.isEmpty(profileRepTitleEt.getText().toString())) {
                    Log.d(TAG, "transferRep: " + currentUserId + " to " + userId);
                    transferRep();
                }
            }
        });

        return rootView;
    }

    private void transferRep() {
        //TODO handle reping yourself
        if (!userId.equals(currentUserId)) {

            Rep rep = new Rep(number, currentUserId, helper.getCurrentDate(), profileRepTitleEt.getText().toString());
            mDatabase.child("reps").child(userId).push().setValue(rep);

            helper.transaction(mDatabase.child("users").child(currentUserId).child("reps"), -number);
            helper.transaction(mDatabase.child("users").child(userId).child("reps"), number);
            profileRepTitleEt.setText("");
        }
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
