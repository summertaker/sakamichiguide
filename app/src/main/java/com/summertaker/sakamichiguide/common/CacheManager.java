package com.summertaker.sakamichiguide.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CacheManager {

    protected String mTag;

    protected SharedPreferences mSharedPreferences;
    protected SharedPreferences.Editor mSharedEditor;

    protected String mDateFormatString = "yyyy-MM-dd HH:mm:ss";

    public CacheManager(Context context) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mSharedPreferences = context.getSharedPreferences(Config.USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
    }

    public CacheManager(SharedPreferences sharedPreferences) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mSharedPreferences = sharedPreferences;
    }

    public String load(String cacheKey) {
        return load(cacheKey, Config.CACHE_EXPIRE_TIME);
    }

    public String load(String cacheKey, int expireMinutes) {
        String data = null;
        String jsonString = mSharedPreferences.getString(cacheKey, "");
        if (jsonString.isEmpty()) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            String cacheDate = jsonObject.getString("cacheDate");
            String today = Util.getToday(mDateFormatString);
            if (isValidCacheDate(cacheDate, today, expireMinutes)) {
                data = jsonObject.getString("data");
            }
            //Log.e(mTag, " - isValid: " + isValid + " / cacheDate: " + cacheDate + ", today: " +today);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

    public JSONObject loadJsonObject(String cacheKey, int expireMinutes) {
        String jsonString = mSharedPreferences.getString(cacheKey, "");
        //Log.e(mTag, "jsonString: " + jsonString);

        JSONObject jsonObject = null;

        if (!jsonString.isEmpty()) {
            try {
                jsonObject = new JSONObject(jsonString);

                String cacheDate = jsonObject.getString("cacheDate");
                String today = Util.getToday(mDateFormatString);
                boolean isValid = isValidCacheDate(cacheDate, today, expireMinutes);
                jsonObject.put("isValid", isValid);
                //Log.e(mTag, " - isValid: " + isValid + " / cacheDate: " + cacheDate + ", today: " +today);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public void save(String cacheKey, String data) {
        if (data == null || data.isEmpty() || data.equals("[]")) {
            //return;
            data = "";
        }

        String today = Util.getToday(mDateFormatString);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cacheDate", today);
            jsonObject.put("data", data);
            //Log.e(mTag, jsonObject.toString());

            mSharedEditor = mSharedPreferences.edit();
            mSharedEditor.putString(cacheKey, jsonObject.toString());
            mSharedEditor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void save(String cacheKey, JSONArray data) {
        String today = Util.getToday(mDateFormatString);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cacheDate", today);
            jsonObject.put("data", data);
            //Log.e(mTag, jsonObject.toString());

            mSharedEditor = mSharedPreferences.edit();
            mSharedEditor.putString(cacheKey, jsonObject.toString());
            mSharedEditor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*public boolean loadSnsCache(String cacheKey, ArrayList<WebData> dataList) {
        //Log.e(mTag, "loadSnsCache()......... " + cacheKey);

        String jsonString = mSharedPreferences.getString(cacheKey, "");
        //Log.e(mTag, " - Cache data: " + jsonString);
        if (jsonString.isEmpty()) {
            return false;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String cacheDate = jsonObject.getString("cacheDate");
            String today = Util.getToday(mDateFormatString);

            boolean isValid = isValidCacheDate(cacheDate, today);
            //Log.e(mTag, " - isValid: " + isValid + " / cacheDate: " + cacheDate + ", today: " +today);

            if (isValid) {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                //Log.e(mTag, "jsonArray.toString(): " + jsonArray.toString());
                dataList.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    WebData data = new WebData();
                    //data.setSiteKey(Util.getString(object, "siteKey"));
                    data.setId(Util.getString(object, "id"));
                    dataList.add(data);
                }

                //Log.e(mTag, "- dataList.size(): " + dataList.size());
            }
        } catch (JSONException e) {
            Log.e(mTag, "ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        return dataList.size() > 0;
    }*/

    /*public void saveSnsCache(String cacheKey, ArrayList<WebData> dataList) {
        //Log.e(mTag, "saveSnsCache()......... " + cacheKey);

        if (dataList == null || dataList.size() == 0) {
            return;
        }

        String today = Util.getToday(mDateFormatString);
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            for (WebData data : dataList) {
                JSONObject object = new JSONObject();
                //object.put("siteKey", data.getSiteKey());
                object.put("id", data.getId());
                jsonArray.put(object);
            }

            jsonObject.put("cacheDate", today);
            jsonObject.put("items", jsonArray);

            //Log.e(mTag, jsonObject.toString());

            mSharedEditor = mSharedPreferences.edit();
            mSharedEditor.putString(cacheKey, jsonObject.toString());
            mSharedEditor.apply();
        } catch (JSONException e) {
            Log.e(mTag, "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    */

    protected boolean isValidCacheDate(String cacheDate, String currentDate, int expireMinutes) {
        if (expireMinutes == 0) {
            return true;
        } else {
            SimpleDateFormat format = new SimpleDateFormat(mDateFormatString, Locale.getDefault());
            boolean isValid = false;
            try {
                //Log.e(mTag, "cacheDate: " + cacheDate);
                //Log.e(mTag, "currentDate: " + currentDate);
                Date d1 = format.parse(cacheDate);
                Date d2 = format.parse(currentDate);

                long diff = d2.getTime() - d1.getTime();
                long diffSeconds = diff / 1000 % 60;
                long diffMinutes = diff / (60 * 1000) % 60;
                //Log.e(mTag, "expireMinutes: " + expireMinutes + " / diffMinutes: " + diffMinutes);

                isValid = diffMinutes < expireMinutes;

            } catch (ParseException e) {
                Log.e(mTag, "ERROR: " + e.getMessage());
                e.printStackTrace();
            }
            return isValid;
        }
    }
}
