package com.theandroiddev.riffking;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "InsertThreadFragment";
    EditText title_et, URL_et, content_et;
    Button insert_btn;
    String URL;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private String currentDate;

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
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("URL");
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
        mDatabase = FirebaseDatabase.getInstance().getReference();


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        title_et = (EditText) getActivity().findViewById(R.id.title_et);
        URL_et = (EditText) getActivity().findViewById(R.id.URL_et);
        content_et = (EditText) getActivity().findViewById(R.id.content_et);
        insert_btn = (Button) getActivity().findViewById(R.id.insert_btn);

        insert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertThread();
            }
        });

        URL_et.setText(mParam1);
        Toast.makeText(getContext(), mParam1, Toast.LENGTH_SHORT).show();

    }


    private void insertThread() {

        if (validate()) {

            Thread thread = new Thread(title_et.getText().toString(), getAuthor(), URL_et.getText().toString(), content_et.getText().toString());
            mDatabase.child("threads").push().setValue(thread);


        } else {
            onInsertThreadFailed();
        }


    }


    private void onInsertThreadSuccess(String id, String title1, String author, String URL) {

        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("title", title1);
        bundle.putString("author", author);
        bundle.putString("URL", URL);

        Toast.makeText(getContext(), id + " " + title1 + " " + author + " " + URL,
                Toast.LENGTH_SHORT).show();

        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        try {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_home, homeFragment);
            transaction.commit();

        } catch (ClassCastException e) {
            Log.e(TAG, "cant get fragment manager");
            Toast.makeText(getContext(), "Cant get fragment manager!", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean validate() {

        if (TextUtils.isEmpty(title_et.getText().toString())) {
            title_et.setError("Title can't be empty");
            return false;
        }
        if (TextUtils.isEmpty(content_et.getText().toString())) {
            content_et.setError("Content can't be empty");
            return false;
        }

        return true;

    }

    private void onInsertThreadFailed() {

        insert_btn.setEnabled(true);
        Toast.makeText(getContext(), "Error inserting thread!", Toast.LENGTH_SHORT).show();

    }

    private String getAuthor() {

        //TODO GET FROM AUTH
        String author = SharedPrefManager.getInstance(getActivity()).getEmail();


        return author;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
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
