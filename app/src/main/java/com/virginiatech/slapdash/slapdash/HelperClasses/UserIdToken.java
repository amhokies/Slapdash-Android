package com.virginiatech.slapdash.slapdash.HelperClasses;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by nima on 10/18/16.
 * <p>
 * The helper class to normalize the how the current user ObjectId is stored
 * using the shared preferences.
 */

public class UserIdToken {
    private static UserIdToken instance = null;
    private final String USER_ID_TAG = "USERID";
    private final String SHARED_PREF_TAG = "com.slapdash.app";
    private SharedPreferences slapdashPreference;

    private UserIdToken(Context context) {
        slapdashPreference = context.getSharedPreferences(SHARED_PREF_TAG, Context.MODE_PRIVATE);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                   Public methods                                        //
    /////////////////////////////////////////////////////////////////////////////////////////////
    public static UserIdToken getInstance(Context context) {
        return instance == null ? (instance = new UserIdToken(context)) : instance;
    }

    //-------------------------------------------------------------------------------------------
    public void updateUserIdToken(String newTokenId) {
        slapdashPreference.edit().putString(USER_ID_TAG, newTokenId).apply();
    }

    //-------------------------------------------------------------------------------------------
    public String getCurrentUserIdToken() {
        return slapdashPreference.getString(USER_ID_TAG, null);
    }

    //-------------------------------------------------------------------------------------------
    public void removeUserIdToken() {
        slapdashPreference.edit().remove(USER_ID_TAG).apply();
    }

}
