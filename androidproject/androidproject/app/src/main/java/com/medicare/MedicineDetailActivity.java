package com.medicare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicare.model.Cart;
import com.medicare.utils.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MedicineDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MedicineDetailActivity";
    private Activity mActivity;

    TextView tv_name, tv_des, tv_price, tv_qty;
    ImageView iv_minus, iv_plus, iv_back;
    Button btn_cart;

    String name;
    String des;
    int id;
    int price;
    int qtyMain;
    ArrayList<Cart> cartArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);
        mActivity = MedicineDetailActivity.this;
        findViews();
        init();
        setListener();
    }

    private void findViews() {
        tv_name = findViewById(R.id.tv_name);
        tv_des = findViewById(R.id.tv_des);
        tv_price = findViewById(R.id.tv_price);
        tv_qty = findViewById(R.id.tv_qty);
        iv_minus = findViewById(R.id.iv_minus);
        iv_plus = findViewById(R.id.iv_plus);
        btn_cart = findViewById(R.id.btn_cart);
        iv_back = findViewById(R.id.iv_back);
    }

    private void init() {
        name = getIntent().getStringExtra("name");
        des = getIntent().getStringExtra("des");
        id = getIntent().getIntExtra("id", 1);
        price = getIntent().getIntExtra("price", 0);
        qtyMain = getIntent().getIntExtra("qty", 0);

        tv_name.setText(name);
        tv_des.setText(des);
        tv_price.setText(String.valueOf(price));

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
    }

    private void setListener() {
        iv_minus.setOnClickListener(this);
        iv_plus.setOnClickListener(this);
        btn_cart.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cart:
                Cart cart = new Cart();
                cart.setProduct_id(id);
                cart.setName(name);
                cart.setDes(des);
                cart.setPrice(price);
                cart.setQty(Integer.parseInt(tv_qty.getText().toString()));
                int total = price * Integer.parseInt(tv_qty.getText().toString());
                cart.setTotal(total);

                cartArrayList.add(cart);

                Gson gson = new Gson();
                String json = gson.toJson(cartArrayList);

                Preferences.setStringPref(mActivity,"cartdata",json);

                startActivity(new Intent(MedicineDetailActivity.this,CartActivity.class));
                break;
            case R.id.iv_plus:
                int qty = Integer.parseInt(tv_qty.getText().toString());
                int newqty = qty + 1;
                if(newqty <= qtyMain) {
                    tv_qty.setText(String.valueOf(newqty));
                }else{
                    Toast.makeText(mActivity, "Out of stock", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_minus:
                int qty11 = Integer.parseInt(tv_qty.getText().toString());
                if (qty11 > 1) {
                    int n = qty11 - 1;
                    tv_qty.setText(String.valueOf(n));
                } else {
                    Toast.makeText(mActivity, "Quantity not less then 1", Toast.LENGTH_SHORT).show();
                }
                break;
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
