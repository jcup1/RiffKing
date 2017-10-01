package com.theandroiddev.riffking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener, RankingFragment.OnFragmentInteractionListener,
        VideosFragment.OnFragmentInteractionListener, UploadFragment.OnFragmentInteractionListener,
        ThreadFragment.OnFragmentInteractionListener, InsertThreadFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, ProfileCommentsFragment.OnFragmentInteractionListener,
        ProfileVideosFragment.OnFragmentInteractionListener, ProfileRepFragment.OnFragmentInteractionListener,
        ProfileRepFragmentMe.OnFragmentInteractionListener {

    private static final String TAG = "HomeActivity";
    static User user;
    static Thread thread;
    public FloatingActionButton fab;
    public CircularImageView navProfileImg;
    public TextView navNameTv, navEmailTv;
    DrawerLayout drawer;
    String urlLink = "";
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabase;

    public static List<Thread> loadSharedPreferencesLogList(Context context) {
        List<Thread> callLog = new ArrayList<Thread>();
        SharedPreferences mPrefs = context.getSharedPreferences("threadsSaved", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("myJson", "");
        if (json.isEmpty()) {
            callLog = new ArrayList<Thread>();
        } else {
            Type type = new TypeToken<List<Thread>>() {
            }.getType();
            callLog = gson.fromJson(json, type);
        }
        return callLog;
    }

    public static void saveSharedPreferencesLogList(Context context, List<Thread> callLog) {
        SharedPreferences mPrefs = context.getSharedPreferences("threadsSaved", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(callLog);
        prefsEditor.putString("myJson", json);
        prefsEditor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFirebaseAuth = FirebaseAuth.getInstance();

        initUser();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("users").child(user.getId()).getValue(User.class) == null) {
                    logout();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            }
        };


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final View headerView = navigationView.getHeaderView(0);
        navProfileImg = (CircularImageView) headerView.findViewById(R.id.nav_profile_img);
        navProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileFragment(headerView);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        navNameTv = (TextView) headerView.findViewById(R.id.nav_name_tv);
        navEmailTv = (TextView) headerView.findViewById(R.id.nav_email_tv);

        setName();


        HomeFragment fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_home, fragment).commit();

        youtubeShareable();

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            onBackPressed();
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public void onBackPressed() {
//        Log.d(TAG, "onBackPressed: ");
//
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//
//        } else {
//            if (getFragmentManager().getBackStackEntryCount() > 1) {
//                getFragmentManager().popBackStack();
//            } else {
//                super.onBackPressed();
//            }
//        }
//
//    }

    private void youtubeShareable() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            urlLink = extras.getString(Intent.EXTRA_TEXT);

            if (urlLink != null && !urlLink.equals("")) {
                displaySelectedScreen(R.id.nav_upload);
            }

        }

    }

    private void openProfileFragment(View headerView) {

        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", user.getId());
        bundle.putString("CURRENT_USER_ID", user.getId()); //Two same only in this case
        Log.d(TAG, "openProfileFragment: " + user.getId());
        profileFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_home, profileFragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        if (id == R.id.action_blog) {
            Intent i = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://theandroiddev.com/"));
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    void logout() {

        mFirebaseAuth.signOut();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        displaySelectedScreen(id);

        return true;
    }

    private void displaySelectedScreen(int id) {

        Fragment fragment = null;
        Bundle bundle;

        switch (id) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                bundle = new Bundle();
                bundle.putString("CURRENT_USER_ID", user.getId());
                bundle.putString("USER_ID", user.getId());
                fragment.setArguments(bundle);

                break;
            case R.id.nav_ranking:
                bundle = new Bundle();
                bundle.putString("CURRENT_USER_ID", user.getId());
                bundle.putString("USER_ID", user.getId());
                fragment = new RankingFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.nav_videos:
                bundle = new Bundle();
                bundle.putString("CURRENT_USER_ID", user.getId());
                bundle.putString("USER_ID", user.getId());
                fragment = new ProfileVideosFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.nav_upload:
                bundle = new Bundle();
                bundle.putString("URL", urlLink);
                fragment = new InsertThreadFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Not ready yet :(", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
                Toast.makeText(this, "Not ready yet :(", Toast.LENGTH_SHORT).show();
                break;

        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_home, fragment).addToBackStack("stack1").commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        loadQueued();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void loadQueued() {
        List<Thread> threadsInQueue = loadSharedPreferencesLogList(this);

        //Toast.makeText(this, "on RESUME" + threadsInQueue.toString(), Toast.LENGTH_SHORT).show();
        if (!threadsInQueue.isEmpty() && userIsConnected()) {

            //HomeActivity homeActivity = (HomeActivity) getActivity();

            for (int i = 0; i < threadsInQueue.size(); i++) {
                //Toast.makeText(this, "adding", Toast.LENGTH_SHORT).show();
                mDatabase.child("threads").push().setValue(threadsInQueue.get(i));
                threadsInQueue.remove(i);
            }

            saveSharedPreferencesLogList(this, threadsInQueue);
        }
    }

    private boolean userIsConnected() {

        return Utility.isNetworkAvailable(this);
//        String textt = "";
//
//        if(mFirebaseAuth.isC() != null) {
//            textt = mFirebaseAuth.getCurrentUser().getEmail();
//        }
//

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        int id = R.id.nav_home;
        if (fragment instanceof HomeFragment) id = R.id.nav_home;
        else if (fragment instanceof RankingFragment) id = R.id.nav_ranking;
        else if (fragment instanceof VideosFragment) id = R.id.nav_videos;
        else if (fragment instanceof UploadFragment) id = R.id.nav_upload;
        else if (fragment instanceof ProfileFragment) id = R.id.nav_profile_img;
        else return;
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getHeaderView(id);
        navigationView.setCheckedItem(id);
    }

    public void setName() {

        navNameTv.setText(user.getName());
        navEmailTv.setText(user.getEmail());
        Picasso.with(this).load(user.getPhotoUrl()).into(navProfileImg);

    }

    public void initUser() {

        if (mFirebaseAuth.getCurrentUser() != null) {
            user = new User();
            user.setId(mFirebaseAuth.getCurrentUser().getUid());
            user.setName(mFirebaseAuth.getCurrentUser().getDisplayName());
            user.setEmail(mFirebaseAuth.getCurrentUser().getEmail());
            user.setPhotoUrl(mFirebaseAuth.getCurrentUser().getPhotoUrl().toString());
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
