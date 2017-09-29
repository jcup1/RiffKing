package com.theandroiddev.riffking;

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

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    @BindView(com.theandroiddev.riffking.R.id.register_logo_img)
    ImageView logoImg;
    @BindView(com.theandroiddev.riffking.R.id.register_email_et)
    TextInputEditText emailEt;
    @BindView(com.theandroiddev.riffking.R.id.register_password_et)
    TextInputEditText passwordEt;
    @BindView(com.theandroiddev.riffking.R.id.register_name_et)
    TextInputEditText nameEt;
    @BindView(com.theandroiddev.riffking.R.id.register_register_btn)
    Button registerBtn;
    @BindView(com.theandroiddev.riffking.R.id.register_login_tv)
    TextView loginTv;
    String email, password, name;

    @OnClick(com.theandroiddev.riffking.R.id.register_register_btn)
    public void register() {

        //tryToRegister(emailEt, passwordEt);

    }

    @OnClick(com.theandroiddev.riffking.R.id.register_login_tv)
    public void login() {

        startLoginActivity();
    }

    private void onRegisterFailed() {

        Toast.makeText(this, "Register Failed", Toast.LENGTH_SHORT).show();
        registerBtn.setEnabled(true);

    }

    private boolean validate() {

        return true;
    }

    private void onEmailNotAvalible() {

        emailEt.setError("Email is in use");
        registerBtn.setEnabled(true);
    }

    private void onRegisterSuccess(int id, String email) {
        registerBtn.setEnabled(true);

        nextActivity(id, email);
        finish();
    }

    private void nextActivity(int id, String email) {

        SharedPrefManager.getInstance(this).userLogin(id, email);
        Intent intent;
        intent = new Intent(RegisterActivity.this, HomeActivity.class);
        finish();
        startActivity(intent);

    }


    private void startLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.theandroiddev.riffking.R.layout.activity_register);

        ButterKnife.bind(this);


    }
}
