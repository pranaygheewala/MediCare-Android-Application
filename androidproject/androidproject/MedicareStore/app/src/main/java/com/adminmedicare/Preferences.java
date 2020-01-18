package com.adminmedicare;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String MEDICARE = "medicareadmin";
    public static String login_status = "loginstatus";

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
}
