package com.summertaker.sakamichiguide.common;

import android.content.SharedPreferences;
import android.util.Log;

import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OshimenManager {

    protected String mTag = "========== OshimenManager";

    protected String mCacheId = "oshimen";

    protected SharedPreferences mSharedPreferences;
    protected SharedPreferences.Editor mSharedEditor;

    protected String mDateFormatString = "yyyy-MM-dd HH:mm:ss";

    public OshimenManager(SharedPreferences sharedPreferences) {
        this.mSharedPreferences = sharedPreferences;
    }

    public ArrayList<MemberData> load() {
        //Log.e(mTag, "loadSnsCache()......... " + cacheKey);

        ArrayList<MemberData> dataList = new ArrayList<>();

        String jsonString = mSharedPreferences.getString(mCacheId, "");
        if (jsonString.isEmpty()) {
            return dataList;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            //Log.e(mTag, "jsonArray.length(): " + jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                MemberData data = new MemberData();
                data.setId(Util.getString(object, "id"));
                data.setName(Util.getString(object, "name"));
                data.setNameJa(Util.getString(object, "nameJa"));
                data.setFurigana(Util.getString(object, "furigana"));
                data.setNameKo(Util.getString(object, "nameKo"));
                data.setNameEn(Util.getString(object, "nameEn"));
                data.setGroupId(Util.getString(object, "groupId"));
                data.setGroupName(Util.getString(object, "groupName"));
                data.setTeamName(Util.getString(object, "teamName"));
                data.setGeneration(Util.getString(object, "generation"));
                data.setBirthday(Util.getString(object, "birthday"));
                data.setThumbnailUrl(Util.getString(object, "thumbnailUrl"));
                data.setImageUrl(Util.getString(object, "imageUrlUrl"));
                data.setProfileUrl(Util.getString(object, "profileUrl"));
                data.setNamuwikiUrl(Util.getString(object, "namuwikiUrl"));

                dataList.add(data);
                //Log.e(mTag, "data.getGroupId(): " + data.getGroupId());
            }
            //Log.e(mTag, "load().dataList.size(): " + dataList.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Collections.shuffle(dataList);
        return dataList;
    }

    public void save(MemberData memberData) {
        //Log.e(mTag, "saveSnsCache()......... " + cacheKey);

        ArrayList<MemberData> dataList = new ArrayList<>();
        dataList = this.load();
        //Log.e(mTag, "save().dataList.size(): " + dataList.size());

        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            boolean exist = false;
            for (MemberData data : dataList) {
                if (data.getId().equals(memberData.getId())) {
                    exist = true;
                } else {
                    jsonArray.put(getJsonObject(data));
                }
            }
            if (!exist) {
                jsonArray.put(getJsonObject(memberData));
                //Log.e(mTag, memberData.getName() + " added.");
            }

            jsonObject.put("items", jsonArray);
            //Log.e(mTag, jsonObject.toString());

            mSharedEditor = mSharedPreferences.edit();
            mSharedEditor.putString(mCacheId, jsonObject.toString());
            mSharedEditor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJsonObject(MemberData data) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("id", data.getId());
        object.put("name", data.getName());
        object.put("nameJa", data.getNameJa());
        object.put("furigana", data.getFurigana());
        object.put("nameKo", data.getNameKo());
        object.put("nameEn", data.getNameEn());
        object.put("groupId", data.getGroupId());
        object.put("groupName", data.getGroupName());
        object.put("teamName", data.getTeamName());
        object.put("generation", data.getGeneration());
        object.put("birthday", data.getBirthday());
        object.put("thumbnailUrl", data.getThumbnailUrl());
        object.put("imageUrl", data.getImageUrl());
        object.put("profileUrl", data.getProfileUrl());
        object.put("namuwikiUrl", data.getNamuwikiUrl());
        //Log.e(mTag, data.getName() + " added.");
        return object;
    }

    protected boolean isValidCacheDate(String cacheDate, String currentDate) {
        SimpleDateFormat format = new SimpleDateFormat(mDateFormatString, Locale.getDefault());

        try {
            Date d1 = format.parse(cacheDate);
            Date d2 = format.parse(currentDate);

            long diff = d2.getTime() - d1.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;

            return (diffMinutes < Config.CACHE_EXPIRE_TIME); // 15분

        } catch (ParseException e) {
            Log.e(mTag, "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
