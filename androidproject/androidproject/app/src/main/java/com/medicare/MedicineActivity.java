package com.medicare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medicare.adapter.MedicineAdapter;
import com.medicare.interfaces.OnMedicineItemClick;
import com.medicare.model.Medicine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MedicineActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private static final String TAG = "MedicineActivity";
    ImageView iv_back;
    RecyclerView list_medicine;
    ProgressDialog progressDialog;
    private RequestQueue mRequestQueue;
    private static final String URL_FOR_LOGIN = "http://159.89.169.152/medical/api/getMedicine";

    ArrayList<Medicine> medicineArrayList = new ArrayList<>();
    private MedicineAdapter mAdapter;

    EditText et_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);
        mActivity = MedicineActivity.this;
        findViews();
        init();
        setListener();
    }

    private void findViews() {
        iv_back = findViewById(R.id.iv_back);
        list_medicine = findViewById(R.id.list_medicine);
        et_search = findViewById(R.id.et_search);
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mRequestQueue = Volley.newRequestQueue(mActivity);

        getMedicineDataFromAPI();


        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setListener() {
        iv_back.setOnClickListener(this);
    }

    private void getMedicineDataFromAPI() {
        progressDialog.setMessage("Logging you in...");
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
                            Medicine medicine = new Medicine();
                            medicine.setId(j1.getInt("id"));
                            medicine.setName(j1.getString("name"));
                            medicine.setName(j1.getString("name"));
                            medicine.setDes(j1.getString("description"));

                            try {
                                medicine.setPrice(j1.getInt("price"));
                                medicine.setQty(j1.getInt("qty"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            medicineArrayList.add(medicine);
                        }

                        Log.e(TAG, "onResponse: " + medicineArrayList );
                        Collections.reverse(medicineArrayList);

                        mAdapter = new MedicineAdapter(getApplicationContext(), medicineArrayList);
                        GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                        //RecyclerView. mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        list_medicine.setLayoutManager(mLayoutManager);
                        list_medicine.setItemAnimator(new DefaultItemAnimator());
                        list_medicine.setAdapter(mAdapter);

                        Log.e(TAG, "onResponse: " + medicineArrayList);

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
