package com.medicare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicare.adapter.CartAdapter;
import com.medicare.adapter.SavePreAdapter;
import com.medicare.interfaces.OnCartItemDelete;
import com.medicare.interfaces.OnPresciptionClickListener;
import com.medicare.model.Cart;
import com.medicare.utils.Constants;
import com.medicare.utils.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SavePrescriptionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SavePrescriptionActivit";
    Activity mActivity;
    ImageView iv_back;
    RecyclerView rv_save_list;
    SavePreAdapter mSavePreAdapter;

    ArrayList<String> save_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_prescription);
        mActivity = SavePrescriptionActivity.this;
        findViews();
        init();
        setListener();
    }

    private void findViews() {
        iv_back = findViewById(R.id.iv_back);
        rv_save_list = findViewById(R.id.rv_save_list);
    }

    private void init() {
        String serializedObject = Preferences.getStringPref(mActivity,Preferences.save_uri);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            save_list = gson.fromJson(serializedObject, type);
        }

        if(save_list == null){
            save_list = new ArrayList<>();
        }

        for (int i = 0; i < save_list.size(); i++) {
            Log.e(TAG, "init: "+ save_list.get(i));
        }

        mSavePreAdapter = new SavePreAdapter(getApplicationContext(), save_list, new OnPresciptionClickListener() {
            @Override
            public void onSavePreClick(int pos) {
                Constants.selected_image = true;
                Constants.select_image_url = save_list.get(pos);
                finish();
            }
        });
        GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_save_list.setLayoutManager(mLayoutManager);
        rv_save_list.setItemAnimator(new DefaultItemAnimator());
        rv_save_list.setAdapter(mSavePreAdapter);

    }

    private void setListener() {
        iv_back.setOnClickListener(this);
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
