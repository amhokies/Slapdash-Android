package com.virginiatech.slapdash.slapdash.EventRespondActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.virginiatech.slapdash.slapdash.DataModelClasses.Event;
import com.virginiatech.slapdash.slapdash.DataModelClasses.UserLocation;
import com.virginiatech.slapdash.slapdash.HelperClasses.CompatibilityHelper;
import com.virginiatech.slapdash.slapdash.R;
import com.virginiatech.slapdash.slapdash.Services.LocationService;
import com.virginiatech.slapdash.slapdash.api.EventInvitationRequest;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPI;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPIBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nelson on 10/11/2016.
 */
public class EventInvitationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_invitation);
        Intent recievedIntent = getIntent();

        Button acceptBtn, declineBtn;
        TextView titleTv, descTv, startTimeTv, adminTv;
        ImageView categoryIv;
        String eventIdTemp = "", title = "", description = "", admin = "", category = "";
        long starttimelong = 0;

        if (recievedIntent != null) {
            Object temp;
            if ((temp = recievedIntent.getStringExtra("eventid")) != null) {
                eventIdTemp = (String) temp;
            }
            if ((temp = recievedIntent.getStringExtra("title")) != null) {
                title = (String) temp;
            }
            if ((temp = recievedIntent.getStringExtra("description")) != null) {
                description = (String) temp;
            }
            if ((temp = recievedIntent.getStringExtra("admin")) != null) {
                admin = (String) temp;
            }
            if ((long) (temp = recievedIntent.getLongExtra("starttimeLong", 0)) > 0) {
                starttimelong = (long) temp;
            }
            if ((temp = recievedIntent.getStringExtra("category")) != null) {
                category = (String) temp;
            }
        }

        titleTv = (TextView) findViewById(R.id.titleTv);
        descTv = (TextView) findViewById(R.id.descTv);
        adminTv = (TextView) findViewById(R.id.adminTv);
        startTimeTv = (TextView) findViewById(R.id.starttimeTv);
        categoryIv = (ImageView) findViewById(R.id.categoryIv);
        acceptBtn = (Button) findViewById(R.id.acceptBtn);
        declineBtn = (Button) findViewById(R.id.declineBtn);

        if (titleTv != null) {
            titleTv.setText(title);
        }
        if (descTv != null) {
            descTv.setText(description);
        }
        if (adminTv != null) {
            adminTv.setText("Slapper: " + admin);
        }
        if (starttimelong > 0 && startTimeTv != null) {
            startTimeTv.setText(new SimpleDateFormat("hh:mm aa EEEE MM/dd/yy")
                    .format(new Date(starttimelong)));
        }

        Drawable categoryDrawable = getCategoryDrawable(category);
        if (categoryDrawable != null && categoryIv != null) {
            categoryIv.setImageDrawable(categoryDrawable);
        }

        final String eventId = eventIdTemp;
        if (acceptBtn != null) {
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    respondToInvitationAsync(eventId, true);
                }
            });
        }

        if (declineBtn != null) {
            declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    respondToInvitationAsync(eventId, false);
                }
            });
        }

        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(getApplicationContext());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Drawable getCategoryDrawable(String category) {
        int categroyPictureId;
        switch (category) {
            case "Food":
                categroyPictureId = R.drawable.burger_480;
                break;
            case "Drink":
                categroyPictureId = R.drawable.drink_480;
                break;
            case "Play":
                categroyPictureId = R.drawable.play_480;
                break;
            case "SlapDash":
                categroyPictureId = R.drawable.random_480;
                break;
            default:
                categroyPictureId = 0;
                break;
        }

        Drawable toReturn = null;
        if (categroyPictureId > 0) {
            toReturn = CompatibilityHelper.getDrawable(this, categroyPictureId);
        }
        return toReturn;
    }

    //---------------------------------------------------------------------------------------
    private void respondToInvitationAsync(String eventId, final boolean accept) {
        final EventRespondWait_Fragment wait = new EventRespondWait_Fragment();
        Bundle respondBundle = new Bundle();
        respondBundle.putString("eventid", eventId);
        wait.setArguments(respondBundle);
        wait.show(getFragmentManager(), "RESPOND_WAIT");

        // Create the body of request
        LocationManager locationManager = (LocationManager)
                this.getSystemService(Context.LOCATION_SERVICE);
        UserLocation userLocation = LocationService.getRecentLocation(this, locationManager);
        EventInvitationRequest req = new EventInvitationRequest(eventId, accept, userLocation);

        // Get the Facebook AccessToken
        String accessToken = AccessToken.getCurrentAccessToken().getToken();

        // Call the Api
        SlapDashAPI service = SlapDashAPIBuilder.getAPI();
        Call<Event> call = service.respondToEvent(accessToken, req);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    wait.SucceededToRespond(accept);
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn't respond to event",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}
