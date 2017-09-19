package com.theandroiddev.riffking;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ThreadFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ThreadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThreadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "position";
    private static final String TAG = "ThreadFragment";

    TextView position_tv;

    // TODO: Rename and change types of parameters
    private String position;

    private OnFragmentInteractionListener mListener;
    private String getThreadURL = "http://theandroiddev.com/get_thread.php";

    public ThreadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThreadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThreadFragment newInstance(String param1, String param2) {
        ThreadFragment fragment = new ThreadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        // fragment

        if (getArguments() != null) {
            position = getArguments().getString(ARG_PARAM1);

        }

        return inflater.inflate(com.theandroiddev.riffking.R.layout.fragment_thread, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//
//        position_tv = (TextView) getView().findViewById(R.id.position_tv);
//        position_tv.setText(position);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public String getCurrentDate() {


        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));

        return df.format(Calendar.getInstance().getTime());

    }

    private void initData(Thread thread) {

//        position_tv.setText(thread.getTitle());
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
