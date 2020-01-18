package com.medicare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicare.adapter.CartAdapter;
import com.medicare.adapter.CartNewAdapter;
import com.medicare.interfaces.OnCartItemDelete;
import com.medicare.model.Cart;
import com.medicare.utils.Constants;
import com.medicare.utils.Preferences;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PaymentActivity";
    private Activity mActivity;
    ImageView iv_back,iv_done,iv_pre;

    LinearLayout ll_cart,ll_prescription,ll_done;
    Button btn_pay;
    String mode,address,image_uri;
    ArrayList<Cart> cartArrayList = new ArrayList<>();
    CartNewAdapter mCartNewAdapter;
    RecyclerView cart_data;
    TextView tv_total_pay,tv_address,tv_address2;
    ProgressDialog progressDialog;
    private RequestQueue mRequestQueue;
    private static final String URL_FOR_ORDER = "http://159.89.169.152/medical/api/addOrder";

    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mActivity = PaymentActivity.this;
        findViews();
        init();
        setListener();
    }

    private void findViews() {
        iv_back = findViewById(R.id.iv_back);
        iv_done = findViewById(R.id.iv_done);
        ll_cart = findViewById(R.id.ll_cart);
        ll_prescription = findViewById(R.id.ll_prescription);
        ll_done = findViewById(R.id.ll_done);
        btn_pay = findViewById(R.id.btn_pay);
        cart_data = findViewById(R.id.cart_data);
        tv_total_pay = findViewById(R.id.tv_total_pay);
        tv_address = findViewById(R.id.tv_address);
        tv_address2 = findViewById(R.id.tv_address2);
        iv_pre = findViewById(R.id.iv_pre);
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mRequestQueue = Volley.newRequestQueue(mActivity);

        Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.iv_done)
                .into(iv_done);

        ll_done.setVisibility(View.GONE);

        mode = getIntent().getStringExtra("mode");
        address = getIntent().getStringExtra("address");
        image_uri = getIntent().getStringExtra("image_uri");


        if(mode.equals("simple")){
            ll_cart.setVisibility(View.VISIBLE);
            ll_prescription.setVisibility(View.GONE);
            btn_pay.setText(getString(R.string.payment_order1));

            tv_address.setText("Address : "+ address);
            String serializedObject = Preferences.getStringPref(mActivity,"cartdata");
            if (serializedObject != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Cart>>() {
                }.getType();
                cartArrayList = gson.fromJson(serializedObject, type);
            }

            if(cartArrayList == null){
                cartArrayList = new ArrayList<>();
            }

            int total_pay = 0;
            for (int i = 0; i < cartArrayList.size(); i++) {
                total_pay += cartArrayList.get(i).getTotal();
            }
            tv_total_pay.setText("Total amount: " + String.valueOf(total_pay));

            mCartNewAdapter = new CartNewAdapter(getApplicationContext(), cartArrayList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            cart_data.setLayoutManager(mLayoutManager);
            cart_data.setItemAnimator(new DefaultItemAnimator());
            cart_data.setAdapter(mCartNewAdapter);

        }else if(mode.equals("advance")){
            ll_cart.setVisibility(View.GONE);
            ll_prescription.setVisibility(View.VISIBLE);
            btn_pay.setText(getString(R.string.payment_order2));
            tv_address2.setText("Address : "+ address);
            Picasso.with(mActivity).load(Uri.parse(image_uri)).into(iv_pre);

        }else{
            Toast.makeText(mActivity, "Something want to wrong", Toast.LENGTH_SHORT).show();
        }

    }

    private void setListener() {
        iv_back.setOnClickListener(this);
        btn_pay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_pay:
                if(mode.equals("simple")){
                    String userid = Preferences.getStringPref(getApplicationContext(),Preferences.user_id);

                    callSimpleOrderAPI(userid,String.valueOf(cartArrayList.get(i).getProduct_id()),String.valueOf(cartArrayList.get(i).getQty()));


                }else{
                    String userid = Preferences.getStringPref(getApplicationContext(),Preferences.user_id);
                    //String userid = "2";
                    callAdvanceOrderAPI(userid,image_uri);
                }
                break;
        }
    }

    private void callSimpleOrderAPI(final String userid, final String productid,final String qty) {
        String cancel_req_tag = "login";
        progressDialog.setMessage("Order Progress...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_ORDER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    String responseMessage = jObj.getString("ResponseMessage");

                    if (responseMessage.equals("Order request sent")) {

                        i++;
                        if(i<cartArrayList.size()) {
                            callSimpleOrderAPI(userid, String.valueOf(cartArrayList.get(i).getProduct_id()), String.valueOf(cartArrayList.get(i).getQty()));
                        }else {
                            Preferences.setStringPref(getApplicationContext(), "cartdata", "");
                            Toast.makeText(mActivity, "Order request sent", Toast.LENGTH_SHORT).show();
                            ll_cart.setVisibility(View.GONE);
                            ll_prescription.setVisibility(View.GONE);
                            ll_done.setVisibility(View.VISIBLE);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();

                                    if(AddressActivity.mActivity != null){
                                        AddressActivity.mActivity.finish();
                                    }
                                    if(AddPrescriptionActivity.mActivity != null){
                                        AddPrescriptionActivity.mActivity.finish();
                                    }
                                    if(CartActivity.mActivity != null){
                                        CartActivity.mActivity.finish();
                                    }
                                }
                            },2000);
                        }
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
                params.put("user_id", userid);
                params.put("product_id[]", productid);
                params.put("qty[]", qty);
                return params;
            }
        };
        // Adding request to request queue
        mRequestQueue.add(strReq);
    }


    private void callAdvanceOrderAPI(final String userid,final String image_uri) {

        progressDialog.setMessage("Order process...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_ORDER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    String responseMessage = jObj.getString("ResponseMessage");

                    if (responseMessage.equals("Order request sent")) {

                        Toast.makeText(mActivity, "Order request sent", Toast.LENGTH_SHORT).show();
                        ll_prescription.setVisibility(View.GONE);
                        ll_done.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();

                                if(AddressActivity.mActivity != null){
                                    AddressActivity.mActivity.finish();
                                }
                                if(AddPrescriptionActivity.mActivity != null){
                                    AddPrescriptionActivity.mActivity.finish();
                                }
                                if(CartActivity.mActivity != null){
                                    CartActivity.mActivity.finish();
                                }

                            }
                        },2000);

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
                params.put("user_id", userid);
                params.put("qty[]", String.valueOf("1"));
                params.put("image[]", image_uri);
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
