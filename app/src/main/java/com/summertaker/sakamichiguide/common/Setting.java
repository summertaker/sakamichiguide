package com.summertaker.sakamichiguide.common;

import android.content.Context;
import android.content.SharedPreferences;

public class Setting {

    private SharedPreferences mSharedPreferences;

    public Setting(Context context) {
        this.mSharedPreferences = context.getSharedPreferences(Config.USER_PREFERENCE_KEY, 0);
    }

    public String get(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public void set(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
