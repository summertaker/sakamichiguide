package com.summertaker.sakamichiguide.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Util {

    public static Locale getLocale(Context context) {
        //return context.getResources().getConfiguration().locale;
        return Locale.getDefault();
    }

    public static String getLocaleStrng(Context context) {
        //Locale current = context.getResources().getConfiguration().locale;
        return Locale.getDefault().getCountry();
    }

    /*public static boolean isJapanLocale(Context context) {
        String locale = getLocaleStrng(context);
        return "JP".equals(locale);
    }*/

    public static boolean isEnglishLocale(Context context) {
        String l = getLocaleStrng(context);
        return (!"KR".equals(l) && !"JP".equals(l) && !"CN".equals(l) && !"TW".equals(l) && !"SG".equals(l));
    }

    public static String getJapaneseString(String text, String encoding) {
        if (encoding == null) {
            encoding = "ISO-8859-1"; // // JIS, SJIS, 8859_1, SHIFT-JIS
        }
        try {
            return new String(text.getBytes(encoding), Charset.forName("UTF8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }

    public static String getChineseString(String text, String encoding) {
        if (encoding == null) {
            encoding = "ISO-8859-1"; // // JIS, SJIS, 8859_1, SHIFT-JIS
        }
        try {
            return new String(text.getBytes(encoding), Charset.forName("UTF8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }

    public static String replaceJapaneseWhiteSpace(String s) {
        if (s == null) {
            return s;
        } else {
            // http://stackoverflow.com/questions/479825/problem-trimming-japanese-string-in-java
            return s.replace("　", "").replaceAll("\\p{Z}", " "); // 　
        }
    }

    public static String removeSpace(String s) {
        if (s == null) {
            return s;
        } else {
            return s.replace(" ", "").replace("　", "").replaceAll("\\p{Z}", " ");
        }
    }

    public static boolean isEqualJa(String nameJa, String nameKo) {
        boolean isEqaul;

        //String[] jaHan = {"髙", "麟", "濵", "﨑", "奈", "崎"};
        //String[] koHan = {"高", "麒", "濱", "崎", "菜", "﨑"};

        String ja = nameJa; //removeSpace(nameJa);
        String ko = nameKo; //removeSpace(nameKo);
        //Log.e("===", ja + " / " + ko);

        isEqaul = ja.equals(ko);
        /*if (!isEqaul) {
            for (int i = 0; i < jaHan.length; i++) {
                isEqaul = ja.replace(jaHan[i], koHan[i]).equals(ko);
                if (isEqaul) {
                    break;
                }
            }
        }*/

        return isEqaul;
    }

    public static boolean isEqualId(String localName, String wikiName) {
        boolean result = false;

        String local = localName;
        String wiki = wikiName;

        // 나무위키 오타
        if (wiki.contains("Beby Chaesara Anadlia")) {
            wiki = wiki.replace("Beby Chaesara Anadlia", "Beby Chaesara Anadila");
        } else if (wiki.contains("Fransisca Saraswati Puspa Dwi")) {
            wiki = wiki.replace("Fransisca Saraswati Puspa Dwi", "Fransisca Saraswati Puspa Dewi");
        } else if (wiki.contains("Priscilla Sari Dewi")) {
            wiki = wiki.replace("Priscilla Sari Dewi", "Priscillia Sari Dewi");
        } else if (wiki.contains("Fakhiryani Shafariyanti")) {
            wiki = wiki.replace("Fakhiryani Shafariyanti", "Fakhriyani Shafariyanti");
        }

        local = removeSpace(local);
        wiki = removeSpace(wiki);
        //Log.e("=====", "isEqualString(): " + local + " / " + wiki);

        return local.equals(wiki);
    }

    public static String replaceNamuwikiKanjiWithOfficial(String source) {
        String result = source;

        result = result.replace("中田花菜", "中田花奈"); // Nogizaka46
        result = result.replace("山﨑怜奈", "山崎怜奈"); // Nogizaka46
        result = result.replace("川後陽奈", "川後陽菜"); // Nogizaka46

        return result;
    }

    public static boolean isEqualString(String localName, String wikiName) {
        //boolean result = false;

        //String local = localName;
        //String wiki = wikiName;

        //local = removeSpace(local);
        //wiki = removeSpace(wiki);
        //Log.e("=====", "isEqualString(): " + local + " / " + wiki);

        if (localName == null || wikiName == null) {
            return false;
        } else {
            return localName.equals(wikiName);
        }
        //return local.equals(wiki);
    }

    public static String ucfirstAll(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        } else {
            String result = "";
            String[] array = s.toLowerCase().split(" ");
            for (int i = 0; i < array.length; i++) {
                //Log.i("#####", "array[i]: " + array[i]);
                if (i > 0) {
                    result += " ";
                }
                result += ucfirst(array[i]);
            }
            return result;
        }
    }

    public static String ucfirst(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        } else if (s.length() == 1) {
            return s.toUpperCase();
        } else {
            return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
        }
    }

    public static String getString(JSONObject json, String key) {
        try {
            return json.getString(key);
        } catch (JSONException e) {
            //e.printStackTrace();
            return "";
        }
    }

    public static String getOrdinal(String s) {
        return getOrdinal(Integer.parseInt(s));
    }

    public static String getOrdinal(int i) {
        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return "th";
            default:
                return suffixes[i % 10];

        }
    }

    public static int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public static String numberFormat(int value) {
        return numberFormat(String.valueOf(value));
    }

    public static String numberFormat(String value) {
        NumberFormat nf = NumberFormat.getInstance(); // get instance for your locale
        nf.setMaximumFractionDigits(0); // set decimal places
        Double d = Double.parseDouble(value);
        return nf.format(d); // the parameter must be a long or double
    }

    public static String getToday(String format) {
        // 2016년 3월 22일 (금)
        //Date now = new Date();
        //DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        //String today = df.format(now);

        // 2016년 3월 22일 화요일 오후 10:45
        //String dateTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(now);

        Calendar calendar = Calendar.getInstance();
        if (format == null) {
            format = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null...");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static String urlToId(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        String id = url;

        id = id.replace("http", "");
        id = id.replace("https", "");
        id = id.replace("www", "");
        id = id.replace("php", "");
        id = id.replace("html", "");
        id = id.replace("htm", "");

        //id = getEncodedUrl(url);

        id = id.replace(":", "");
        id = id.replace("/", "");
        id = id.replace(".", "");
        id = id.replace("?", "");
        id = id.replace("&", "");
        id = id.replace("%", "");
        id = id.replace("=", "");
        id = id.replace("-", "");
        id = id.replace("_", "");

        // http://stackoverflow.com/questions/13204807/max-file-name-length-in-android
        // Max 255 byte: 127 characters limit, because 1 char is 2 bytes, so 127 chars is 254 bytes
        int maxLength = 127;
        int length = id.length();
        if (length > maxLength) {
            id = id.substring(id.length() - maxLength, id.length());
        }
        //Log.e("", "[ " + id.length() + " ] " + id);

        id = id.toLowerCase();

        return id;
    }

    public static String[] getSnsInfo(String url) {
        String[] result = {"", ""};

        String userId = "";
        String fullUrl = "";

        String[] array = url.split("/");
        if (array.length > 3) {
            userId = array[3].replace("#", "").trim();
            if (userId.contains("?")) {
                array = userId.split("\\?");
                userId = array[0];
            }

            if (url.contains("plus.google.com")) {
                //fullUrl = url;
                if (url.contains("/u/0/")) {
                    // "https://plus.google.com/u/0/114004494049213490409/posts";
                    userId = array[5];
                }
                fullUrl = "https://plus.google.com/" + userId + "/posts";
            } else if (url.contains("twitter.com")) {
                //fullUrl = url;
                fullUrl = "https://mobile.twitter.com/" + userId;
            } else if (url.contains("facebook.com")) {
                //fullUrl = url;
                fullUrl = "https://m.facebook.com/" + userId;
            } else if (url.contains("instagram.com")) {
                //fullUrl = url;
                fullUrl = "https://instagram.com/" + userId;
            }
        }
        //Log.e("===", "fullUrl: " + fullUrl);

        result[0] = userId;
        result[1] = fullUrl;

        return result;
    }

    public static void setProgressBarColor(ProgressBar progressBar, int color, PorterDuff.Mode mode) {
        if (color == 0) {
            color = Config.PROGRESS_BAR_COLOR_LIGHT;
        }
        if (mode == null) {
            mode = PorterDuff.Mode.MULTIPLY; // or PorterDuff.Mode.SRC_ATOP
        }
        progressBar.getIndeterminateDrawable().setColorFilter(color, mode);
    }

    public static String getErrorMessage(VolleyError e) {
        String message = e.getClass().getSimpleName();
        NetworkResponse response = e.networkResponse;
        if (response != null && response.data != null) {
            message = response.statusCode + "\n" + message;
        }
        return message;
    }

    public static void alert(Context context, String message) {
        if (message != null) {
            AlertDialog.Builder adBuilder = new AlertDialog.Builder(context);
            adBuilder.setMessage(message).setTitle(R.string.app_name);
            adBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = adBuilder.create();
            dialog.show();
        }
    }

    /*
    public static void setCache(SharedPreferences preferences, String key, String value) {
        //SharedPreferences sharedPreferences = context.getSharedPreferences(Config.USER_PREFERENCE_KEY, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getCache(SharedPreferences preferences, String key) {
        //SharedPreferences sp = context.getSharedPreferences(Config.USER_PREFERENCE_KEY, 0);
        return preferences.getString(key, "");
    }

    public static void clearCache(SharedPreferences preferences) {
        //SharedPreferences sp = context.getSharedPreferences(Config.USER_PREFERENCE_KEY, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
    */
}
