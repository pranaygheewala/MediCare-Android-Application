package com.medicare.order;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medicare.R;
import com.medicare.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private static String URL_FOR_ORDER = "http://159.89.169.152/medical/api/userOrders?user_id=";

    ArrayList<Order.Datum> arrayList = new ArrayList<>();
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
    }

    private void init() {
        String userid = Preferences.getStringPref(getApplicationContext(),Preferences.user_id);
        //String userid = "2";

        URL_FOR_ORDER = URL_FOR_ORDER + userid;

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mRequestQueue = Volley.newRequestQueue(mActivity);

        getMedicineDataFromAPI();
    }

    private void setListener() {
        iv_back.setOnClickListener(this);
    }

    private void getMedicineDataFromAPI() {
        progressDialog.setMessage("Get Order data...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_FOR_ORDER, new Response.Listener<String>() {

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


                        orderAdapter = new OrderAdapter(getApplicationContext(), arrayList);
                       // GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        order_list.setLayoutManager(mLayoutManager);
                        order_list.setItemAnimator(new DefaultItemAnimator());
                        order_list.setAdapter(orderAdapter);

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
