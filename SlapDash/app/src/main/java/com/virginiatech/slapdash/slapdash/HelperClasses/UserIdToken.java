package com.virginiatech.slapdash.slapdash.HelperClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by nima on 10/18/16.
 */

public class UserIdToken {
    private static UserIdToken instance = null;
    private final String USER_ID_TAG = "USERID";
    private final String SHARED_PREF_TAG = "com.slapdash.app";
    private SharedPreferences slapdashPreference;

    private UserIdToken(Context context){
        slapdashPreference = context.getSharedPreferences(SHARED_PREF_TAG, Context.MODE_PRIVATE);
    }

    public static UserIdToken getInstance(Context context){
        return instance == null ? (instance = new UserIdToken(context)) : instance;
    }

    public void updateUserIdToken(String newTokenId) {
        Log.d(USER_ID_TAG, "updateUserIdToken has been called with String param:" +  newTokenId);
        slapdashPreference.edit().putString(
                USER_ID_TAG,
                newTokenId
        ).commit();
    }

    public String getCurrentUserIdToken(){
        return slapdashPreference.getString(USER_ID_TAG, null);
    }

    public void removeUserIdToken(){
        slapdashPreference.edit().remove(USER_ID_TAG).commit();
    }

}
