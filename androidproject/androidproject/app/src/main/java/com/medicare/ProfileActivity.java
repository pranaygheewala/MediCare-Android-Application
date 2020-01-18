package com.medicare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.medicare.utils.Preferences;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";
    Activity mActivity;
    ImageView iv_back;
    EditText signUp_edt_name, signUp_edt_email, signUp_edt_dob, signUp_edt_address;
    RadioButton radio_btnMale, radio_btnFemale;
    Button btn_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViews();
        init();
        setListener();
    }

    private void findViews() {
        iv_back = findViewById(R.id.iv_back);
        signUp_edt_name = findViewById(R.id.signUp_edt_name);
        signUp_edt_email = findViewById(R.id.signUp_edt_email);
        signUp_edt_email.setEnabled(false);
        signUp_edt_dob = findViewById(R.id.signUp_edt_dob);
        signUp_edt_address = findViewById(R.id.signUp_edt_address);
        radio_btnMale = findViewById(R.id.radio_btnMale);
        radio_btnFemale = findViewById(R.id.radio_btnFemale);
        btn_update = findViewById(R.id.btn_update);
    }

    private void init() {
        String name = Preferences.getStringPref(getApplicationContext(),Preferences.user_name);
        String email = Preferences.getStringPref(getApplicationContext(),Preferences.user_email);
        String dob = Preferences.getStringPref(getApplicationContext(),Preferences.user_dob);
        String address = Preferences.getStringPref(getApplicationContext(),Preferences.user_addres);
        String gender = Preferences.getStringPref(getApplicationContext(),Preferences.user_gender);

        signUp_edt_name.setText(name);
        signUp_edt_email.setText(email);
        signUp_edt_dob.setText(dob);
        signUp_edt_address.setText(address);
        if(gender.equals("Male")){
            radio_btnMale.setChecked(true);
        }else{
            radio_btnFemale.setChecked(true);
        }

    }

    private void setListener() {
        iv_back.setOnClickListener(this);
        btn_update.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_update:
                if(verify()){
                    update();
                }
                break;
        }
    }

    private void update() {
        Preferences.setStringPref(getApplicationContext(),Preferences.user_name,signUp_edt_name.getText().toString());
        Preferences.setStringPref(getApplicationContext(),Preferences.user_dob,signUp_edt_dob.getText().toString());
        Preferences.setStringPref(getApplicationContext(),Preferences.user_addres,signUp_edt_address.getText().toString());
        if(radio_btnMale.isChecked()) {
            Preferences.setStringPref(getApplicationContext(), Preferences.user_gender,"Male" );
        }else {
            Preferences.setStringPref(getApplicationContext(), Preferences.user_gender,"Female" );
        }
    }

    private boolean verify() {
        if (signUp_edt_name.getText().toString().trim().length() > 0) {

            if (signUp_edt_email.getText().toString().trim().length() > 0) {

                if (isValidEmail(signUp_edt_email.getText().toString())) {

                    if (validateDate(signUp_edt_dob.getText().toString())) {

                        if (signUp_edt_address.getText().toString().trim().length() > 0) {

                            return true;
                        }else{
                            signUp_edt_address.setError("Enter Valid BirthDate");
                            return false;
                        }
                    } else {
                        signUp_edt_dob.setError("Enter Valid BirthDate");
                        return false;
                    }

                } else {
                    signUp_edt_email.setError("Enter Valid Email");
                    return false;
                }

            } else {
                signUp_edt_email.setError("Enter Email");
                return false;
            }
        } else {
            signUp_edt_name.setError("Enter Name");
            return false;
        }
    }

    static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }


    public boolean validateDate(String strDate) {
        if (strDate.trim().equals("")) {
            signUp_edt_dob.setError("Enter BirthDate");
            return false;
        } else {
            SimpleDateFormat sdfrmt = new SimpleDateFormat("dd/MM/yyyy");
            sdfrmt.setLenient(false);
            try {
                Date javaDate = sdfrmt.parse(strDate);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
