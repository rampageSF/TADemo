package com.lifesense.ta_android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lifesense.ta_android.MyApp;


/**
 * @author linquandong
 * @create 2019/7/19
 * @Describe
 **/
public class SpUtils {


    private static final String USER_ID = "ta_userid";
    private static final String DOMAIN = "ta_domain";
    private static SharedPreferences sSharedPreferences;
    private static SharedPreferences.Editor sEditor;

    private static final String DEFAULT_SP_NAME = "SharedData";
    private static final int DEFAULT_INT = 0;
    private static final float DEFAULT_FLOAT = 0.0f;
    private static final String DEFAULT_STRING = "";
    private static final boolean DEFAULT_BOOLEAN = false;

    static {
        sSharedPreferences = MyApp.getApp().getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE);
        sEditor = sSharedPreferences.edit();
    }

    public static void put(String key, Object value) {
        if (value instanceof String) {
            sEditor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            sEditor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            sEditor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            sEditor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            sEditor.putLong(key, (Long) value);
        } else {
            sEditor.putString(key, value.toString());
        }
        sEditor.commit();
    }

    public static Object get(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sSharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sSharedPreferences.getInt(key, (int) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sSharedPreferences.getBoolean(key, (boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sSharedPreferences.getFloat(key, (float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sSharedPreferences.getLong(key, (long) defaultObject);
        }
        return null;
    }



    public static void saveUserId(String userid) {
        put(USER_ID,userid);
    }

    public static String getUserId() {
        return (String) get(USER_ID,new String());
    }

    public static void saveDomain(String url) {
        put(DOMAIN,url);
    }

    public static String getDomain(){
        return (String) get(DOMAIN,"");
    }
}

