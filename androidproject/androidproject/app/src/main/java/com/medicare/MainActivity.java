package com.medicare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.medicare.reminderHelper.ReminderActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView search_medicine, near_store, upload_prescription, set_reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search_medicine = findViewById(R.id.search_medicine);
        near_store = findViewById(R.id.near_store);
        upload_prescription = findViewById(R.id.upload_prescription);
        set_reminder = findViewById(R.id.set_reminder);

        search_medicine.setOnClickListener(this);
        near_store.setOnClickListener(this);
        upload_prescription.setOnClickListener(this);
        set_reminder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_medicine:
                Toast.makeText(this, "Working", Toast.LENGTH_SHORT).show();
                break;
            case R.id.near_store:
                startActivity(new Intent(MainActivity.this,NearMadicalActivity.class));
                break;
            case R.id.upload_prescription:
                startActivity(new Intent(MainActivity.this,AddPrescriptionActivity.class));
                break;
            case R.id.set_reminder:
                startActivity(new Intent(MainActivity.this, ReminderActivity.class));
                break;
        }
    }
}
