package com.theandroiddev.riffking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 2;

    FirebaseAuth mFirebaseAuth;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth.AuthStateListener mAuthListener;
    @BindView(R.id.login_layout)
    ConstraintLayout loginLayout;
    @BindView(R.id.signInButton)
    SignInButton loginButton;
    @BindView(R.id.login_guest)
    TextView loginGuestTv;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;

    @Override
    protected void onStart() {
        super.onStart();

        initPostData();
    }

    private void initPostData() {
        if (Utility.isNetworkAvailable(getApplicationContext())) {
            mFirebaseAuth.addAuthStateListener(mAuthListener);
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }
    }

    @OnClick(com.theandroiddev.riffking.R.id.login_guest)
    public void guest() {
        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        i.putExtra("guestmode", true);
        startActivity(i);
        finish();
    }

    @OnClick(com.theandroiddev.riffking.R.id.signInButton)
    public void login() {
        if (Utility.isNetworkAvailable(getApplicationContext())) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else noInternetSnack();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.theandroiddev.riffking.R.layout.activity_login);
        ButterKnife.bind(this);

        //TODO finish ProgressDialog
        mProgress = new ProgressDialog(getApplicationContext());
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        if (Utility.isNetworkAvailable(getApplicationContext())) {

            initData();
        } else {
            noInternetSnack();
        }

    }

    private void initData() {

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    //i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                    startActivity(i);
                    finish();
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.theandroiddev.riffking.R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "Error Google API Connection", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void noInternetSnack() {

        Snackbar snack = Snackbar.make(loginLayout, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utility.isNetworkAvailable(getApplicationContext())) {
                            //Ifs to handle the case when user opened app with internet connection and lost it after onStart()
                            //If not null, then already managing a GoogleApiClient
                            if (mFirebaseAuth == null)
                                initData();
                            if (mDatabase == null)
                                initPostData();
                        } else noInternetSnack();
                    }
                });
        snack.show();
    }

    @Override
    public void onPause() {
        super.onPause();

        mGoogleApiClient.stopAutoManage(LoginActivity.this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();

                firebaseAuthWithGoogle(account);


            } else {
                Toast.makeText(this, "Firebase Auth Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if (account != null) {
                                Log.d(TAG, "onActivityResult: accnt not null");
                                User user = new User(account.getDisplayName(), account.getEmail(), account.getPhotoUrl().toString(), 0, 0, 0, 0, 0);
                                writeNewUser(user, mFirebaseAuth.getCurrentUser().getUid());
                            }
                            Log.d(TAG, "signInWithCredential:success");
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void writeNewUser(final User user, final String id) {

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("users").child(id).getValue(User.class) == null) {
                    mDatabase.child("users").child(id).setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
