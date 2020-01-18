package com.medicare;

import androidx.appcompat.app.AppCompatActivity;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.medicare.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private static final String TAG = "LoginActivity";
    EditText login_edt_email, login_edt_password;
    TextView login_tv_forgotPass;
    Button btn_login, btn_signup;
    ProgressDialog progressDialog;
    private RequestQueue mRequestQueue;
    private static final String URL_FOR_LOGIN = "http://159.89.169.152/medical/api/login";

    String token;
    ArrayList<String> addressArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mActivity = LoginActivity.this;

        String status = Preferences.getStringPref(getApplicationContext(),Preferences.user_log_in);
        if(status.equals("login")){
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
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
        login_tv_forgotPass = findViewById(R.id.login_tv_forgotPass);
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);
    }

    private void init() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mRequestQueue = Volley.newRequestQueue(mActivity);
    }

    private void setListener() {
        login_tv_forgotPass.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_tv_forgotPass:
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
                break;
            case R.id.btn_login:
                if (verify()) {
                    callLogInAPI(login_edt_email.getText().toString(), login_edt_password.getText().toString(), token);
                }
                break;
            case R.id.btn_signup:
                Intent i1 = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i1);
                break;
        }
    }


    private void callLogInAPI(final String email, final String password, final String token) {
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

                    if (responseMessage.equals("success")) {
                        JSONObject data = jObj.getJSONObject("data");

                        Log.e(TAG, "onResponse: " + data.getString("id") );
                        Log.e(TAG, "onResponse: " + data.getString("name") );
                        Log.e(TAG, "onResponse: " + data.getString("email") );
                        Log.e(TAG, "onResponse: " + data.getString("password") );
                        Log.e(TAG, "onResponse: " + data.getString("dob") );
                        Log.e(TAG, "onResponse: " + data.getString("gender") );
                        Log.e(TAG, "onResponse: " + data.getString("address") );
                        Log.e(TAG, "onResponse: " + data.getString("token") );

                        Preferences.setStringPref(getApplicationContext(),Preferences.user_name,data.getString("name"));
                        Preferences.setStringPref(getApplicationContext(),Preferences.user_id,data.getString("id"));
                        Preferences.setStringPref(getApplicationContext(),Preferences.user_email,data.getString("email"));
                        Preferences.setStringPref(getApplicationContext(),Preferences.user_dob,data.getString("dob"));
                        Preferences.setStringPref(getApplicationContext(),Preferences.user_addres,data.getString("address"));
                        Preferences.setStringPref(getApplicationContext(),Preferences.user_gender,data.getString("gender"));
                        Preferences.setStringPref(getApplicationContext(),Preferences.user_token,data.getString("token"));

                        Preferences.setStringPref(getApplicationContext(),Preferences.user_log_in,"login");

                        addressArray.add(data.getString("address"));

                        Gson gson = new Gson();

                        String s = gson.toJson(addressArray);

                        Preferences.setStringPref(mActivity,Preferences.user_address_array,s);


                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
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
                params.put("token", token);
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

                    if (login_edt_password.getText().toString().length() >= 8) {

                        return true;
                    } else {
                        login_edt_password.setError("Password length 8 and above");
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