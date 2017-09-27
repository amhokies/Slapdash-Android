package com.virginiatech.slapdash.slapdash.FireBaseService;

import android.util.Log;

import com.facebook.AccessToken;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.virginiatech.slapdash.slapdash.HelperClasses.UserIdToken;
import com.virginiatech.slapdash.slapdash.MainActivity;
import com.virginiatech.slapdash.slapdash.DataModelClasses.User;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPI;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPIBuilder;

import org.apache.commons.lang3.StringUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nima on 10/13/16.
 */

public class FireBaseService extends FirebaseInstanceIdService {

    private static final String TAG = "TOKEN-REFRESH";

    @Override
    public void onTokenRefresh() {

        Log.d(TAG, "onTokenRefresh() has been called");

        // grab new token
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "new fcmToken: " + fcmToken);

        String userId = UserIdToken.getInstance(getApplicationContext()).getCurrentUserIdToken();

        if(StringUtils.isBlank(userId)){
            //TODO: act accordingly and redirect to login
            Log.d(TAG, "userId is blank");
            return;
        }

        AccessToken fbAccessToken = AccessToken.getCurrentAccessToken();
        if(fbAccessToken == null){
            //TODO: see why this would happen
            Log.d(TAG, "fbAccessToken is null");
            return;
        }

        SlapDashAPI service = SlapDashAPIBuilder.getAPI();
        User toBeUpdated = new User();
        toBeUpdated.setAndroidregid(fcmToken);

        Call<User> call = service.updateUser(userId, fbAccessToken.toString(), toBeUpdated);

        call.enqueue(new Callback<User>(){

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    Log.d(MainActivity.DEBUG, "Updated the androidregid successfully");
                } else {
                    Log.e(MainActivity.DEBUG, "Couldn't update the androidregid");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(MainActivity.DEBUG, "Something went wrong trying to update androidId");
            }
        } );
    }
}
