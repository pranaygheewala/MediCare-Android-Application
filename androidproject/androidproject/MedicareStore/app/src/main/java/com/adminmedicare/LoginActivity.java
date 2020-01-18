package com.adminmedicare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private static final String TAG = "LoginActivity";
    EditText login_edt_email, login_edt_password;
    Button btn_login;
    ProgressDialog progressDialog;
    private RequestQueue mRequestQueue;
    private static final String URL_FOR_LOGIN = "http://159.89.169.152/medical/api/adminLogin";

    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mActivity = LoginActivity.this;

        status = Preferences.getStringPref(getApplicationContext(),Preferences.login_status);

        if(status.equals("login")){
            Intent i = new Intent(LoginActivity.this, DashBoardActivity.class);
            startActivity(i);
            finish();
        }

        findViews();
        init();
        setListener();
    }

    private void findViews() {
        login_edt_email = findViewById(R.id.login_edt_email);
        login_edt_password = findViewById(R.id.login_edt_password);
        btn_login = findViewById(R.id.btn_login);
    }

    private void init() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mRequestQueue = Volley.newRequestQueue(mActivity);
    }

    private void setListener() {
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_login:
                if (verify()) {
                    callLogInAPI(login_edt_email.getText().toString(), login_edt_password.getText().toString());
                }
                break;
        }
    }


    private void callLogInAPI(final String email, final String password) {
        String cancel_req_tag = "login";
        progressDialog.setMessage("Logging you in...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    String responseMessage = jObj.getString("ResponseMessage");

                    if (responseMessage.equals("Login Successfully")) {

                        Preferences.setStringPref(getApplicationContext(),Preferences.login_status,"login");

                        Intent i = new Intent(LoginActivity.this, DashBoardActivity.class);
                        startActivity(i);

                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        // Adding request to request queue
        mRequestQueue.add(strReq);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private Boolean verify() {
        if (login_edt_email.getText().toString().trim().length() > 0) {

            if (isValidEmail(login_edt_email.getText().toString())) {

                if (login_edt_password.getText().toString().trim().length() > 0) {

                    if (login_edt_password.getText().toString().length() >= 6) {

                        return true;
                    } else {
                        login_edt_password.setError("Password length 6 and above");
                        return false;
                    }

                } else {
                    login_edt_password.setError("Enter Password");
                    return false;
                }
            } else {
                login_edt_email.setError("Enter Valid Email");
                return false;
            }

        } else {
            login_edt_email.setError("Enter Email");
            return false;
        }
    }

    static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
}
//Intent i = new Intent(LoginActivity.this,MainActivity.class);
//startActivity(i);