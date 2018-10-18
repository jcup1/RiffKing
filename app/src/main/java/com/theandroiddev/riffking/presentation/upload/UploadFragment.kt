package com.theandroiddev.riffking.presentation.upload

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.theandroiddev.riffking.R
import com.theandroiddev.riffking.presentation.home.HomeActivity
import com.theandroiddev.riffking.presentation.home.HomeActivity.Companion.saveSharedPreferencesLogList
import com.theandroiddev.riffking.presentation.thread.Thread
import com.theandroiddev.riffking.utils.Helper
import com.theandroiddev.riffking.utils.Helper.Companion.ytIdLength
import com.theandroiddev.riffking.utils.Utility
import kotlinx.android.synthetic.main.fragment_upload.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [UploadFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [UploadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadFragment : Fragment() {

    private var URL: String? = null

    private var databaseReference: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var helper: Helper? = null
    private var urlLink: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private var threadsInQueue: MutableList<Thread>? = mutableListOf()

    val user: String?
        get() = firebaseAuth?.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        urlLink = arguments?.getString("URL")
        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onResume() {
        super.onResume()

        val homeActivity = activity as HomeActivity?
        homeActivity?.fab?.visibility = View.VISIBLE

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val homeActivity = activity as HomeActivity?
        homeActivity?.fab?.setImageResource(R.drawable.ic_check_24dp)
        homeActivity?.fab?.setOnClickListener {
            if (userIsConnected()) {
                //TODO highlight in drawer doesn't change
                insertThread()
            } else {
                insertThreadToQueue()
            }
        }

        URL_tv.text = urlLink
        youtube_btn.setOnClickListener {
            //TODO use butterknife
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://"))
            startActivity(intent)
        }
        //Toast.makeText(getContext(), urlLink, Toast.LENGTH_SHORT).show();

    }

    private fun insertThreadToQueue() {

        if (validate()) {

            val user = user
            if (user != null) {
                val thread = Thread(user, title_et.text.toString(), URL_tv.text.toString(),
                        content_et.text.toString(), "default", helper?.currentDate ?: "N/A", 0, 0)
                threadsInQueue?.add(thread)
            }
            Toast.makeText(context, "No Internet Connection. Added to queue.", Toast.LENGTH_SHORT).show()
            activity?.onBackPressed()

        }
    }

    private fun userIsConnected(): Boolean {

        val context = context
        return if (context != null) {
            Utility.isNetworkAvailable(context)
        } else {
            false
        }
        //        String textt = "";
        //
        //        if(firebaseAuth.isC() != null) {
        //            textt = firebaseAuth.getCurrentUser().getEmail();
        //        }
        //
        //        Toast.makeText(getContext(),textt , Toast.LENGTH_SHORT).show();

    }

    private fun insertThread() {

        if (validate()) {
            val user = user

            if (user != null) {
                val thread = Thread(user, title_et.text.toString(), URL_tv.text.toString(), content_et.text.toString(),
                        "default", helper?.currentDate ?: "N/A", 0, 0)
                databaseReference?.child("threads")?.push()?.setValue(thread)
                urlLink = ""
                val homeActivity = activity as HomeActivity?
                homeActivity?.urlLink = ""
                activity?.onBackPressed()
            }
        }

    }

    private fun validate(): Boolean {

        if (TextUtils.isEmpty(title_et.text.toString())) {
            title_et.error = "Title can't be empty"
            return false
        }
        if (TextUtils.isEmpty(URL_tv.text.toString())) {
            URL_tv.error = "Video URL can't be empty"
            return false
        }
        if (URL_tv.text.toString().length < ytIdLength) {
            URL_tv.error = "Video URL is not proper"
            return false
        }

        if (!URL_tv.text.toString().contains(".") && !URL_tv.text.toString().contains("youtu") && !URL_tv.text.toString().contains("/")
                && linkIdIsProper(URL_tv)) {
            URL_tv.error = "Video URL is not proper"
            return false
        }

        return true

    }

    private fun linkIdIsProper(urlEt: TextView): Boolean {

        val ytId = urlEt.text.toString().substring(urlEt.length() - ytIdLength, urlEt.length())

        return !ytId.contains("/")


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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
            helper = Helper(context)
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onPause() {
        super.onPause()
        val context = context
        val threadsInQueue = threadsInQueue
        if (threadsInQueue != null && context != null) {

            saveSharedPreferencesLogList(context, threadsInQueue)
            //Toast.makeText(getContext(), threadsInQueue.toString() + "added to queue", Toast.LENGTH_SHORT).show();
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        private val URL_PARAM = "URL_PARAM"
        private val TAG = "UploadFragment"

        fun newInstance(param1: String, param2: String): UploadFragment {
            val fragment = UploadFragment()
            val args = Bundle()
            args.putString(URL_PARAM, param1)
            fragment.arguments = args
            return fragment
        }
    }


}// Required empty public constructor
