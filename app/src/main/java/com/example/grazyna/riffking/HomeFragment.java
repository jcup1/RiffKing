package com.example.grazyna.riffking;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;


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

    public void setRecyclerViewLayoutManager(LayoutManagerType recyclerViewLayoutManager) {

        int scrollPosition = 0;

        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).
                    findFirstCompletelyVisibleItemPosition();
        }

        switch (recyclerViewLayoutManager) {

            case GRID_LAYOUT_MANAGER:
                layoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                currentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINIEAR_LAYOUT_MANAGER:
                layoutManager = new LinearLayoutManager(getActivity());
                currentLayoutManagerType = LayoutManagerType.LINIEAR_LAYOUT_MANAGER;
                break;
            default:
                layoutManager = new LinearLayoutManager(getActivity());
                currentLayoutManagerType = LayoutManagerType.LINIEAR_LAYOUT_MANAGER;

        }

        recyclerView.setLayoutManager(layoutManager);
        currentLayoutManagerType = LayoutManagerType.LINIEAR_LAYOUT_MANAGER;

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

        initDataset();
        //threadToInsert(param1, param2, param3, param4);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");

        initDataset();


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

//    private void threadToInsert(String id, String title, String author, String URL) {
//
//
//
//        int id_num = Integer.parseInt(id);
//
//        Thread threadToInsert = new Thread(id_num, title, author, URL);
//
//        threads.add(threadToInsert);
//        Log.e(TAG, "threadToInsert: CREATIN" );
//        customAdapter.notifyDataSetChanged();
//    }

    private void insertThread() {

        InsertThreadFragment insertThreadFragment = new InsertThreadFragment();

        FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_home, insertThreadFragment).addToBackStack("state3");
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
        View rootView = inflater.inflate(R.layout.recyler_view_frag, container, false);
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
        Toast.makeText(getContext(), "ASDASD", Toast.LENGTH_SHORT).show();

        if (getArguments() != null) {
            id = getArguments().getString("id");
            title = getArguments().getString("title");
            author = getArguments().getString("author");
            URL = getArguments().getString("URL");


            //threadToInsert(id, title, author, URL);
        }

        if (customAdapter == null) {
            customAdapter = new CustomAdapter(inflater.getContext(), threads);

        }
        recyclerView.setAdapter(customAdapter);


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

        customAdapter = new CustomAdapter(getContext(), threads);


        for (int i = 0; i < DATASET_COUNT; i++) {
            threads.add(new Thread());
            threads.get(i).setTitle("This is element #" + i);
        }
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
