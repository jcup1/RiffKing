package com.example.grazyna.riffking;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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
    EditText title_et, author_et, URL_et;
    Button insert_btn;
    private String insert_thread_URL = "http://theandroiddev.com/insert_thread.php";
    // TODO: Rename and change types of parameters
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        title_et = (EditText) getActivity().findViewById(R.id.title_et);
        author_et = (EditText) getActivity().findViewById(R.id.author_et);
        URL_et = (EditText) getActivity().findViewById(R.id.URL_et);
        insert_btn = (Button) getActivity().findViewById(R.id.insert_btn);

        setAuthor(author_et);
        insert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertThread();
            }
        });

    }

    private void insertThread() {

        if (validate()) {

            authenticate(title_et.getText().toString(), author_et.getText().toString(),
                    URL_et.getText().toString());

        } else {
            onInsertThreadFailed();
        }


    }

    private void authenticate(final String title, final String author, final String URL) {

        insert_btn.setEnabled(false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                insert_thread_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);

                if (response.contains("Thread insert failed")) {
                    onInsertThreadFailed();

                } else {
                    Toast.makeText(getContext(), "SUCCESS! " + response, Toast.LENGTH_SHORT).show();
                    onInsertThreadSuccess(response, title, author, URL);

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error checking", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                onInsertThreadFailed();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", title);
                params.put("author", author);
                params.put("URL", URL);
                params.put("date", getDateTime());
                return params;
            }
        };


        MySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);

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

        return !(TextUtils.isEmpty(title_et.getText().toString()) ||
                TextUtils.isEmpty(author_et.getText().toString()));

    }

    private void onInsertThreadFailed() {

        insert_btn.setEnabled(true);
        Toast.makeText(getContext(), "Error inserting thread!", Toast.LENGTH_SHORT).show();

    }

    private void setAuthor(EditText author_et) {

        String author = SharedPrefManager.getInstance(getActivity()).getEmail();
        author_et.setText(author);

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
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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
