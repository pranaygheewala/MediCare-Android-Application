package com.adminmedicare;

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

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private Activity mActivity;
    ImageView iv_back;
    Button btn_show_orders;
    EditText et_medicine_name, et_medicine_des, et_medicine_price, et_medicine_qty;
    ProgressDialog progressDialog;
    private RequestQueue mRequestQueue;
    private static final String URL_FOR_LOGIN = "http://159.89.169.152/medical/api/addMedicine";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = MainActivity.this;
        findViews();
        init();
        setListener();
    }

    private void findViews() {
        iv_back = findViewById(R.id.iv_back);
        btn_show_orders = findViewById(R.id.btn_show_orders);
        et_medicine_name = findViewById(R.id.et_medicine_name);
        et_medicine_des = findViewById(R.id.et_medicine_des);
        et_medicine_price = findViewById(R.id.et_medicine_price);
        et_medicine_qty = findViewById(R.id.et_medicine_qty);
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mRequestQueue = Volley.newRequestQueue(mActivity);
    }

    private void setListener() {
        iv_back.setOnClickListener(this);
        btn_show_orders.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_show_orders:
                String s = et_medicine_name.getText().toString();
                String s1 = et_medicine_des.getText().toString();
                String s2 = et_medicine_price.getText().toString();
                String s3 = et_medicine_qty.getText().toString();
                int d1 = 0;
                int qty = 0;

                if (s.equals("")) {
                    et_medicine_name.setError("Enter Medicine Name");
                } else if (s1.equals("")) {
                    et_medicine_des.setError("Enter Medicine Description");
                } else if (s2.equals("")) {

                    et_medicine_price.setError("Enter Medicine price");
                } else if (s3.equals("")) {
                    et_medicine_qty.setError("Enter Medicine Quantity");
                } else {
                    d1 = Integer.parseInt(s2);
                    qty = Integer.parseInt(s3);

                    if (d1 < 1) {
                        et_medicine_price.setError("Enter Medicine price morw then 1");
                    } else if (qty < 1) {
                        et_medicine_qty.setError("Enter Medicine Quantity more then 1");
                    } else {
                        callAddMedicineAPI(s, s1, d1, qty);
                    }
                }
                break;
        }
    }

    private void callAddMedicineAPI(final String name, final String des, final int price, final int qty) {
        progressDialog.setMessage("Adding Medicine...");
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
                        Toast.makeText(mActivity, "Medicine added successfully ", Toast.LENGTH_SHORT).show();
                        et_medicine_name.setText("");
                        et_medicine_des.setText("");
                        et_medicine_price.setText("");
                        et_medicine_qty.setText("");
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
                params.put("description", des);
                params.put("price", String.valueOf(price));
                params.put("qty", String.valueOf(qty));
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
