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
import android.widget.ImageView;
import android.widget.Toast;

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

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private static final String TAG = "ForgotPasswordActivity";
    EditText signUp_edt_email;
    ImageView signUp_iv_back;
    Button btn_get_password;
    ProgressDialog progressDialog;
    private RequestQueue mRequestQueue;
    private static final String URL_FOR_LOGIN = "http://159.89.169.152/medical/api/forgotPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mActivity = ForgotPasswordActivity.this;
        findViews();
        init();
        setListener();
    }

    private void findViews() {
        signUp_edt_email = findViewById(R.id.signUp_edt_email);
        signUp_iv_back = findViewById(R.id.signUp_iv_back);
        btn_get_password = findViewById(R.id.btn_get_password);
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mRequestQueue = Volley.newRequestQueue(mActivity);
    }

    private void setListener() {
        btn_get_password.setOnClickListener(this);
        signUp_iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_password:
                if (verify()) {
                    callForgotPasswordAPI(signUp_edt_email.getText().toString());
                }
                break;
            case R.id.signUp_iv_back:
                onBackPressed();
                break;
        }
    }

    private void callForgotPasswordAPI(final String email) {
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
                    String ResponseCode = jObj.getString("ResponseCode");

                    if (ResponseCode.equals("1")) {
                        Toast.makeText(mActivity, responseMessage + "Check it now", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mActivity, "Something went to wrong or check your email", Toast.LENGTH_SHORT).show();
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }
        };
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

    private boolean verify() {
        if (signUp_edt_email.getText().toString().trim().length() > 0) {
            if (isValidEmail(signUp_edt_email.getText().toString())) {
                return true;
            } else {
                signUp_edt_email.setError("Enter Valid Email");
                return false;
            }
        } else {
            signUp_edt_email.setError("Enter Email");
            return false;
        }
    }

    static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
