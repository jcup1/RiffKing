package com.example.grazyna.riffking;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    public static final String KEY_LAYOUT_MANAGER = "layoutManager";
    public static final int SPAN_COUNT = 2;
    public static final int DATASET_COUNT = 2;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String TAG = "HomeFragment";
    protected LayoutManagerType currentLayoutManagerType;
    protected RecyclerView recyclerView;
    protected CustomAdapter customAdapter;
    protected RecyclerView.LayoutManager layoutManager;
    protected ArrayList<Thread> threads;
    private String getThreadsURL = "http://theandroiddev.com/get_threads.php";
    private LayoutManagerType recyclerViewLayoutManager;
    private boolean logShown;
    // TODO: Rename and change types of parameters
    private String id;
    private String title;
    private String author;
    private String URL;
    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public HomeFragment newInstance(String param1, String param2, String param3, String param4) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    getActivity().finish();

                    return true;
                }


                return false;
            }
        });

        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertThread();
            }
        });
    }


    private void insertThread() {

        InsertThreadFragment insertThreadFragment = new InsertThreadFragment();

        FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_home, insertThreadFragment).addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed()) {
            onResume();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rootView.setTag(TAG);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

//        currentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
//
//        if (savedInstanceState != null) {
//            currentLayoutManagerType = (LayoutManagerType) savedInstanceState.getSerializable(KEY_LAYOUT_MANAGER);
//
//        }
//        setRecyclerViewLayoutManager(currentLayoutManagerType);
        if (getArguments() != null) {
            id = getArguments().getString("id");
            title = getArguments().getString("title");
            author = getArguments().getString("author");
            URL = getArguments().getString("URL");


            //threadToInsert(id, title, author, URL);
        }

        initDataset();




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
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_LAYOUT_MANAGER, currentLayoutManagerType);
        super.onSaveInstanceState(outState);
    }

    private void initDataset() {

        threads = new ArrayList<>();
        threads = getList();

    }

    public ArrayList<Thread> getList() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,
                getThreadsURL, (String) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int count = 0;
                while (count < response.length()) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(count);
                        Thread thread = new Thread(jsonObject.getString("title"),
                                jsonObject.getString("name"),
                                jsonObject.getString("comments"),
                                jsonObject.getString("URL"),
                                jsonObject.getInt("thread_id"),
                                jsonObject.getInt("likes"),
                                jsonObject.getInt("views"),
                                jsonObject.getString("date"));
                        threads.add(thread);
                        count++;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                customAdapter = new CustomAdapter(getContext(), threads);
                recyclerView.setAdapter(customAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();

            }
        });

        MySingleton.getmInstance(getContext()).addToRequestQueue(jsonArrayRequest);
        return threads;
    }

    public String getCurrentDate() {


        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String currentDate = df.format(Calendar.getInstance().getTime());

        return currentDate;

    }


    private void onGetThreadFailed() {

        Toast.makeText(getContext(), "Error getting threads", Toast.LENGTH_SHORT).show();
    }

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINIEAR_LAYOUT_MANAGER
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
