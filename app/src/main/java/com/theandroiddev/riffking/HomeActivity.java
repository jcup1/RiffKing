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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener, RankingFragment.OnFragmentInteractionListener,
        VideosFragment.OnFragmentInteractionListener, UploadFragment.OnFragmentInteractionListener,
        ThreadFragment.OnFragmentInteractionListener, InsertThreadFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, FragmentB.OnFragmentInteractionListener,
        FragmentC.OnFragmentInteractionListener, FragmentD.OnFragmentInteractionListener {

    private static final String TAG = "HomeActivity";
    public FloatingActionButton fab;
    public ImageView navProfileImg;
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


        //TODO CHECK IS CONNECTED TO THE INTERNET

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            }
        };

        fab = (FloatingActionButton) findViewById(R.id.fab);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final View headerView = navigationView.getHeaderView(0);
        navProfileImg = (ImageView) headerView.findViewById(R.id.nav_profile_img);
        navProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileFragment(headerView);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        navNameTv = (TextView) headerView.findViewById(R.id.nav_name_tv);
        setName();
        navEmailTv = (TextView) headerView.findViewById(R.id.nav_email_tv);
        navEmailTv.setText(SharedPrefManager.getInstance(this).getEmail());

        HomeFragment fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_home, fragment).commit();
        youtubeShareable();


    }

    private void youtubeShareable() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            urlLink = extras.getString(Intent.EXTRA_TEXT);

            if (urlLink != null && !urlLink.equals("")) {
                displaySelectedScreen(R.id.nav_upload);
            }

        }

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

    private void openProfileFragment(View headerView) {

        ProfileFragment profileFragment = new ProfileFragment();

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

    private void logout() {

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

        switch (id) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_ranking:
                fragment = new RankingFragment();
                Toast.makeText(this, "nic tu nie ma", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_videos:
                fragment = new VideosFragment();
                Toast.makeText(this, "niczego tu nie znajdę...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_upload:

                Bundle bundle = new Bundle();
                bundle.putString("URL", urlLink);
                fragment = new InsertThreadFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Przed wyruszeniem w drogę, nalezy zebrać drużynę...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
                Toast.makeText(this, "Nie w mieście...", Toast.LENGTH_SHORT).show();
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

    public void loadQueued() {
        List<Thread> threadsInQueue = loadSharedPreferencesLogList(this);

        Toast.makeText(this, "on RESUME" + threadsInQueue.toString(), Toast.LENGTH_SHORT).show();
        if (!threadsInQueue.isEmpty() && userIsConnected()) {

            //HomeActivity homeActivity = (HomeActivity) getActivity();

            for (int i = 0; i < threadsInQueue.size(); i++) {
                Toast.makeText(this, "adding", Toast.LENGTH_SHORT).show();
                mDatabase.child("threads").push().setValue(threadsInQueue.get(i));
                threadsInQueue.remove(i);
            }
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
//        Toast.makeText(getContext(),textt , Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        int id;
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

        if (mFirebaseAuth.getCurrentUser() != null) {
            navNameTv.setText(mFirebaseAuth.getCurrentUser().getDisplayName());

        } else {
            Toast.makeText(this, "Can't get user name", Toast.LENGTH_SHORT).show();
            navNameTv.setText("");
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
