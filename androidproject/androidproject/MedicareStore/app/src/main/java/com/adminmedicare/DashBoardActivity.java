package com.adminmedicare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout addMedicine,inventory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        init();

    }

    private void init(){

        addMedicine = findViewById(R.id.add_medicine);
        inventory = findViewById(R.id.inventory);
        addMedicine.setOnClickListener(this);
        inventory.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if(view == addMedicine){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);

        }else if(view == inventory){
            Intent intent = new Intent(this,OrderMaintainActivity.class);
            startActivity(intent);

        }
    }
}
