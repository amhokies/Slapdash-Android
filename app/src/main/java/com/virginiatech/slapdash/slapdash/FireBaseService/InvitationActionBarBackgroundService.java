package com.virginiatech.slapdash.slapdash.FireBaseService;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.virginiatech.slapdash.slapdash.DataModelClasses.Event;
import com.virginiatech.slapdash.slapdash.api.EventInvitationRequest;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPI;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPIBuilder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

import static com.virginiatech.slapdash.slapdash.Services.LocationService.getRecentLocation;

/**
 * Created by Matt on 11/6/2016.
 */
public class InvitationActionBarBackgroundService extends IntentService {

    private static final String TAG = "INVITATION-ACTION-BAR";
    private static final String ACTION_BAR_ACCEPT = "ACCEPT";
    private static final String ACTION_BAR_DECLINE = "DECLINE";

    public InvitationActionBarBackgroundService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // TODO: Figure out how to more gracefully cancel the intended notification
        //nManager.cancel(TAG, 0);
        nManager.cancelAll();

        if (intent != null) {
            Log.d(TAG, "Success: Intent NOT null");

            String eventId = intent.getStringExtra("eventid");
            if (eventId != null) {

                String action = intent.getAction();
                if (action != null) {
                    if (ACTION_BAR_ACCEPT.equals(action)) {
                        Log.d(TAG, "Accepting event " + eventId);
                        respondToInvitationSync(eventId, true);
                        // TODO: Set Event to Main Event?
                    }
                    else if (ACTION_BAR_DECLINE.equals(action)) {
                        Log.d(TAG, "Declining event " + eventId);
                        respondToInvitationSync(eventId, false);
                    }
                    else {
                        // this should never happen
                        Log.d(TAG, "Unknown action");
                    }
                }
                else {
                    Log.d(TAG, "action is null");
                }
            }
            else {
                Log.d(TAG, "eventid is null");
            }
        }
        else {
            Log.d(TAG, "Fail: Intent null");
        }
    }

    private void respondToInvitationSync(String eventId, final boolean accept) {

        if(!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(getApplication());
        }

        SlapDashAPI service = SlapDashAPIBuilder.getAPI();
        Call<Event> call = service.respondToEvent(
                AccessToken.getCurrentAccessToken().getToken(),
                new EventInvitationRequest(eventId, accept,
                        getRecentLocation(this,
                                (LocationManager)getSystemService(this.LOCATION_SERVICE))));

        Response<Event> resp = null;
        try {
            resp = call.execute();
        }
        catch (IOException e) {
            Log.d(TAG, "IO Exception caught");
            e.printStackTrace();
        }

        if (resp != null && resp.isSuccessful()) {
            Log.d(TAG, "Success: Response to invitation");
        }
        else {
            Log.d(TAG, "Fail: Response to invitation");
        }
    }

}
