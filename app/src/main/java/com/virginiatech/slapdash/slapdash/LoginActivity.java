package com.virginiatech.slapdash.slapdash;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.virginiatech.slapdash.slapdash.DataModelClasses.User;
import com.virginiatech.slapdash.slapdash.HelperClasses.UserIdToken;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPI;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPIBuilder;

import org.apache.commons.lang3.StringUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.HttpURLConnection;
import java.util.Arrays;

/**
 * Created by Nelson on 9/26/2016.
 */
public class LoginActivity extends Activity {
    private static final String TAG = "LOGIN-ACTIVITY";
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ProfileTracker mProfileTracker;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        //Log.d(MainActivity.DEBUG, AccessToken.getCurrentAccessToken().getToken());
        if(AccessToken.getCurrentAccessToken() != null){
            finish();
        }
        String[] permissions = {"public_profile", "user_friends", "email"};
        loginButton.setReadPermissions(Arrays.asList(permissions));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult loginResult) {
                Profile profile = Profile.getCurrentProfile();
                if (profile == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            SignUpToSlapDashServer(loginResult, profile2);
                            mProfileTracker.stopTracking();
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                } else {
                    SignUpToSlapDashServer(loginResult, profile);
                }
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }

        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if you don't add following block,
        // your registered `FacebookCallback` won't be called
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Login Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private void SignUpToSlapDashServer(LoginResult loginResult, Profile profile){
        Log.v("facebook - profile", profile.getFirstName());
        Log.v("facebook - profile", profile.getLastName());
        Log.v("facebook - profile", profile.getName());
        Log.v("facebook - profile", profile.getId());
        Log.v("facebook - profile", profile.getProfilePictureUri(200, 200).toString());

        // CREATE CALL goes here!
        // As long as call is successful, we are good
        AccessToken fbAccessToken = loginResult.getAccessToken();
        if (fbAccessToken == null) {
//            Log.d(TAG, "accessToken is null");
//            Log.d(TAG, "aborting facebook login");
            Toast.makeText(getBaseContext(), "Facebook Login Failed", Toast.LENGTH_LONG).show();
            finish();
        }

        String fbToken = fbAccessToken.getToken();
        if (StringUtils.isBlank(fbToken)) {
//            Log.d(TAG, "fbToken is blank");
//            Log.d(TAG, "aborting facebook login");
            Toast.makeText(getBaseContext(), "Facebook Login Failed", Toast.LENGTH_LONG).show();
            finish();
        }

        Log.v(TAG, "fbToken:" + fbToken);

        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        if (StringUtils.isBlank(fcmToken)) {
//            Log.d(TAG, "fcmToken is blank");
//            Log.d(TAG, "aborting facebook login");
            Toast.makeText(getBaseContext(), "Facebook Login Failed", Toast.LENGTH_LONG).show();
            finish();
        }

//        Log.v(TAG, "fcmToken:" + fcmToken);

        User userToBeCreated = new User(profile.getFirstName(), profile.getLastName(), fbToken, fcmToken);

        SlapDashAPI service = SlapDashAPIBuilder.getAPI();
        Call<User> call = service.createUser(fbToken, userToBeCreated);

        call.enqueue(signUpUserCallback);
    }

    private Callback<User> signUpUserCallback = new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            int resCode = response.code();
            if (resCode == HttpURLConnection.HTTP_OK
                    || resCode == HttpURLConnection.HTTP_CREATED) {
//                System.out.println("Success: HTTP POST /users/id");
                Toast.makeText(getBaseContext(), "Success: server returned HTTP " + resCode, Toast.LENGTH_LONG).show();

                UserIdToken.getInstance(getApplicationContext()).updateUserIdToken(response.body().get_id());
//
//                String userId = UserIdToken.getInstance(getApplicationContext()).getCurrentUserIdToken();
//
//                if(StringUtils.isBlank(userId)){
//                    Log.d(TAG, "userId is blank");
//                } else {
//                    Log.d(TAG, "userId: " + userId);
//                }
                finish();
            } else {
                Toast.makeText(getBaseContext(),"Couldn't sign up, Try again!", Toast.LENGTH_SHORT).show();
                LoginManager login = LoginManager.getInstance();
                if(login != null){
                    login.logOut();
                }
            }
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            t.printStackTrace();
//            System.out.println("Failure: HTTP POST /users/id");
            Toast.makeText(getBaseContext(), "Failure to fetch from "
                    + SlapDashAPIBuilder.getSLAPDASH_BASE_URL(), Toast.LENGTH_LONG).show();
        }
    };
}
