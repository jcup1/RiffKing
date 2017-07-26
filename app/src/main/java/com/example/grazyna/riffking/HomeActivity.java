package com.example.grazyna.riffking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

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
    private String get_user_name_URL = "http://theandroiddev.com/get_user_name.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        displaySelectedScreen(R.id.nav_home);

    }

    private void openProfileFragment(View headerView) {

        ProfileFragment profileFragment = new ProfileFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_home, profileFragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() > 1) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }

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

        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {

            if (SharedPrefManager.getInstance(this).logout()) {
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error logging out", Toast.LENGTH_SHORT).show();
            }

            startLoginActivity();

        } else {
            Toast.makeText(this, "User is not logged in!", Toast.LENGTH_SHORT).show();
        }

    }

    private void startLoginActivity() {

        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
        finish();
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
                fragment = new UploadFragment();
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_settings:
                break;

        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_home, fragment).addToBackStack("state1");
            fragmentTransaction.commit();
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

        final String email = SharedPrefManager.getInstance(this).getEmail();
//        final String id = SharedPrefManager.getInstance(this).getId();


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                get_user_name_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("No user")) {
                    noUser();
                } else {

                    onNameSet(response);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
//                params.put("id", id);

                return params;
            }
        };

        MySingleton.getmInstance(HomeActivity.this).addToRequestQueue(stringRequest);

    }

    private void onNameSet(String response) {

        navNameTv.setText(response);
    }

    private void noUser() {
        Toast.makeText(this, "Can't get user name", Toast.LENGTH_SHORT).show();
        navNameTv.setText("");


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
