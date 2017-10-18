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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.theandroiddev.riffking.Helper.YTIDLENGTH;
import static com.theandroiddev.riffking.HomeActivity.saveSharedPreferencesLogList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment {

    private static final String URL_PARAM = "URL_PARAM";
    private static final String TAG = "UploadFragment";
    EditText titleEt, contentEt;
    TextView urlTv;
    ImageView youtubeBtn;
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

    public UploadFragment() {
        // Required empty public constructor
    }

    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
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
    }

    @Override
    public void onResume() {
        super.onResume();

        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.fab.setVisibility(View.VISIBLE);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleEt = (EditText) getActivity().findViewById(R.id.title_et);
        urlTv = (TextView) getActivity().findViewById(R.id.URL_tv);
        contentEt = (EditText) getActivity().findViewById(R.id.content_et);
        youtubeBtn = (ImageView) getActivity().findViewById(R.id.youtube_btn);

        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.fab.setImageResource(R.drawable.ic_check_24dp);
        homeActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userIsConnected()) {
                    //TODO highlight in drawer doesn't change
                    insertThread();
                } else {
                    insertThreadToQueue();
                }

            }
        });

        urlTv.setText(urlLink);
        youtubeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO use butterknife
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://"));
                startActivity(intent);
            }
        });
        //Toast.makeText(getContext(), urlLink, Toast.LENGTH_SHORT).show();

    }

    private void insertThreadToQueue() {

        if (validate()) {

            Thread thread = new Thread(getUser(), titleEt.getText().toString(), urlTv.getText().toString(), contentEt.getText().toString(), "default", helper.getCurrentDate(), 0, 0);
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

            Thread thread = new Thread(getUser(), titleEt.getText().toString(), urlTv.getText().toString(), contentEt.getText().toString(), "default", helper.getCurrentDate(), 0, 0);
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
        if (TextUtils.isEmpty(urlTv.getText().toString())) {
            urlTv.setError("Video URL can't be empty");
            return false;
        }
        if (urlTv.getText().toString().length() < YTIDLENGTH) {
            urlTv.setError("Video URL is not proper");
            return false;
        }

        if (!urlTv.getText().toString().contains(".") && !urlTv.getText().toString().contains("youtu") && !urlTv.getText().toString().contains("/")
                && linkIdIsProper(urlTv)) {
            urlTv.setError("Video URL is not proper");
            return false;
        }

        return true;

    }

    private boolean linkIdIsProper(TextView urlEt) {

        String ytId = urlEt.getText().toString().substring(urlEt.length() - YTIDLENGTH, urlEt.length());

        return !ytId.contains("/");


    }

//    private void onInsertThreadFailed() {
//
//        Toast.makeText(getContext(), "Error inserting thread!", Toast.LENGTH_SHORT).show();
//        FirebaseCrash.log("Error inserting thread!");
//
//    }
//
//    private String[] getAuthor() {
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String[] author = new String[0];
//
//        if (user != null) {
//            author = new String[]{user.getDisplayName(), user.getEmail()};
//
//
//        } else {
//            Toast.makeText(getContext(), "Lost Connection", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getContext(), LoginActivity.class);
//            startActivity(intent);
//            getActivity().finish();
//        }
//
//        return author;
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_upload, container, false);
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
