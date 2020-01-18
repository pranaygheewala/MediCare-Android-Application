package com.medicare.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String MEDICARE = "medicare";
    public static String save_uri = "saveURI";
    public static String user_name = "user_name";
    public static String user_id = "user_id";
    public static String user_email = "user_email";
    public static String user_addres = "user_addres";
    public static String user_token = "user_token";
    public static String user_log_in = "user_log_in";
    public static String user_dob = "user_dob";
    public static String user_gender = "user_gendar";
    public static String user_address_array = "user_address_array";

    public static void setStringPref(Context context1, String str, String str2) {
        SharedPreferences.Editor context = context1.getSharedPreferences(MEDICARE, 0).edit();
        context.putString(str, str2);
        context.apply();
        context.commit();
    }

    public static String getStringPref(Context context, String str) {
        return context.getSharedPreferences(MEDICARE, 0).getString(str, "");
    }

    public static void setIntPref(Context context1, String str, int i) {
        SharedPreferences.Editor context = context1.getSharedPreferences(MEDICARE, 0).edit();
        context.putInt(str, i);
        context.apply();
        context.commit();
    }

    public static int getIntPref(Context context, String str) {
        return context.getSharedPreferences(MEDICARE, 0).getInt(str, -1);
    }

    public static void setBooleanPref(Context context1, String str, boolean z) {
        SharedPreferences.Editor context = context1.getSharedPreferences(MEDICARE, 0).edit();
        context.putBoolean(str, z);
        context.apply();
        context.commit();
    }

    public static boolean getBooleanPref(Context context, String str) {
        return context.getSharedPreferences(MEDICARE, 0).getBoolean(str, false);
    }

    public static void clearPref(Context context){
        SharedPreferences.Editor getPref = context.getSharedPreferences(MEDICARE,0).edit();
        getPref.clear();
        getPref.commit();
    }
}
