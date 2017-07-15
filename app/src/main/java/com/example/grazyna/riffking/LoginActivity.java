package com.example.grazyna.riffking;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    public static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.login_logo_img)
    ImageView logoImg;
    @BindView(R.id.login_email_et)
    TextInputEditText emailEt;
    @BindView(R.id.login_password_et)
    TextInputEditText passwordEt;
    @BindView(R.id.login_login_btn)
    Button loginBtn;
    @BindView(R.id.login_register_tv)
    TextView registerTv;
    private String login_url = "http://theandroiddev.com/login.php";
    private boolean loggedIn;

    @OnClick(R.id.login_login_btn)
    public void login() {

        tryToLogin(emailEt, passwordEt);

    }

    @OnClick(R.id.login_register_tv)
    public void register() {

        startRegisterActivity();

    }

    private void tryToLogin(TextInputEditText emailEt, TextInputEditText passwordEt) {

        loginBtn.setEnabled(false);
        final String email = emailEt.getText().toString();
        final String password = passwordEt.getText().toString();

        if (!validate(email, password)) {
            onLoginFailed();
        } else {
            authenticate(email, password);
        }

    }

    private void onLoginFailed() {

        Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
        loginBtn.setEnabled(true);

    }

    private void authenticate(final String email, final String password) {
        //TODO Wroclaw
    }

    public boolean validate(String email, String password) {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("enter a valid email address");
            valid = false;
        } else {
            emailEt.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 16) {
            passwordEt.setError("between 4 and 16 alphanumeric characters");
            valid = false;
        } else {
            passwordEt.setError(null);
        }

        return valid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isLoggedIn()) {
            onLoggedIn();
        } else {
            onNotLoggedIn();
        }

    }

    private void onLoggedIn() {
        startMainActivity();

    }

    private void startMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void onNotLoggedIn() {
        ButterKnife.bind(this); //TODO CHECK IS IT THE RIGHT METHOD
    }

    private void startRegisterActivity() {

        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(registerIntent, REQUEST_SIGNUP);

    }

    public boolean isLoggedIn() {

        //TODO SHARED PREF
        return true;
    }

}
