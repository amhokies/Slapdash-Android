package com.virginiatech.slapdash.slapdash.EventRespondActivity;

import android.Manifest;
import android.location.Location;
import android.location.LocationManager;
import android.location.Criteria;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.virginiatech.slapdash.slapdash.DataModelClasses.Event;
import com.virginiatech.slapdash.slapdash.MainActivity;
import com.virginiatech.slapdash.slapdash.R;
import com.virginiatech.slapdash.slapdash.DataModelClasses.UserLocation;
import com.virginiatech.slapdash.slapdash.api.EventInvitationRequest;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPI;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPIBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
                Log.d(MainActivity.DEBUG, temp.toString());
            }
            if ((temp = recievedIntent.getStringExtra("title")) != null) {
                title = (String) temp;
                Log.d(MainActivity.DEBUG, temp.toString());
            }
            if ((temp = recievedIntent.getStringExtra("description")) != null) {
                description = (String) temp;
                Log.d(MainActivity.DEBUG, temp.toString());
            }
            if ((temp = recievedIntent.getStringExtra("admin")) != null) {
                admin = (String) temp;
                Log.d(MainActivity.DEBUG, temp.toString());
            }
            if ((long)(temp = recievedIntent.getLongExtra("starttimeLong", 0)) > 0) {
                starttimelong = (long) temp;
                Log.d(MainActivity.DEBUG, temp.toString());
            }
            if ((temp = recievedIntent.getStringExtra("category")) != null) {
                category = (String) temp;
                Log.d(MainActivity.DEBUG, temp.toString());
            }
        }

        titleTv = (TextView) findViewById(R.id.titleTv);
        descTv = (TextView) findViewById(R.id.descTv);
        adminTv = (TextView) findViewById(R.id.adminTv);
        startTimeTv = (TextView) findViewById(R.id.starttimeTv);
        categoryIv = (ImageView) findViewById(R.id.categoryIv);
        acceptBtn = (Button) findViewById(R.id.acceptBtn);
        declineBtn = (Button) findViewById(R.id.declineBtn);

        if(titleTv != null) {
            titleTv.setText(title);
        }
        if(descTv != null) {
            descTv.setText(description);
        }
        if(adminTv != null) {
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
        if(acceptBtn != null){
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    respondToInvitationAsync(eventId, true);
                }
            });
        }

        if(declineBtn != null){
            declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    respondToInvitationAsync(eventId, false);
                }
            });
        }

        if(!FacebookSdk.isInitialized()){
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(getApplication());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Nullable
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toReturn = getResources().getDrawable(categroyPictureId, getTheme());
            } else {
                toReturn = getResources().getDrawable(categroyPictureId);
            }
        }
        return toReturn;
    }

    private void respondToInvitationAsync(String eventId, final boolean accept) {
        final EventRespondWait_Fragment wait = new EventRespondWait_Fragment();
        Bundle respondBundle = new Bundle();
        respondBundle.putString("eventid", eventId);
        wait.setArguments(respondBundle);
        wait.show(getFragmentManager() ,"RESPOND_WAIT");
        SlapDashAPI service = SlapDashAPIBuilder.getAPI();
        Call<Event> call = service.respondToEvent(
                AccessToken.getCurrentAccessToken().getToken(),
                new EventInvitationRequest(eventId, accept, getRecentLocation()));

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if(response.isSuccessful()){
                    Log.d(MainActivity.DEBUG,"Successful Responce to invitation");
                    wait.SucceededToRespond(accept);
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn't respond to event",
                            Toast.LENGTH_SHORT);
                }

                Log.d(MainActivity.DEBUG, "Respond from invitationRespond API was " +
                        response.code());
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong when responding to event",
                        Toast.LENGTH_SHORT);
                t.printStackTrace();
            }
        });

    }

    private UserLocation getRecentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            if (bestProvider != null) {
                Location location = locationManager.getLastKnownLocation(bestProvider);
                if (location != null) {
                    return new UserLocation(location.getLatitude(), location.getLongitude());
                }
            }
        }
        return null;
    }
}
