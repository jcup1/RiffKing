package com.theandroiddev.riffking;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ImageView profileImg, profileMoneyImg, profileFollowersImg, profileMessagesImg, profilePmImg;
    TextView profileNameTv, profileDescTv, profileEmailTv, profileMoneyTv, profileFollowersTv, profileLikesTv;
    Button profileFollowBtn;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentTabHost tabHost;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        profileNameTv = (TextView) getView().findViewById(R.id.profile_name_tv);
        profileDescTv = (TextView) getView().findViewById(R.id.profile_desc_tv);
        profileEmailTv = (TextView) getView().findViewById(R.id.profile_email_tv);
        profileMoneyTv = (TextView) getView().findViewById(R.id.profile_money_tv);
        profileFollowersTv = (TextView) getView().findViewById(R.id.profile_followers_tv);
        profileLikesTv = (TextView) getView().findViewById(R.id.profile_likes_tv);

        //getUser(String.valueOf(SharedPrefManager.getInstance(getContext()).getId()));

    }

//    public void getUser(final String userId) {
//
//        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST,
//                getUserURL, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//
//                    //JSONObject jsonObject = response.getJSONObject(1);
//                    User user = new User(
//                            jsonObject.getString("name"),
//                            jsonObject.getInt("age"),
//                            jsonObject.getString("details"),
//                            jsonObject.getString("email"),
//                            jsonObject.getInt("likes"),
//                            jsonObject.getInt("rep"),
//                            jsonObject.getInt("followers"));
//
//                    initData(user);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
//                error.printStackTrace();
//
//            }
//        }
//
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Log.e(TAG, "getParams: passed data" + userId);
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("userid", userId);
//                return params;
//            }
//
//
//        };
//
//        MySingleton.getmInstance(getContext()).addToRequestQueue(jsonObjectRequest);
//
//
//    }

    private void initData(User user) {

        profileNameTv.setText(user.getName());
        profileDescTv.setText(user.getDetails());
        profileEmailTv.setText(user.getEmail());
        profileMoneyTv.setText(String.valueOf(user.getReps()));
        profileFollowersTv.setText(String.valueOf(user.getFollowers()));
        profileLikesTv.setText(String.valueOf(user.getLikes()));



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        tabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        tabHost.setup(getActivity(), getChildFragmentManager(), R.id.tab_container);

        tabHost.addTab(tabHost.newTabSpec("fragmentb").setIndicator("Fragment B"),
                FragmentB.class, null);
        tabHost.addTab(tabHost.newTabSpec("fragmentc").setIndicator("Fragment C"),
                FragmentC.class, null);
        tabHost.addTab(tabHost.newTabSpec("fragmentd").setIndicator("Fragment D"),
                FragmentD.class, null);

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
