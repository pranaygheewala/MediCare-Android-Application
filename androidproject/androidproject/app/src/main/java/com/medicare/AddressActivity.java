package com.medicare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicare.adapter.AddressAdapter;
import com.medicare.interfaces.OnItemClickListener;
import com.medicare.utils.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddressActivity";
    public static Activity mActivity;
    EditText address_edit_1, address_edit_2, address_edit_3, address_edit_4, address_edit_5, address_edit_6, address_edit_7;
    ImageView iv_back;
    Button btn_save,btn_select_continue;

    String imageUri;

    String address;

    String mode;
    RecyclerView rv_address_list;
    ArrayList<String> addressArray = new ArrayList<>();
    FloatingActionButton btn_add_address;
    LinearLayout ll_addressList,ll_addAddress;
    Boolean isAddressOpen = false;

    private AddressAdapter mAdapter;
    ArrayList<Boolean> addresscheckList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        mActivity = AddressActivity.this;
        findViews();
        init();
        setListener();
    }

    private void findViews() {
        iv_back = findViewById(R.id.iv_back);
        address_edit_1 = findViewById(R.id.address_edit_1);
        address_edit_2 = findViewById(R.id.address_edit_2);
        address_edit_3 = findViewById(R.id.address_edit_3);
        address_edit_4 = findViewById(R.id.address_edit_4);
        address_edit_5 = findViewById(R.id.address_edit_5);
        address_edit_6 = findViewById(R.id.address_edit_6);
        address_edit_7 = findViewById(R.id.address_edit_7);
        ll_addressList = findViewById(R.id.ll_addressList);
        ll_addAddress = findViewById(R.id.ll_addAddress);

        btn_save = findViewById(R.id.btn_save);
        btn_add_address = findViewById(R.id.btn_add_address);
        btn_select_continue = findViewById(R.id.btn_select_continue);

        rv_address_list = findViewById(R.id.rv_address_list);
    }

    private void init() {

        String serializedObject = Preferences.getStringPref(mActivity, Preferences.user_address_array);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            addressArray = gson.fromJson(serializedObject, type);
        }

        if (addressArray == null) {
            addressArray = new ArrayList<>();
        }


        mode = getIntent().getStringExtra("mode");
        imageUri = getIntent().getStringExtra("image_uri");

        Log.e("karan", "init: " + imageUri );

        setListData();
    }

    private void setListener() {
        iv_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_add_address.setOnClickListener(this);
        btn_select_continue.setOnClickListener(this);
    }

    private void setListData() {

        addresscheckList.clear();
        if(addressArray.size() > 0){
            for (int i = 0; i < addressArray.size(); i++) {
                if(i == 0){
                    addresscheckList.add(true);
                }else{
                    addresscheckList.add(false);
                }
            }
        }

        mAdapter = new AddressAdapter(getApplicationContext(), addressArray, addresscheckList, new OnItemClickListener() {
            @Override
            public void onItemClick(int number) {

                addresscheckList.clear();

                for (int i = 0; i < addressArray.size(); i++) {
                    if(i == number){
                        addresscheckList.add(true);
                    }else{
                        addresscheckList.add(false);
                    }
                }

                mAdapter.notifyDataSetChanged();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_address_list.setLayoutManager(mLayoutManager);
        rv_address_list.setItemAnimator(new DefaultItemAnimator());
        rv_address_list.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_add_address:
                isAddressOpen = true;
                ll_addAddress.setVisibility(View.VISIBLE);
                ll_addressList.setVisibility(View.GONE);
                break;
            case R.id.btn_select_continue:

                int val= 0;
                for (int i = 0; i < addresscheckList.size(); i++) {
                    if(addresscheckList.get(i) == true){
                        val = i;
                    }
                }
                address = addressArray.get(val);

                Intent intent = new Intent(AddressActivity.this,PaymentActivity.class);
                intent.putExtra("address",address);
                intent.putExtra("image_uri",imageUri);
                intent.putExtra("mode",mode);
                startActivity(intent);

                break;

            case R.id.btn_save:
                if (verify()) {

                    String Addaddress = address_edit_1.getText().toString() + " " +
                            address_edit_2.getText().toString() + "\n" +
                            address_edit_3.getText().toString() + " " +
                            address_edit_4.getText().toString() + " " +
                            address_edit_5.getText().toString() + "\n" +
                            address_edit_6.getText().toString() + " " +
                            address_edit_7.getText().toString() + " ";

                    /*Intent intent = new Intent(AddressActivity.this,PaymentActivity.class);
                    intent.putExtra("address",address);
                    intent.putExtra("image_uri",imageUri);
                    intent.putExtra("mode",mode);
                    startActivity(intent);*/

                    addressArray.add(Addaddress);

                    Gson gson = new Gson();
                    String s = gson.toJson(addressArray);
                    Preferences.setStringPref(mActivity, Preferences.user_address_array, s);

                    isAddressOpen = false;
                    ll_addAddress.setVisibility(View.GONE);
                    ll_addressList.setVisibility(View.VISIBLE);

                    setListData();
                }
                break;
        }
    }

    private boolean verify() {

        if (address_edit_1.getText().toString().trim().length() > 0) {

            if (address_edit_2.getText().toString().trim().length() > 0) {

                if (address_edit_3.getText().toString().trim().length() > 0) {

                    if (address_edit_4.getText().toString().trim().length() > 0) {

                        if (address_edit_5.getText().toString().trim().length() > 0) {

                            if (address_edit_6.getText().toString().trim().length() > 0) {

                                if (address_edit_7.getText().toString().trim().length() > 0) {

                                    return true;
                                } else {
                                    address_edit_7.setError("Enter Address");
                                    return false;
                                }

                            } else {
                                address_edit_6.setError("Enter Address");
                                return false;
                            }

                        } else {
                            address_edit_5.setError("Enter Address");
                            return false;
                        }

                    } else {
                        address_edit_4.setError("Enter Address");
                        return false;
                    }

                } else {
                    address_edit_3.setError("Enter Address");
                    return false;
                }

            } else {
                address_edit_2.setError("Enter Address");
                return false;
            }
        } else {
            address_edit_1.setError("Enter Address");
            return false;
        }

    }

    @Override
    public void onBackPressed() {
        if(isAddressOpen){
            isAddressOpen = false;
            ll_addressList.setVisibility(View.VISIBLE);
            ll_addAddress.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }
}
