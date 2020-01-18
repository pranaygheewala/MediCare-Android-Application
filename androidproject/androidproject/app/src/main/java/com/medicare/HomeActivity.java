package com.medicare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.infideap.drawerbehavior.AdvanceDrawerLayout;
import com.medicare.order.OrderMaintainActivity;
import com.medicare.reminderHelper.ReminderActivity;
import com.medicare.utils.Preferences;


public class HomeActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "HomeActivity";

    private AdvanceDrawerLayout drawer;

    private HomeActivity moActivity;
    private ConstraintLayout moClBrowse;
    private ConstraintLayout moClCart;
    private ConstraintLayout moClToolbarCart;
    private ConstraintLayout moClWishList;
    private ConstraintLayout moLogout;

    private ImageView moIvAvatar;
    private ImageView moIvCart;
    private ImageView moIvEditProfile;
    private ImageView moIvMenu;
    private TextView moTvCartDot;
    private TextView moTvEmail;
    private TextView moTvNavCartDot;
    private TextView moTvNavFavDot;
    LinearLayout search_medicine, near_store, upload_prescription, set_reminder;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_home);
        this.moActivity = this;
        initViews();
        initViewListeners();
    }



    private void initViews() {
        moIvMenu = (ImageView) findViewById(R.id.home_iv_menu);
        moIvCart = (ImageView) findViewById(R.id.home_iv_cart);

        moClToolbarCart = (ConstraintLayout) findViewById(R.id.home_cl_cart);
        moTvCartDot = (TextView) findViewById(R.id.home_tv_cartDot);
        drawer = (AdvanceDrawerLayout) findViewById(R.id.drawer_layout);
        drawer.useCustomBehavior(GravityCompat.START);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        moIvAvatar = (ImageView) navigationView.findViewById(R.id.nav_iv_avatar);
        moIvEditProfile = (ImageView) navigationView.findViewById(R.id.nav_iv_editProfile);
        moTvEmail = (TextView) navigationView.findViewById(R.id.nav_tv_email);

        moTvEmail.setText(Preferences.getStringPref(this.moActivity, Preferences.user_email));

        moClBrowse = (ConstraintLayout) navigationView.findViewById(R.id.nav_cl_browse);
        moClCart = (ConstraintLayout) navigationView.findViewById(R.id.nav_cl_cart);
        moClWishList = (ConstraintLayout) navigationView.findViewById(R.id.nav_cl_wishlist);


        search_medicine = findViewById(R.id.search_medicine);
        near_store = findViewById(R.id.near_store);
        upload_prescription = findViewById(R.id.upload_prescription);
        set_reminder = findViewById(R.id.set_reminder);
        moLogout = findViewById(R.id.nav_logout);


    }

    private void initViewListeners() {
        this.moIvMenu.setOnClickListener(this);
        this.moIvCart.setOnClickListener(this);
        this.moClBrowse.setOnClickListener(this);
        this.moClCart.setOnClickListener(this);
        this.moClWishList.setOnClickListener(this);
        this.moIvEditProfile.setOnClickListener(this);
        this.moIvAvatar.setOnClickListener(this);


        search_medicine.setOnClickListener(this);
        near_store.setOnClickListener(this);
        upload_prescription.setOnClickListener(this);
        set_reminder.setOnClickListener(this);
        moLogout.setOnClickListener(this);
    }


    public void onClick(View view1) {
        int view = view1.getId();
        if (view != R.id.nav_iv_editProfile) {
            switch (view) {
                case R.id.home_iv_cart:
                    startActivity(new Intent(HomeActivity.this,CartActivity.class));
                    return;
                case R.id.home_iv_menu:
                    this.drawer.openDrawer((int) GravityCompat.START);
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.moIvMenu.getWindowToken(), 0);
                    return;
                case R.id.search_medicine:
                    startActivity(new Intent(HomeActivity.this,MedicineActivity.class));
                    return;
                case R.id.near_store:
                    startActivity(new Intent(HomeActivity.this,NearMadicalActivity.class));
                    return;
                case R.id.upload_prescription:
                    startActivity(new Intent(HomeActivity.this,AddPrescriptionActivity.class));
                    return;
                case R.id.set_reminder:
                    startActivity(new Intent(HomeActivity.this, ReminderActivity.class));
                    return;
                default:
                    switch (view) {
                        case R.id.nav_cl_browse:
                            startActivity(new Intent(HomeActivity.this,MedicineActivity.class));

                            if (this.drawer.isDrawerOpen((int) GravityCompat.START)) {
                                this.drawer.closeDrawer((int) GravityCompat.START);
                            }
                            return;
                        case R.id.nav_cl_cart:

                            startActivity(new Intent(HomeActivity.this,CartActivity.class));
                            if (this.drawer.isDrawerOpen((int) GravityCompat.START)) {
                                this.drawer.closeDrawer((int) GravityCompat.START);
                            }
                            return;
                        case R.id.nav_cl_wishlist:
                            startActivity(new Intent(HomeActivity.this, OrderMaintainActivity.class));

                            if (this.drawer.isDrawerOpen((int) GravityCompat.START)) {
                                this.drawer.closeDrawer((int) GravityCompat.START);
                            }
                            return;

                        case R.id.nav_logout:

                            logoutApp();
                            return;
                        case R.id.nav_iv_avatar:
                            if (this.drawer.isDrawerOpen((int) GravityCompat.START)) {
                                this.drawer.closeDrawer((int) GravityCompat.START);
                            }

                            /*AlertDialog.Builder builder= new AlertDialog.Builder(HomeActivity.this);
                            builder.setTitle("Confirmation");
                            builder.setMessage("Do you want to logout?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Preferences.setStringPref(getApplicationContext(),Preferences.user_log_in,"logout");
                                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
                            builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog deleteDialog=builder.create();
                            deleteDialog.show();*/
                            startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
                            return;
                        default:
                            return;
                    }
            }
        }

    }

    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen((int) GravityCompat.START)) {
            drawerLayout.closeDrawer((int) GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void logoutApp(){
        Preferences.clearPref(this);
        finish();
        startActivity(new Intent(HomeActivity.this,LoginActivity.class));

    }

}

