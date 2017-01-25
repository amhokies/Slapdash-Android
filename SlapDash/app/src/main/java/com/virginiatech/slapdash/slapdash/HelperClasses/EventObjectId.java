package com.virginiatech.slapdash.slapdash.HelperClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by nima on 10/19/16.
 */

public class EventObjectId {
    private static EventObjectId instance = null;
    private final String EVENT_TAG = "CURRENT_EVENT_ID";
    private final String SHARED_PREF_TAG = "com.slapdash.app";
    private SharedPreferences slapdashPreference;

    private EventObjectId(Context context){
        slapdashPreference = context.getSharedPreferences(SHARED_PREF_TAG, Context.MODE_PRIVATE);
    }

    public static EventObjectId getInstance(Context context){
        return instance == null ? (instance = new EventObjectId(context)) : instance;
    }

    public void updateEventObjectIdToken(String newTokenId){
        slapdashPreference.edit().putString(
                EVENT_TAG,
                newTokenId
        ).commit();
    }

    public String getCurrentEventObjectIdToken(){
        return slapdashPreference.getString(EVENT_TAG, null);
    }

    public void removeEventObjectIdToken(){
        slapdashPreference.edit().remove(EVENT_TAG).commit();
    }
}
