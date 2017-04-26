package com.cn.lx.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xueliang on 2017/4/13.
 */

public class SharedPreferencesManager {

    SharedPreferences sharedPreferences ;
    public SharedPreferencesManager(Context context){
        sharedPreferences = context.getSharedPreferences("xmpp",Context.MODE_PRIVATE);
    }

    public String getStringByKey(String key){
        return sharedPreferences.getString(key,null);
    }

    public boolean putString(String key,String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        return editor.commit();
    }

}
