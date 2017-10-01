package com.theandroiddev.riffking;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    public static final String KEY_LAYOUT_MANAGER = "mLayoutManager";
    private static final String TAG = "HomeFragment";

    // TODO: Rename parameter arguments, choose names that match

    protected RecyclerView mRecyclerView;
    protected ThreadAdapter mThreadAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Thread> threads;
    protected ArrayList<Thread> threadsToRemove;
    protected LayoutManagerType mCurrentLayoutManagerType;
    protected Helper helper;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            int pos = viewHolder.getAdapterPosition();
            toRemove(pos);

        }
    };
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase;
    private OnFragmentInteractionListener mListener;
    private LayoutManagerType recyclerViewLayoutManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    private void toRemove(int position) {
        showUndoSnackbar(position);

    }

    private void showUndoSnackbar(final int position) {
        final Thread thread = threads.get(position);
        Snackbar snackbar = Snackbar
                .make(mRecyclerView, "REMOVED", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        undoToRemove(thread, position);
                    }
                });
        snackbar.show();

        sendToRemoveQueue(thread, position);
    }

    private void sendToRemoveQueue(Thread thread, int position) {
        threads.remove(position);
        mThreadAdapter.notifyItemRemoved(position);
        threadsToRemove.add(thread);
    }

    private void undoToRemove(Thread thread, int position) {
        threads.add(position, thread);
        mThreadAdapter.notifyItemInserted(position);
        mRecyclerView.scrollToPosition(position);
        threadsToRemove.remove(thread);
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

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        helper = new Helper();

        threads = new ArrayList<>();
        threadsToRemove = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("threads").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                threads.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children) {
                    Thread thread = child.getValue(Thread.class);
                    thread.setId(child.getKey());
                    threads.add(0, thread);

                }
                mThreadAdapter.notifyDataSetChanged();
                Log.e(TAG, "onDataChange:QWE ");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        remove();

    }

    private void remove() {


        while (threadsToRemove.size() > 0) {
            Log.d(TAG, "remove: " + threadsToRemove + " " + threadsToRemove.size());

            mDatabase.child("threads").child(threadsToRemove.get(0).getId()).removeValue();
            threadsToRemove.remove(0);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(com.theandroiddev.riffking.R.layout.fragment_home, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);


        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        mThreadAdapter = new ThreadAdapter(getContext(), threads, mDatabase);
        mRecyclerView.setAdapter(mThreadAdapter);

        if (HomeActivity.user.getEmail().equals("jakubpchmiel@gmail.com")) {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(mRecyclerView);
        }

        //TODO check why set two times
        setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);


        return rootView;
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), Helper.SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
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
    @Override
    public void onResume() {
        super.onResume();

        if (!userIsConnected()) {
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }


        mThreadAdapter.notifyDataSetChanged();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//
//                    getActivity().finish();
//
//                    return true;
//                }
//
//
//                return false;
//            }
//        });

        //TODO shoudnt it be in onCreateView?

        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.fab.setVisibility(View.VISIBLE);
        homeActivity.fab.setImageResource(R.drawable.ic_add_24dp);
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
        transaction.replace(com.theandroiddev.riffking.R.id.content_home, insertThreadFragment).addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed()) {
            onResume();
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


    public enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
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
