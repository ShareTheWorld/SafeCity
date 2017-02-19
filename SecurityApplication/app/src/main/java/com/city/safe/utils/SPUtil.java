package com.city.safe.utils;

import android.content.Context;
import android.content.SharedPreferences;




/**
 * Created by user on 16-12-3.
 */

public class SPUtil {
    private static final String SP_FILE_NAME="sp";
    public static final String SELECTED_IMG="selected_img";
    public static final String USER_INFO="user_info";
    public static final String CURRENT_PROJECT_ID="current_project_id";
    public static final String USER_ID="user_id";
    public static final String USER_PASSWORD="user_password";
    public  static void saveValueByKey(Context context,String key,String value){
        SharedPreferences sp=context.getSharedPreferences(SP_FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(key,value);
        editor.commit();
        Logger.i("hongtao.fu","saveValue= "+value);
    }
    public  static String getValueByKey(Context context,String key){
        SharedPreferences sp=context.getSharedPreferences(SP_FILE_NAME,Context.MODE_PRIVATE);
        String value=sp.getString(key,"");
        Logger.i("hongtao.fu","getValue= "+value);
        return value;
    }
}
