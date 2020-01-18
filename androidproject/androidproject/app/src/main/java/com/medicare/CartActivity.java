package com.medicare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicare.adapter.CartAdapter;
import com.medicare.adapter.MedicineAdapter;
import com.medicare.interfaces.OnCartItemDelete;
import com.medicare.interfaces.OnMedicineItemClick;
import com.medicare.model.Cart;
import com.medicare.model.Medicine;
import com.medicare.utils.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CartActivity";
    public static Activity mActivity;
    ArrayList<Cart> cartArrayList = new ArrayList<>();
    ImageView iv_back;
    RecyclerView rv_cart;
    TextView tv_total_payment,tv_noData;
    CartAdapter mCartAdapter;
    Button btn_confirm_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mActivity = CartActivity.this;
        findVIews();
        init();
        setListener();
    }

    private void findVIews() {
        iv_back = findViewById(R.id.iv_back);
        rv_cart = findViewById(R.id.rv_cart);
        tv_total_payment = findViewById(R.id.tv_total_payment);
        tv_noData  = findViewById(R.id.tv_noData);
        btn_confirm_order = findViewById(R.id.btn_confirm_order);
    }

    private void init() {
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
        mCartAdapter = new CartAdapter(getApplicationContext(), cartArrayList, new OnCartItemDelete() {
            @Override
            public void itemDelete(int pos) {

                cartArrayList.remove(pos);
                Gson gson = new Gson();
                String json = gson.toJson(cartArrayList);

                Preferences.setStringPref(getApplicationContext(),"cartdata",json);

                mCartAdapter.notifyItemRemoved(pos);

                int total_pay = 0;
                for (int i = 0; i < cartArrayList.size(); i++) {
                    total_pay += cartArrayList.get(i).getTotal();
                }
                tv_total_payment.setText("Total Payment: " + String.valueOf(total_pay));

                if(cartArrayList.size() == 0){
                    tv_noData.setVisibility(View.VISIBLE);
                    tv_total_payment.setVisibility(View.GONE);
                }

                Toast.makeText(mActivity, "item deleted", Toast.LENGTH_SHORT).show();
            }
        });
        //GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_cart.setLayoutManager(mLayoutManager);
        rv_cart.setItemAnimator(new DefaultItemAnimator());
        rv_cart.setAdapter(mCartAdapter);


        int total_pay = 0;
        for (int i = 0; i < cartArrayList.size(); i++) {
            total_pay += cartArrayList.get(i).getTotal();
        }
        tv_total_payment.setText("Total Payment: " + String.valueOf(total_pay));

        if(cartArrayList.size() == 0){
            tv_noData.setVisibility(View.VISIBLE);
            tv_total_payment.setVisibility(View.GONE);
            btn_confirm_order.setVisibility(View.GONE);
        }

    }

    private void setListener() {
        iv_back.setOnClickListener(this);
        btn_confirm_order.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_confirm_order:

                if (cartArrayList.size() > 0) {

                    Intent intent = new Intent(CartActivity.this,AddressActivity.class);
                    intent.putExtra("mode","simple");
                    startActivity(intent);
                }else {
                    Toast.makeText(mActivity, "NO cart item available", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
