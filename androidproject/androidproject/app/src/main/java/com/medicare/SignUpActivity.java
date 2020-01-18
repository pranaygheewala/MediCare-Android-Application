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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private static final String TAG = "SignUpActivity";
    EditText signUp_edt_name, signUp_edt_email, signUp_edt_dob, signUp_edt_address, signUp_edt_password;
    RadioGroup radioGroup;
    RadioButton radio_btnMale, radio_btnFemale;
    ImageView signUp_iv_back;
    Button btn_register;
    ProgressDialog progressDialog;
    private RequestQueue mRequestQueue;
    private static final String URL_FOR_LOGIN = "http://159.89.169.152/medical/api/register";
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mActivity = SignUpActivity.this;
        findViews();
        init();
        setListener();
    }

    private void findViews() {
        signUp_edt_name = findViewById(R.id.signUp_edt_name);
        signUp_edt_email = findViewById(R.id.signUp_edt_email);
        signUp_edt_dob = findViewById(R.id.signUp_edt_dob);
        signUp_edt_address = findViewById(R.id.signUp_edt_address);
        signUp_edt_password = findViewById(R.id.signUp_edt_password);

        radioGroup = findViewById(R.id.radioGroup);
        radio_btnMale = findViewById(R.id.radio_btnMale);
        radio_btnFemale = findViewById(R.id.radio_btnFemale);

        btn_register = findViewById(R.id.btn_register);

        signUp_iv_back = findViewById(R.id.signUp_iv_back);
    }

    private void init() {
        radio_btnMale.setChecked(true);
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
        btn_register.setOnClickListener(this);
        signUp_iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                if (verify()) {
                    String gender;

                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    if(selectedId == R.id.radio_btnFemale)
                        gender = "Female";
                    else
                        gender = "Male";

                    callSignUpAPI(signUp_edt_name.getText().toString(),
                            signUp_edt_email.getText().toString(),
                            signUp_edt_dob.getText().toString(),
                            gender,
                            signUp_edt_password.getText().toString(),
                            token,
                            signUp_edt_address.getText().toString());
                }
                break;
            case R.id.signUp_iv_back:
                onBackPressed();
                break;
        }
    }

    private void callSignUpAPI(final  String name, final String email,final String dob,final String gender, final String password, final String token,final String address) {
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

                        Log.e(TAG, "onResponse: " + data.getString("name") );
                        Log.e(TAG, "onResponse: " + data.getString("email") );
                        Log.e(TAG, "onResponse: " + data.getString("password") );
                        Log.e(TAG, "onResponse: " + data.getString("dob") );
                        Log.e(TAG, "onResponse: " + data.getString("gender") );
                        Log.e(TAG, "onResponse: " + data.getString("address") );
                        Log.e(TAG, "onResponse: " + data.getString("token") );

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
                params.put("name", name);
                params.put("email", email);
                params.put("dob", dob);
                params.put("gender", gender);
                params.put("password", password);
                params.put("token", token);
                params.put("address", address);
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

    private boolean verify() {
        if (signUp_edt_name.getText().toString().trim().length() > 0) {

            if (signUp_edt_email.getText().toString().trim().length() > 0) {

                if (isValidEmail(signUp_edt_email.getText().toString())) {

                    if (validateDate(signUp_edt_dob.getText().toString())) {

                        if (signUp_edt_address.getText().toString().trim().length() > 0) {

                            if (signUp_edt_password.getText().toString().trim().length() > 0) {

                                if(signUp_edt_password.getText().toString().length() >= 8){

                                    String str = signUp_edt_password.getText().toString();
                                    int upper = 0, lower = 0, number = 0, special = 0;

                                    for(int i = 0; i < str.length(); i++)
                                    {
                                        char ch = str.charAt(i);
                                        if (ch >= 'A' && ch <= 'Z')
                                            upper++;
                                        else if (ch >= 'a' && ch <= 'z')
                                            lower++;
                                        else if (ch >= '0' && ch <= '9')
                                            number++;
                                        else
                                            special++;
                                    }

                                    if(upper >= 1){

                                        if(special >= 1){

                                            if(number >= 1){

                                                return true;

                                            }else{
                                                signUp_edt_password.setError("Must contain one Number");
                                                return false;
                                            }
                                        }else{
                                            signUp_edt_password.setError("Must contain one Special latter");
                                            return false;
                                        }
                                    }else{
                                        signUp_edt_password.setError("Must contain one Upper case latter");
                                        return false;
                                    }

                                }else{
                                    signUp_edt_password.setError("Password length 8 and above");
                                    return false;
                                }

                            }else{
                                signUp_edt_password.setError("Enter Password");
                                return false;
                            }
                        }else{
                            signUp_edt_address.setError("Enter Valid BirthDate");
                            return false;
                        }
                    } else {
                        signUp_edt_dob.setError("Enter Valid BirthDate");
                        return false;
                    }

                } else {
                    signUp_edt_email.setError("Enter Valid Email");
                    return false;
                }

            } else {
                signUp_edt_email.setError("Enter Email");
                return false;
            }
        } else {
            signUp_edt_name.setError("Enter Name");
            return false;
        }
    }

    static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }


    public boolean validateDate(String strDate) {
        if (strDate.trim().equals("")) {
            signUp_edt_dob.setError("Enter BirthDate");
            return false;
        } else {
            SimpleDateFormat sdfrmt = new SimpleDateFormat("dd/MM/yyyy");
            sdfrmt.setLenient(false);
            try {
                Date javaDate = sdfrmt.parse(strDate);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
