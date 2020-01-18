package com.adminmedicare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrdarDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "OrdarDetailActivity";
    Activity mActivity;
    ImageView iv_back;
    ProgressDialog progressDialog;
    private RequestQueue mRequestQueue;
    private String ORDAR_DETAIL = "http://159.89.169.152/medical/api/getOrder?order_id=";

    int id;

    TextView tv_orderid,tv_name, tv_email, tv_address, tv_status, tv_Create_time, image_detail, medicine_name, medicine_des, tv_price, tv_qty;
    ImageView iv_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordar_detail);
        mActivity = OrdarDetailActivity.this;
        findVIews();
        init();
        setListener();
    }

    private void findVIews() {
        iv_back = findViewById(R.id.iv_back);

        tv_orderid = findViewById(R.id.tv_orderid);
        tv_name = findViewById(R.id.tv_name);
        tv_email = findViewById(R.id.tv_email);
        tv_address = findViewById(R.id.tv_address);
        tv_status = findViewById(R.id.tv_status);
        tv_Create_time = findViewById(R.id.tv_Create_time);
        image_detail = findViewById(R.id.image_detail);
        medicine_name = findViewById(R.id.medicine_name);
        medicine_des = findViewById(R.id.medicine_des);
        tv_price = findViewById(R.id.tv_price);
        tv_qty = findViewById(R.id.tv_qty);
        iv_image = findViewById(R.id.iv_image);

    }

    private void init() {
        id = getIntent().getIntExtra("id", 1);

        Log.e(TAG, "init: get from intent" + id);

        ORDAR_DETAIL = ORDAR_DETAIL + id;
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mRequestQueue = Volley.newRequestQueue(mActivity);
        getMedicineDataFromAPI();
    }

    private void getMedicineDataFromAPI() {
        progressDialog.setMessage("Get Order data...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                ORDAR_DETAIL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jObj = new JSONObject(response);
                    String responseMessage = jObj.getString("ResponseMessage");

                    if (responseMessage.equals("success")) {
                        JSONObject jDATA = jObj.getJSONObject("data");

                        Log.e(TAG, "onResponse: " + jDATA.getString("id"));
                        Log.e(TAG, "onResponse: " + jDATA.getString("order_id"));
                        Log.e(TAG, "onResponse: " + jDATA.getString("user_id"));
                        Log.e(TAG, "onResponse: " + jDATA.getString("status"));
                        Log.e(TAG, "onResponse: " + jDATA.getString("created_at"));
                        Log.e(TAG, "onResponse: " + jDATA.getString("items"));

                        tv_orderid.setText("Order ID: "+jDATA.getString("order_id"));

                        JSONObject jUSER = jDATA.getJSONObject("user_detail");

                        Log.e(TAG, "onResponse: " + jUSER.getString("id"));
                        Log.e(TAG, "onResponse: " + jUSER.getString("name"));
                        Log.e(TAG, "onResponse: " + jUSER.getString("email"));
                        Log.e(TAG, "onResponse: " + jUSER.getString("address"));

                        tv_name.setText("Customer Name: "+jUSER.getString("name"));
                        tv_email.setText("Email address: "+jUSER.getString("email"));
                        tv_address.setText("Address: "+jUSER.getString("address"));

                        tv_status. setText("Order status : " + jDATA.getString("status"));
                        tv_Create_time.setText("Order at : " + jDATA.getString("created_at"));

                        JSONArray jITEM = jDATA.getJSONArray("items");

                        if (jITEM.length() > 0) {

                            JSONObject jITEMObject = (JSONObject) jITEM.get(0);

                            Log.e(TAG, "onResponse: " + jITEMObject.getString("id"));
                            Log.e(TAG, "onResponse: " + jITEMObject.getString("order_id"));
                            Log.e(TAG, "onResponse: " + jITEMObject.getString("product_id"));
                            Log.e(TAG, "onResponse: " + jITEMObject.getString("image"));
                            Log.e(TAG, "onResponse: " + jITEMObject.getString("qty"));
                            Log.e(TAG, "onResponse: " + jITEMObject.getString("medicine_details"));

                            if(!jITEMObject.getString("image").equals("")) {
                                Picasso.with(mActivity).load(Uri.parse(jITEMObject.getString("image"))).placeholder(R.drawable.ic_launcher_background).into(iv_image);
                            }else{
                                iv_image.setVisibility(View.GONE);
                                image_detail.setText("Payment : done");
                            }


                            if (!jITEMObject.getString("medicine_details").equals(null)) {

                                JSONObject jMedicine = jITEMObject.getJSONObject("medicine_details");

                                Log.e(TAG, "onResponse: " + jMedicine.getString("name"));
                                Log.e(TAG, "onResponse: " + jMedicine.getString("description"));
                                Log.e(TAG, "onResponse: " + jMedicine.getString("price"));
                                Log.e(TAG, "onResponse: " + jMedicine.getString("qty"));

                                medicine_name.setText("Medicine Name: " + jMedicine.getString("name"));
                                medicine_des.setText("Description: " + jMedicine.getString("description"));
                                tv_price.setText("MRP: " + jMedicine.getString("price"));
                                tv_qty.setText("Quantity: " + jMedicine.getString("qty"));
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
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

    private void setListener() {
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
