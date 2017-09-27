package com.virginiatech.slapdash.slapdash;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
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

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nelson on 9/26/2016.
 * <p>
 * Login controller for slapdash. Currently only Facebook login
 * is supported and the result from Facebook login will be sent
 * back to the api.
 */
public class LoginActivity extends Activity {
    //////////////////////////////////////////////////////////////////////////////////////////////
    //                                  Private Members                                         //
    //////////////////////////////////////////////////////////////////////////////////////////////

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ProfileTracker mProfileTracker;
    private boolean hasLoggedInBefore = false; /* Assumption */

    //////////////////////////////////////////////////////////////////////////////////////////////
    //                              Google Index generated code                                 //
    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Login Page")
                .setUrl(Uri.parse("https://amhokies.me"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //                                      Overrides                                           //
    //////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    //--------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        info = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setVisibility(Button.INVISIBLE);

        /* Set up the facebook sdk */
        callbackManager = CallbackManager.Factory.create();
        String[] permissions = {"public_profile", "user_friends", "email"};
        loginButton.setReadPermissions(Arrays.asList(permissions));
        loginButton.registerCallback(callbackManager, new LoginFacebookCallback());

        // Facebook way of keeping track of SignIns
        AccessToken previousAccessToken = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();

        // SlapDash way of keeping tracking of signIns
        String storedAccessToken = UserIdToken.getInstance(getApplicationContext())
                .getCurrentUserIdToken();
        if (storedAccessToken == null) { /* Absolutely new to app */
            /* Sign Up */
            loginButton.setVisibility(Button.VISIBLE);
        } else if (previousAccessToken == null) { /* Probably facebook lost track of access token */
            /* Sign Up */
            hasLoggedInBefore = true; /* since did't go to 'if' clause */
            loginButton.setVisibility(Button.VISIBLE);
        } else { /* all the data is here just validate one more time */
            /* Sign Up */
            hasLoggedInBefore = true;
            ReachSlapDashServer(previousAccessToken,
                    profile, /* might be empty but not a problem */
                    new SignUpUserCallback());
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //--------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if you don't add following block,
        // your registered `FacebookCallback` won't be called
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        finish(false);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //                                  Private Methods                                         //
    //////////////////////////////////////////////////////////////////////////////////////////////

    private void ReachSlapDashServer(AccessToken fbAccessToken,
                                     Profile profile,
                                     Callback<User> callback) {
        // CREATE CALL goes here!
        String fbToken = null;
        if (fbAccessToken == null || StringUtils.isBlank((fbToken = fbAccessToken.getToken()))) {
            Toast.makeText(getBaseContext(), "Facebook Login Failed!", Toast.LENGTH_LONG).show();
            finish(false);
        }

        FirebaseInstanceId fbInstantId = FirebaseInstanceId.getInstance();
        String fcmToken = null;
        if (fbInstantId == null || StringUtils.isBlank(fcmToken = fbInstantId.getToken())) {
            Toast.makeText(getBaseContext(), "FireBase Login Failed!", Toast.LENGTH_LONG).show();
            finish(false);
        }

        User userToBeCreated = new User(profile.getFirstName(), profile.getLastName(), fbToken,
                fcmToken);

        SlapDashAPI service = SlapDashAPIBuilder.getAPI();
        Call<User> call = service.createUser(fbToken, userToBeCreated);
        call.enqueue(callback);
    }

    //--------------------------------------------------------------------------------------------
    private class LoginFacebookCallback implements FacebookCallback<LoginResult> {
        @Override
        public void onSuccess(final LoginResult loginResult) {
            final Callback<User> userCallback = new SignUpUserCallback();
            Profile profile = Profile.getCurrentProfile();
            if (profile == null) {
                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        // profile2 is the new profile
                        ReachSlapDashServer(loginResult.getAccessToken(), profile2, userCallback);
                        mProfileTracker.stopTracking();
                    }
                };
                // no need to call startTracking() on mProfileTracker
                // because it is called by its constructor, internally.
            } else {
                ReachSlapDashServer(loginResult.getAccessToken(), profile, userCallback);
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
    }

    //--------------------------------------------------------------------------------------------
    private class SignUpUserCallback implements Callback<User> {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (response.isSuccessful()) {
                /* Store the userId from API call */
                UserIdToken.getInstance(getApplicationContext())
                        .updateUserIdToken(response.body().get_id());
                finish(true);
            } else {
                Toast.makeText(getBaseContext(), "Couldn't sign up, Try again!",
                        Toast.LENGTH_SHORT).show();
                LoginManager login = LoginManager.getInstance();
                if (login != null) {
                    login.logOut();
                }
            }
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(
                    getBaseContext(),
                    "Failure to connect, Check your internet connection!",
                    Toast.LENGTH_LONG).show();
        }
    }

    //--------------------------------------------------------------------------------------------
    private void finish(boolean loggedInSuccesfullyNow){
        if (loggedInSuccesfullyNow) {
            this.setResult(MainActivity.getLOG_IN_RESULT_SUCCESSFUL());
        } else if(hasLoggedInBefore) {
            this.setResult(MainActivity.getLOG_IN_RESULT_SUCCESSFUL()); /* TODO: Should change to
                                                                           something more meaningful */
        } else {
            this.setResult(MainActivity.getLOG_IN_RESULT_UNSUCCESSFUL());
        }
        super.finish();
    }
}
