package com.virginiatech.slapdash.slapdash.FireBaseService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.virginiatech.slapdash.slapdash.EventRespondActivity.EventInvitationActivity;
import com.squareup.picasso.Picasso;
import com.virginiatech.slapdash.slapdash.EventRespondActivity.EventViewActivity;
import com.virginiatech.slapdash.slapdash.MainActivity;
import com.virginiatech.slapdash.slapdash.R;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static com.virginiatech.slapdash.slapdash.FireBaseService.FCMType.DECISION;
import static com.virginiatech.slapdash.slapdash.FireBaseService.FCMType.INVITATION;
import static com.virginiatech.slapdash.slapdash.Map_Fragment.EventType.DRINK;
import static com.virginiatech.slapdash.slapdash.Map_Fragment.EventType.FOOD;
import static com.virginiatech.slapdash.slapdash.Map_Fragment.EventType.PLAY;
import static com.virginiatech.slapdash.slapdash.Map_Fragment.EventType.RANDOM;

/**
 * Created by nima on 10/13/16.
 */

public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FIREBASE-MESSAGING";
    private static final String ACTION_BAR_ACCEPT = "ACCEPT";
    private static final String ACTION_BAR_DECLINE = "DECLINE";
    private GoogleApiClient mGoogleApiClient;
    //private Place place;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .build();

            Log.d(TAG, "GoogleApiClient built");
        }
        else {
            Log.d(TAG, "Retrieved GoogleApiClient");
        }

        Map<String, String> data = remoteMessage.getData();

        // Check if message contains a data payload.
        if (data.size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // TEAL
        int tealColor = Color.parseColor("#009688");

        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(getApplicationContext())
                .setAutoCancel(true)
                .setSound(Uri.parse("android.resource://com.virginiatech.slapdash.slapdash/" + R.raw.slap_sound))
                .setVibrate(new long[]{100, 2000, 500, 2000})
                .setLights(tealColor, 400, 400)
                .setColor(tealColor);

        Class redirectActivity = getRedirectActivity(data);
        Intent intent = new Intent(this, redirectActivity);

        if (redirectActivity == EventInvitationActivity.class) {

            // build the base invitation notification and intent
            setInvitationNotificationBuilder(builder, data);
            InvitationNotificationIntentBuild(intent, data);

            // build the accept action bar intent
            Intent invitationAcceptIntent = new Intent(this, InvitationActionBarBackgroundService.class);
            InvitationNotificationIntentBuild(invitationAcceptIntent, data);
            invitationAcceptIntent.setAction(ACTION_BAR_ACCEPT);
            PendingIntent invitationAcceptPendingIntent = PendingIntent.getService(this, 0, invitationAcceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // TODO: Add a better check-mark image for accepting an invitation
            builder.addAction(R.mipmap.ic_check_mark, "Accept", invitationAcceptPendingIntent);

            // build the decline action bar intent
            Intent invitationDeclineIntent = new Intent(this, InvitationActionBarBackgroundService.class);
            InvitationNotificationIntentBuild(invitationDeclineIntent, data);
            invitationDeclineIntent.setAction(ACTION_BAR_DECLINE);
            PendingIntent invitationDeclinePendingIntent = PendingIntent.getService(this, 1, invitationDeclineIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // TODO: Add a better "X" image for declining an invitation
            builder.addAction(R.mipmap.ic_action_decline, "Decline", invitationDeclinePendingIntent);
        }
        else if (redirectActivity == EventViewActivity.class) {

            //Log.d(TAG, "EventViewActivity");
            Place place = getPlace(data);
            //Log.d(TAG, "getPlace has been called");
            if (place == null) {
                // do something
                Log.d(TAG, "Could not retrieve place");
            }
            else {
                intent.putExtra("place_id", place.getId());
                setDecisionNotificationBuilder(builder, data, intent, place);
                DecisionNotificationIntentBuild(intent, data, place);

                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle(builder);
                setInboxStyleProperties(builder, inboxStyle, intent, place);
            }
        }
        else {
            // error? this should never happen
            Log.d(TAG, "invalid class: " + redirectActivity);
        }

        String category = data.get("category");
        if (category == null) {
            // error? should never be null
            Log.d(TAG, "type is null");
        }

        if (category.equals(FOOD.getText())) {
            builder.setSmallIcon(R.drawable.burger_128);
        } else if (category.equals(DRINK.getText())) {
            builder.setSmallIcon(R.drawable.drink_128);
        } else if (category.equals(PLAY.getText())) {
            builder.setSmallIcon(R.drawable.play_128);
        } else if (category.equals(RANDOM.getText())) {
            builder.setSmallIcon(R.drawable.random_128);
        } else {
            // error? this should never happen
            Log.d(TAG, "invalid category:" + category);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent eventToRespond = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(eventToRespond);

        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // TODO: We should make the first argument (the request code) a unique id
        nManager.notify(0, builder.build());

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private Place getPlace(Map<String, String> data) {
        
        String placeId = data.get("place_id");
        if (!StringUtils.isBlank(placeId)) {

            Log.d(TAG, "placeId: " + placeId);

            mGoogleApiClient.connect();
            PlaceBuffer places = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId).await();

            Log.d(TAG, places.toString());

            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                Place place = places.get(0);
                Log.d(TAG, "Place found: " + place.getName());
                return place;

            } else {
                Log.d(TAG, "Place not found");
            }
            places.release();

            mGoogleApiClient.disconnect();
        }
        else {
            Log.d(TAG, "place_id was blank");
        }

        return null;
    }

    private void setInboxStyleProperties(final NotificationCompat.Builder builder, NotificationCompat.InboxStyle inboxStyle, Intent intent, Place place) {

        CharSequence charName = place.getName();
        if (charName != null) {
            String name = charName.toString();
            Log.d(TAG, "Name: " + name);
            if (!StringUtils.isBlank(name)) {
                builder.setContentText(name);
                inboxStyle.addLine(name); // Name of Place
                intent.putExtra("name", name);
            }
        }

        float rating = place.getRating();
        if (rating > -1) {
            String ratingString = "Rating: " + rating;
            Log.d(TAG, ratingString);
            inboxStyle.addLine(ratingString); // Rating of Place
            intent.putExtra("rating", ratingString);
        }

        CharSequence charAddress = place.getAddress();
        if (charAddress != null) {
            String address = charAddress.toString();
            Log.d(TAG, "Address: "+ address);
            if (!StringUtils.isBlank(address)) {
                inboxStyle.addLine(address); // Address of Place
                intent.putExtra("address", address);
            }
        }

        CharSequence charPhoneNumber = place.getPhoneNumber();
        if (charPhoneNumber != null) {
            String phoneNumber = charPhoneNumber.toString();
            Log.d(TAG, "Phone #: " + phoneNumber);
            if (!StringUtils.isBlank(phoneNumber)) {
                inboxStyle.addLine(phoneNumber); // Address of Place
                intent.putExtra("phone", phoneNumber);
            }
        }
    }

    private void setInvitationNotificationBuilder(final NotificationCompat.Builder builder, Map<String, String> data) {

        String admin = data.get("admin");
        if (!StringUtils.isBlank(admin)) {

            builder.setContentTitle("Invitation from " + admin);

            String adminFbTokenId = data.get("adminFbTokenId");
            if (!StringUtils.isBlank(admin)) {

                String adminFbProfilePicUrl = "https://graph.facebook.com/v2.8/" + adminFbTokenId + "/picture?type=normal";
                Bitmap adminFbProfilePic = null;
                try {
                    adminFbProfilePic = Picasso.with(getApplicationContext()).load(adminFbProfilePicUrl).get();
                    builder.setLargeIcon(adminFbProfilePic);
                }
                catch (IOException e) {
                    Log.d(TAG, "Picasso failed to get " + adminFbProfilePicUrl);
                }
            }
            else {
                // error? this should never happen
                Log.d(TAG, "adminFbTokenId is null");
            }
        }
        else {
            // error? this should never happen
            Log.d(TAG, "admin is null");
        }

        String eventTitle = data.get("title");
        if (!StringUtils.isBlank(eventTitle)) {
            builder.setContentText("You have been invited to " + eventTitle);
        }
        else {
            // error? this should never happen
            Log.d(TAG, "eventTitle is null");
        }

    }

    private void setDecisionNotificationBuilder(final NotificationCompat.Builder builder, Map<String, String> data, final Intent intent, Place place) {

        String placeId = place.getId();

        // Get a PlacePhotoMetadataResult containing metadata for the first 10 photos.
        PlacePhotoMetadataResult result = Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId).await();

        // Get a PhotoMetadataBuffer instance containing a list of photos (PhotoMetadata).
        if (result != null && result.getStatus().isSuccess()) {

            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0) {
                // Get the first photo in the list.
                PlacePhotoMetadata photo = photoMetadataBuffer.get(0);

                Bitmap largeImage = photo.getScaledPhoto(mGoogleApiClient, 400, 400).await().getBitmap();
                String loc = saveToInternalStorage(largeImage);
                //Picasso.with(getApplicationContext()).load(loc).fetch();
                intent.putExtra("imageUrl", loc);

                // Get a full-size bitmap for the photo.
                //Bitmap image = photo.getPhoto(mGoogleApiClient).await().getBitmap();
                builder.setLargeIcon(largeImage);
            }

            photoMetadataBuffer.release();
        }

        String eventTitle = data.get("eventTitle");
        Log.d(TAG, "eventTitle: " + eventTitle);
        if (!StringUtils.isBlank(eventTitle)) {
            builder.setContentTitle(eventTitle);
            intent.putExtra("eventTitle", eventTitle);
        }
    }

//    private String bitmapLoc = null;
//    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback
//            = new ResultCallback<PlacePhotoResult>() {
//        @Override
//        public void onResult(PlacePhotoResult placePhotoResult) {
//            if (!placePhotoResult.getStatus().isSuccess()) {
//                Log.d(TAG, "getting first photo failed :(");
//                return;
//            }
//
//            Bitmap bitmap = placePhotoResult.getBitmap();
//            Log.d(TAG, "obtained bitmap");
//
//            bitmapLoc = saveToInternalStorage(bitmap);
//        }
//    };

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private Class getRedirectActivity(Map<String, String> data) {

        String type = data.get("type");
        Class redirectActivity = null;
        if (type.equals(INVITATION.getText())) {
            redirectActivity = EventInvitationActivity.class;
        }
        else if (type.equals(DECISION.getText())){
            redirectActivity = EventViewActivity.class;
        }
        else {
            // error? this should never happen
            Log.d(TAG, "invalid type: " + type);
        }

        return redirectActivity;
    }

    private void InvitationNotificationIntentBuild(Intent intent, Map<String, String> data){
        intent.putExtra("title", data.get("title"));
        intent.putExtra("description", data.get("description"));
        intent.putExtra("starttimeLong", Long.parseLong(data.get("starttimeLong")));
        intent.putExtra("category", data.get("category"));
        intent.putExtra("eventid", data.get("_id"));
        intent.putExtra("admin", data.get("admin"));
    }

    private void DecisionNotificationIntentBuild(Intent intent, Map<String, String> data, Place place){
        intent.putExtra("eventId", data.get("eventId"));
        intent.putExtra("eventDescription", data.get("eventDescription"));
        intent.putExtra("starttimeLong", Long.parseLong(data.get("starttimeLong")));
        intent.putExtra("category", data.get("category"));
    }
}
