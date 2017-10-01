package com.theandroiddev.riffking;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.theandroiddev.riffking.HomeActivity.saveSharedPreferencesLogList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InsertThreadFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InsertThreadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InsertThreadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String URL_PARAM = "URL_PARAM";
    private static final String TAG = "InsertThreadFragment";
    EditText titleEt, urlEt, contentEt;
    String URL;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private Helper helper;
    private String urlLink;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private String currentDate;

    private List<Thread> threadsInQueue;

    public InsertThreadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InsertThreadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InsertThreadFragment newInstance(String param1, String param2) {
        InsertThreadFragment fragment = new InsertThreadFragment();
        Bundle args = new Bundle();
        args.putString(URL_PARAM, param1);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            urlLink = getArguments().getString("URL");

        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        helper = new Helper();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleEt = (EditText) getActivity().findViewById(R.id.title_et);
        urlEt = (EditText) getActivity().findViewById(R.id.URL_et);
        contentEt = (EditText) getActivity().findViewById(R.id.content_et);

        //TODO GET RID OF IT IF FAB WORKS

        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.fab.setImageResource(R.drawable.ic_check_24dp);
        homeActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userIsConnected()) {
                    insertThread();
                } else {
                    insertThreadToQueue();
                }

            }
        });

        urlEt.setText(urlLink);
        //Toast.makeText(getContext(), urlLink, Toast.LENGTH_SHORT).show();

    }

    private void insertThreadToQueue() {

        if (validate()) {

            Thread thread = new Thread(getUser(), titleEt.getText().toString(), urlEt.getText().toString(), contentEt.getText().toString(), "default", helper.getCurrentDate(), 0, 0);
            if (threadsInQueue == null)
                threadsInQueue = new ArrayList<>();

            threadsInQueue.add(thread);
            Toast.makeText(getContext(), "No Internet Connection. Added to queue.", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();

        }
    }

    private boolean userIsConnected() {

        return Utility.isNetworkAvailable(getContext());
//        String textt = "";
//
//        if(mFirebaseAuth.isC() != null) {
//            textt = mFirebaseAuth.getCurrentUser().getEmail();
//        }
//
//        Toast.makeText(getContext(),textt , Toast.LENGTH_SHORT).show();

    }

    private void insertThread() {

        if (validate()) {

            Thread thread = new Thread(getUser(), titleEt.getText().toString(), urlEt.getText().toString(), contentEt.getText().toString(), "default", helper.getCurrentDate(), 0, 0);
            mDatabase.child("threads").push().setValue(thread);
            urlLink = "";
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.urlLink = "";
            getActivity().onBackPressed();

        }

    }


    public String getUser() {
        return mFirebaseAuth.getCurrentUser().getUid();
    }

    private boolean validate() {

        if (TextUtils.isEmpty(titleEt.getText().toString())) {
            titleEt.setError("Title can't be empty");
            return false;
        }
        if (TextUtils.isEmpty(urlEt.getText().toString())) {
            urlEt.setError("Video URL can't be empty");
            return false;
        }
        if (urlEt.getText().toString().length() < 11) {
            urlEt.setError("Video URL is not proper");
            return false;
        }

        if (!urlEt.getText().toString().contains(".") && !urlEt.getText().toString().contains("youtu") && !urlEt.getText().toString().contains("/")
                && linkIdIsProper(urlEt)) {
            urlEt.setError("Video URL is not proper");
            return false;
        }

        return true;

    }

    private boolean linkIdIsProper(EditText urlEt) {

        //TODO CHECK LAST IF 11 last CHARACTERS CONTAIN

        String ytId = urlEt.getText().toString().substring(urlEt.length() - 11, urlEt.length());

        return !ytId.contains("/");


    }

    private void onInsertThreadFailed() {

        Toast.makeText(getContext(), "Error inserting thread!", Toast.LENGTH_SHORT).show();
        FirebaseCrash.log("Error inserting thread!");

    }//https://youtu.be/cChsHZ6RVgY?t=5m51s

    private String[] getAuthor() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String[] author = new String[0];

        //TODO GET FROM AUTH
        if (user != null) {
            author = new String[]{user.getDisplayName(), user.getEmail()};


        } else {
            Toast.makeText(getContext(), "Lost Connection", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        return author;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        urlEt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO CHANGE TO BUTTERKNIFE
//
//            }
//        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insert_thread, container, false);
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
    public void onPause() {
        super.onPause();

        if (threadsInQueue != null) {

            saveSharedPreferencesLogList(getContext(), threadsInQueue);
            //Toast.makeText(getContext(), threadsInQueue.toString() + "added to queue", Toast.LENGTH_SHORT).show();
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
