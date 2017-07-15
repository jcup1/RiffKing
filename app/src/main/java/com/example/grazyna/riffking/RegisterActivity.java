package com.example.grazyna.riffking;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.register_logo_img)
    ImageView logoImg;
    @BindView(R.id.register_email_et)
    TextInputEditText emailEt;
    @BindView(R.id.register_password_et)
    TextInputEditText passwordEt;
    @BindView(R.id.register_register_btn)
    Button registerBtn;
    @BindView(R.id.register_login_tv)
    TextView loginTv;

    String email, password, name;

    @OnClick(R.id.register_register_btn)
    public void register() {

        tryToRegister(emailEt, passwordEt);

    }

    private void tryToRegister(TextInputEditText emailEt, TextInputEditText passwordEt) {

        //TODO Wroclaw

    }

    @OnClick(R.id.register_login_tv)
    public void login() {

        startLoginActivity();

    }

    private void startLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
}
