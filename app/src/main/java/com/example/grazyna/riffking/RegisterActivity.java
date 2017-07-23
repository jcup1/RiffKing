package com.example.grazyna.riffking;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    @BindView(R.id.register_logo_img)
    ImageView logoImg;
    @BindView(R.id.register_email_et)
    TextInputEditText emailEt;
    @BindView(R.id.register_password_et)
    TextInputEditText passwordEt;
    @BindView(R.id.register_name_et)
    TextInputEditText nameEt;
    @BindView(R.id.register_register_btn)
    Button registerBtn;
    @BindView(R.id.register_login_tv)
    TextView loginTv;
    String email, password, name;
    private String userRegisterURL = "http://theandroiddev.com/register.php";

    @OnClick(R.id.register_register_btn)
    public void register() {

        tryToRegister(emailEt, passwordEt);

    }

    @OnClick(R.id.register_login_tv)
    public void login() {

        startLoginActivity();
    }

    private void tryToRegister(TextInputEditText emailEt, TextInputEditText passwordEt) {

        //TODO Wroclaw

        if (!validate()) {
            onRegisterFailed();

        } else {

            registerBtn.setEnabled(false);

            String email = emailEt.getText().toString();
            String password = passwordEt.getText().toString();
            String name = nameEt.getText().toString();

            authenticate(email, password, name);
        }

    }

    private void onRegisterFailed() {

        Toast.makeText(this, "Register Failed", Toast.LENGTH_SHORT).show();
        registerBtn.setEnabled(true);

    }

    private boolean validate() {

        return true;
    }

    private void authenticate(final String email, final String password, final String name) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                userRegisterURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response);
                if (response.contains("Register Failed")) {
                    onRegisterFailed();
                } else if (response.contains("Error first")) {

                    onEmailNotAvalible();

                } else {

                    onRegisterSuccess(Integer.valueOf(response), email);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("name", name);

                return params;
            }
        };

        MySingleton.getmInstance(RegisterActivity.this).addToRequestQueue(stringRequest);
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
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);


    }
}
