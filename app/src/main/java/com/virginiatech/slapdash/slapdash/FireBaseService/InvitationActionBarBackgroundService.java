package com.virginiatech.slapdash.slapdash.FireBaseService;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.virginiatech.slapdash.slapdash.DataModelClasses.Event;
import com.virginiatech.slapdash.slapdash.DataModelClasses.UserLocation;
import com.virginiatech.slapdash.slapdash.api.EventInvitationRequest;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPI;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPIBuilder;

import java.io.IOException;

import retrofit2.Call;

import static com.virginiatech.slapdash.slapdash.Services.LocationService.getRecentLocation;

/**
 * Created by Matt on 11/6/2016.
 *
 * The Service that runs in the background in order to respond to an invitation
 * by either declining or accepting.
 */
public class InvitationActionBarBackgroundService extends IntentService {

    private static final String TAG = "INVITATION-ACTION-BAR";
    public static final String ACTION_BAR_ACCEPT = "ACCEPT";
    public static final String ACTION_BAR_DECLINE = "DECLINE";

    //--------------------------------------------------------------------------------------------
    public InvitationActionBarBackgroundService() {
        super(TAG);
    }

    //--------------------------------------------------------------------------------------------
    @Override
    protected void onHandleIntent(Intent intent) {

        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // TODO: Figure out how to more gracefully cancel the intended notification , Using ID?
        nManager.cancelAll();

        if (intent != null) {

            String eventId = intent.getStringExtra("eventid");
            if (eventId != null) {

                String action = intent.getAction();
                if (action != null) {
                    if (ACTION_BAR_ACCEPT.equals(action)) {
                        respondToInvitationSync(eventId, true);
                        // TODO: Set Event to Main Event? Should Ask user?
                    } else if (ACTION_BAR_DECLINE.equals(action)) {
                        respondToInvitationSync(eventId, false);
                    }
                }
            }
        }
    }

    //--------------------------------------------------------------------------------------------
    private void respondToInvitationSync(String eventId, final boolean accept) {

        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(getApplication());
        }

        String accessToken = AccessToken.getCurrentAccessToken().getToken();

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        UserLocation location = getRecentLocation(this, locManager);
        EventInvitationRequest request = new EventInvitationRequest(eventId, accept, location);

        SlapDashAPI service = SlapDashAPIBuilder.getAPI();
        Call<Event> call = service.respondToEvent(accessToken, request);

        try {
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
