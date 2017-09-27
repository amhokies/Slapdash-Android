package com.virginiatech.slapdash.slapdash.EventRespondActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.virginiatech.slapdash.slapdash.MainActivity;
import com.virginiatech.slapdash.slapdash.R;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.virginiatech.slapdash.slapdash.DataModelClasses.EventType.DRINK;
import static com.virginiatech.slapdash.slapdash.DataModelClasses.EventType.FOOD;
import static com.virginiatech.slapdash.slapdash.DataModelClasses.EventType.PLAY;
import static com.virginiatech.slapdash.slapdash.DataModelClasses.EventType.RANDOM;

public class EventViewActivity extends AppCompatActivity {

    private static final String TAG = "EVENT-VIEW";
    GoogleApiClient mGoogleApiClient;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_notification_layout);

         mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();

        String title = "", description = "", category = "", place = "", location = "", imageUrl =  "";
        long starttimelong = 0;
        Intent recievedIntent = getIntent();

        if (recievedIntent != null) {
            Object temp;
            if ((temp = recievedIntent.getStringExtra("eventTitle")) != null) {
                title = (String) temp;
                Log.d(TAG, temp.toString());
            }
            if ((temp = recievedIntent.getStringExtra("eventDescription")) != null) {
                description = (String) temp;
                Log.d(TAG, temp.toString());
            }
            if ((temp = recievedIntent.getStringExtra("category")) != null) {
                category = (String) temp;
                Log.d(TAG, temp.toString());
            }
            if ((temp = recievedIntent.getStringExtra("name")) != null) {
                place = (String) temp;
                Log.d(TAG, temp.toString());
            }
            if ((temp = recievedIntent.getStringExtra("address")) != null) {
                location = (String) temp;
                Log.d(TAG, temp.toString());
            }
            if ((temp = recievedIntent.getStringExtra("imageUrl")) != null) {
                imageUrl = (String) temp;
                Log.d(TAG, temp.toString());
            }
            if ((long)(temp = recievedIntent.getLongExtra("starttimeLong", 0)) > 0) {
                starttimelong = (long) temp;
                Log.d(MainActivity.DEBUG, temp.toString());
            }
        }

        TextView titleTv, descTv, startTimeTv, placeTv, locationTv;
        titleTv = (TextView) findViewById(R.id.event_notification_title);
        descTv = (TextView) findViewById(R.id.event_notification_description);
        startTimeTv = (TextView) findViewById(R.id.event_notification_starttime);
        placeTv = (TextView) findViewById(R.id.event_notification_place);
        locationTv = (TextView) findViewById(R.id.event_notification_location);

        mImageView = (ImageView) findViewById(R.id.event_notification_image_view);

        if (category.equals(FOOD.getText())) {
            mImageView.setImageResource(R.drawable.burger_128);
        } else if (category.equals(DRINK.getText())) {
            mImageView.setImageResource(R.drawable.drink_128);
        } else if (category.equals(PLAY.getText())) {
            mImageView.setImageResource(R.drawable.play_128);
        } else if (category.equals(RANDOM.getText())) {
            mImageView.setImageResource(R.drawable.random_128);
        } else {
            // error? this should never happen
            Log.d(TAG, "invalid category:" + category);
        }

        if(titleTv != null) {
            titleTv.setText(title);
            Log.d(TAG, "Set title to: " + title);
        }
        if(descTv != null) {
            descTv.setText(description);
            Log.d(TAG, "Set description to: " + description);
        }
        if(placeTv != null) {
            placeTv.setText(place);
            Log.d(TAG, "Set place to: " + place);
        }
        if(locationTv != null) {
            locationTv.setText(location);
            Log.d(TAG, "Set location to: " + location);
        }

        if (starttimelong > 0 && startTimeTv != null) {
            startTimeTv.setText(new SimpleDateFormat("hh:mm aa EEEE MM/dd/yy")
                    .format(new Date(starttimelong)));
        }

        if (!StringUtils.isBlank(imageUrl)) {
            //Picasso.with(getApplicationContext()).load(imageUrl).fit().into(mImageView);
            try {
                File f = new File(imageUrl, "profile.jpg");
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                mImageView.setImageBitmap(b);
                Log.d(TAG, "image has been set");
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        else {
            Log.d(TAG, "imageUrl is blank");
        }

        if(!FacebookSdk.isInitialized()){
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(getApplication());
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EventViewActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
