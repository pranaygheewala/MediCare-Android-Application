package com.adminmedicare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OrderMaintainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "OrderMaintainActivity";
    private Activity mActivity;
    ImageView iv_back;
    RecyclerView order_list;
    OrderAdapter orderAdapter;
    ProgressDialog progressDialog;
    private RequestQueue mRequestQueue;
    private static final String URL_FOR_LOGIN = "http://159.89.169.152/medical/api/orders";
    private static final String URL_FOR_ACCEPT = "http://159.89.169.152/medical/api/orderAction";

    ArrayList<Order.Datum> arrayList = new ArrayList<>();

    SwipeRefreshLayout refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_maintain);
        mActivity = OrderMaintainActivity.this;
        findVIews();
        init();
        setListener();
    }

    private void findVIews() {
        iv_back = findViewById(R.id.iv_back);
        order_list = findViewById(R.id.order_list);
        refresh = findViewById(R.id.refresh);
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mRequestQueue = Volley.newRequestQueue(mActivity);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMedicineDataFromAPI();
            }
        });

        getMedicineDataFromAPI();
    }

    private void setListener() {
        iv_back.setOnClickListener(this);
    }

    private void getMedicineDataFromAPI() {
        arrayList.clear();
        progressDialog.setMessage("Get Order data...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_FOR_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jObj = new JSONObject(response);
                    String responseMessage = jObj.getString("ResponseMessage");

                    if (responseMessage.equals("success")) {
                        JSONArray jsonArray = jObj.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject j1 = (JSONObject) jsonArray.get(i);
                            Order.Datum data = new Order.Datum();

                            data.setId(j1.getInt("id"));
                            data.setOrder_id(j1.getString("order_id"));
                            data.setUser_id(j1.getInt("user_id"));
                            data.setStatus(j1.getString("status"));
                            data.setCreated_at(j1.getString("created_at"));

                            arrayList.add(data);
                        }

                        Collections.reverse(arrayList);

                        orderAdapter = new OrderAdapter(getApplicationContext(), arrayList, new OnButtonActionListener() {
                            @Override
                            public void onClick(String orderid,String status) {
                                //Toast.makeText(mActivity, ""+ orderid, Toast.LENGTH_SHORT).show();
                                callAcceptAPI(orderid,status);
                            }
                        });
                       // GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        order_list.setLayoutManager(mLayoutManager);
                        order_list.setItemAnimator(new DefaultItemAnimator());
                        order_list.setAdapter(orderAdapter);

                        if(refresh.isRefreshing()){
                            refresh.setRefreshing(false);
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


    private void callAcceptAPI( final String orderid,final String status) {
        String cancel_req_tag = "login";
        progressDialog.setMessage("Connecting with server...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_ACCEPT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.e(TAG, "onResponse: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    String responseMessage = jObj.getString("ResponseMessage");

                    if (responseMessage.equals("Order accepted")) {
                        Log.e(TAG, "onResponse: " );

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getMedicineDataFromAPIforRefreah();
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
                params.put("action", status);
                params.put("order_id", orderid);
                return params;
            }
        };
        // Adding request to request queue
        mRequestQueue.add(strReq);
    }


    private void getMedicineDataFromAPIforRefreah() {
        progressDialog.setMessage("Refreshing...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_FOR_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try {
                    arrayList.clear();

                    JSONObject jObj = new JSONObject(response);
                    String responseMessage = jObj.getString("ResponseMessage");

                    if (responseMessage.equals("success")) {
                        JSONArray jsonArray = jObj.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject j1 = (JSONObject) jsonArray.get(i);
                            Order.Datum data = new Order.Datum();

                            data.setId(j1.getInt("id"));

                            data.setOrder_id(j1.getString("order_id"));
                            data.setUser_id(j1.getInt("user_id"));
                            data.setStatus(j1.getString("status"));
                            data.setCreated_at(j1.getString("created_at"));

                            arrayList.add(data);
                        }

                        Collections.reverse(arrayList);

                        orderAdapter.notifyDataSetChanged();


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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
