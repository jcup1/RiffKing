package com.theandroiddev.riffking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
    String value1 = "";
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    private String get_user_name_URL = "http://theandroiddev.com/get_user_name.php";

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
            value1 = extras.getString(Intent.EXTRA_TEXT);

            if (value1 != null && !value1.equals("")) {
                Toast.makeText(this, value1
                        , Toast.LENGTH_SHORT).show();

                displaySelectedScreen(R.id.nav_upload);


            }

        }

    }


    private void openProfileFragment(View headerView) {

        ProfileFragment profileFragment = new ProfileFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_home, profileFragment).addToBackStack(null);
        fragmentTransaction.commit();

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
                break;
            case R.id.nav_videos:
                fragment = new VideosFragment();
                break;
            case R.id.nav_upload:

                Bundle bundle = new Bundle();
                bundle.putString("URL", value1);
                fragment = new InsertThreadFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_settings:
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
