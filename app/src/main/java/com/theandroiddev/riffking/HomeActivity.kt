package com.theandroiddev.riffking

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.nav_header_home.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener, RankingFragment.OnFragmentInteractionListener, ThreadFragment.OnFragmentInteractionListener, UploadFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, ProfileCommentsFragment.OnFragmentInteractionListener, ProfileVideosFragment.OnFragmentInteractionListener, ProfileRepFragment.OnFragmentInteractionListener, ProfileRepFragmentMe.OnFragmentInteractionListener {

    var fab: FloatingActionButton? = null

    var urlLink: String? = ""
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var authStateListener: FirebaseAuth.AuthStateListener
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    init {

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //        checkGuestMode();


        initUser()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userId = user?.id
                if (userId != null) {
                    if (dataSnapshot.child("users").child(userId).getValue(User::class.java) == null) {
                        logout()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
            }
        }


        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        fab = findViewById<View>(R.id.fab) as FloatingActionButton

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.setDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        val headerView = nav_view.getHeaderView(0)
        nav_profile_img.setOnClickListener {
            openProfileFragment(headerView)
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        setName()

        val fragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.content_home, fragment).commit()

        youtubeShareable()

        //        checkGuestModeViews();

    }

    //    private void checkGuestModeViews() {
    //        if (guestMode) {
    //
    //        }
    //    }
    //
    //    private void checkGuestMode() {
    //        Intent getIntent = getIntent();
    //        guestMode = getIntent.getBooleanExtra("guestmode", false);
    //    }


    //    @Override
    //    public void onBackPressed() {
    //
    //        Log.d(TAG, "onBackPressed: " +getSupportFragmentManager().getBackStackEntryCount() );
    //        if (getSupportFragmentManager().getBackStackEntryCount() >0) {
    //            getSupportFragmentManager().popBackStack();
    //        } else {
    //            Log.d(TAG, "onBackPressed: finishing");
    //            finish();
    //        }
    //    }

    private fun youtubeShareable() {
        val extras = intent.extras
        if (extras != null) {
            urlLink = extras.getString(Intent.EXTRA_TEXT)

            if (urlLink != null && urlLink != "") {
                displaySelectedScreen(R.id.nav_upload)
                //TODO send link to nav_upload and display in TV
            }

        }

    }

    private fun openProfileFragment(headerView: View) {

        val profileFragment = ProfileFragment()
        val bundle = Bundle()
        bundle.putString("USER_ID", user?.id)
        bundle.putString("CURRENT_USER_ID", user?.id) //Two same only in this case
        Log.d(TAG, "openProfileFragment: " + user?.id)
        profileFragment.arguments = bundle

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_home, profileFragment).addToBackStack(null)
        fragmentTransaction.commit()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }

        if (id == R.id.action_logout) {
            logout()
            return true
        }

        if (id == R.id.action_blog) {
            val i = Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://theandroiddev.com/"))
            startActivity(i)
        }

        return super.onOptionsItemSelected(item)
    }

    internal fun logout() {

        firebaseAuth.signOut()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        val id = item.itemId

        displaySelectedScreen(id)

        return true
    }

    private fun displaySelectedScreen(id: Int) {

        var fragment: Fragment? = null
        val bundle: Bundle

        when (id) {
            R.id.nav_home -> {
                fragment = HomeFragment()
                bundle = Bundle()
                bundle.putString("CURRENT_USER_ID", user?.id)
                bundle.putString("USER_ID", user?.id)
                fragment.arguments = bundle
            }
            R.id.nav_ranking -> {
                bundle = Bundle()
                bundle.putString("CURRENT_USER_ID", user?.id)
                bundle.putString("USER_ID", user?.id)
                fragment = RankingFragment()
                fragment.arguments = bundle
            }
            R.id.nav_videos -> {
                bundle = Bundle()
                bundle.putString("CURRENT_USER_ID", user?.id)
                bundle.putString("USER_ID", user?.id)
                fragment = ProfileVideosFragment()
                fragment.arguments = bundle
            }
            R.id.nav_upload -> {
                bundle = Bundle()
                bundle.putString("URL", urlLink)
                fragment = UploadFragment()
                fragment.arguments = bundle
            }
            R.id.nav_share -> share()
            R.id.nav_settings -> Toast.makeText(this, "Not ready yet :(", Toast.LENGTH_SHORT).show()
        }

        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_home, fragment).addToBackStack("stack1").commit()
        }

        drawer_layout.closeDrawer(GravityCompat.START)

    }

    private fun share() {
        Toast.makeText(this, "Not ready yet :(", Toast.LENGTH_SHORT).show()


    }

    override fun onPostResume() {
        super.onPostResume()

        loadQueued()

    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: DESTROOYED")
        super.onDestroy()
    }

    fun loadQueued() {
        val threadsInQueue = loadSharedPreferencesLogList(this)

        //Toast.makeText(this, "on RESUME" + threadsInQueue.toString(), Toast.LENGTH_SHORT).show();
        if (!threadsInQueue.isEmpty() && userIsConnected()) {

            //HomeActivity homeActivity = (HomeActivity) getActivity();

            for (i in threadsInQueue.indices) {
                //Toast.makeText(this, "adding", Toast.LENGTH_SHORT).show();
                database.child("threads").push().setValue(threadsInQueue[i])
                threadsInQueue.removeAt(i)
            }

            saveSharedPreferencesLogList(this, threadsInQueue)
        }
    }

    private fun userIsConnected(): Boolean {

        return Utility.isNetworkAvailable(this)
        //        String textt = "";
        //
        //        if(firebaseAuth.isC() != null) {
        //            textt = firebaseAuth.getCurrentUser().getEmail();
        //        }
        //

    }

    //TODO HANDLE SAVING FRAGMENTS WHEN ACTIVITY IS RESTORED
    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        val id: Int
        when (fragment) {
            is HomeFragment -> id = R.id.nav_home
            is RankingFragment -> id = R.id.nav_ranking
            is ProfileVideosFragment -> id = R.id.nav_videos
            is ProfileFragment -> id = R.id.nav_profile_img
            is UploadFragment -> id = R.id.nav_upload
            else -> return
        }
        nav_view.getHeaderView(id)
        nav_view.setCheckedItem(id)
    }

    fun setName() {
        if (guestMode == false) {
            nav_name_tv.text = user?.name
            nav_email_tv.text = user?.email
            Picasso.get().load(user?.photoUrl).into(nav_profile_img)
        } else {
            nav_name_tv.text = "Guest"
        }

    }

    fun initUser() {

        if (firebaseAuth.currentUser != null) {
            user = User()
            user?.id = firebaseAuth.currentUser?.uid
            user?.name = firebaseAuth.currentUser?.displayName
            user?.email = firebaseAuth.currentUser?.email
            user?.photoUrl = firebaseAuth.currentUser?.photoUrl.toString()
        }
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    companion object {

        private val TAG = "HomeActivity"
        var guestMode: Boolean? = false
        var user: User? = null

        fun loadSharedPreferencesLogList(context: Context): MutableList<Thread> {
            val mPrefs = context.getSharedPreferences("threadsSaved", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = mPrefs.getString("myJson", "")
            return if (json.isEmpty()) {
                mutableListOf()
            } else {
                val type = object : TypeToken<List<Thread>>() {

                }.type
                gson.fromJson(json, type)
            }
        }

        fun saveSharedPreferencesLogList(context: Context, callLog: List<Thread>) {
            val mPrefs = context.getSharedPreferences("threadsSaved", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            val gson = Gson()
            val json = gson.toJson(callLog)
            prefsEditor.putString("myJson", json)
            prefsEditor.commit()
        }
    }

}
