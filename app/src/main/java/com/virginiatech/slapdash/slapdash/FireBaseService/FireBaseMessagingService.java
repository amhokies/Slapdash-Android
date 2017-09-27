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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.virginiatech.slapdash.slapdash.EventRespondActivity.EventInvitationActivity;
import com.virginiatech.slapdash.slapdash.EventRespondActivity.EventViewActivity;
import com.virginiatech.slapdash.slapdash.MainActivity;
import com.virginiatech.slapdash.slapdash.R;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static com.virginiatech.slapdash.slapdash.DataModelClasses.EventType.DRINK;
import static com.virginiatech.slapdash.slapdash.DataModelClasses.EventType.FOOD;
import static com.virginiatech.slapdash.slapdash.DataModelClasses.EventType.PLAY;
import static com.virginiatech.slapdash.slapdash.DataModelClasses.EventType.RANDOM;
import static com.virginiatech.slapdash.slapdash.FireBaseService.FCMType.DECISION;
import static com.virginiatech.slapdash.slapdash.FireBaseService.FCMType.INVITATION;
import static com.virginiatech.slapdash.slapdash.FireBaseService
        .InvitationActionBarBackgroundService.ACTION_BAR_ACCEPT;
import static com.virginiatech.slapdash.slapdash.FireBaseService
        .InvitationActionBarBackgroundService.ACTION_BAR_DECLINE;

/**
 * Created by Nima on 10/13/16.
 *
 * The service inherited from FirebaseMessagingService that will run in
 * background to detect a push notification from Firebase. Based on the
 * notification field, it will parse the data and configures the right
 * notification to be posted.
 */

public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FIREBASE-MESSAGING";
    private GoogleApiClient mGoogleApiClient;

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                  Overrides                                              //
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        Map<String, String> rmMap = remoteMessage.getData();
        TaskStackBuilder builder = TaskStackBuilder.create(this);
        GenerateProperNotification(rmMap, builder);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                Private Methods                                          //
    /////////////////////////////////////////////////////////////////////////////////////////////

    private void GenerateProperNotification(Map<String, String> data,
                                            TaskStackBuilder stackBuilder) {
        Context context = getApplicationContext();

        // TEAL
        int tealColor = Color.parseColor("#009688");

        // SlapSound
        Uri slapSoundUri = Uri.parse("android.resource://"
                + context.getPackageName()
                + "/"
                + R.raw.slap_sound);

        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(context)
                .setAutoCancel(true)
                .setSound(slapSoundUri)
                .setVibrate(new long[]{100, 2000, 500, 2000})
                .setLights(tealColor, 400, 400)
                .setColor(tealColor);

        // Find the proper action to take based on the notification type
        Class redirectActivity = getRedirectActivity(data);
        Intent intent = new Intent(this, redirectActivity);

        if (redirectActivity == EventInvitationActivity.class) {
            SetupEventCreatedNotificationAction(builder, data, intent);   // Accept or Deny Event
        } else if (redirectActivity == EventViewActivity.class) {
            SetupEventViewNotificationAction(builder, data, intent);      // View Event
        }

        builder.setSmallIcon(getEventCategoryIconFromDataMap(data));

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent eventToRespond = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(eventToRespond);

        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // TODO: Should make the first argument (the request code) a unique id
        nManager.notify(0, builder.build());
    }

    //--------------------------------------------------------------------------------------------
    private void SetupEventCreatedNotificationAction(NotificationCompat.Builder builder,
                                                           Map<String, String> data,
                                                           Intent intent) {
        // build the base invitation notification and intent
        setInvitationNotificationBuilder(builder, data);
        InvitationNotificationIntentBuild(intent, data);

        // build the accept action bar intent
        Intent invitationAcceptIntent = new Intent(this,
                InvitationActionBarBackgroundService.class);

        InvitationNotificationIntentBuild(invitationAcceptIntent, data);
        invitationAcceptIntent.setAction(ACTION_BAR_ACCEPT);
        PendingIntent invitationAcceptPendingIntent = PendingIntent
                .getService(this, 0, invitationAcceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // TODO: Add a better check-mark image for accepting an invitation, Done?
        builder.addAction(R.mipmap.ic_check_mark, "Accept", invitationAcceptPendingIntent);

        // build the decline action bar intent
        Intent invitationDeclineIntent = new Intent(this,
                InvitationActionBarBackgroundService.class);

        InvitationNotificationIntentBuild(invitationDeclineIntent, data);
        invitationDeclineIntent.setAction(ACTION_BAR_DECLINE);
        PendingIntent invitationDeclinePendingIntent = PendingIntent
                .getService(this, 1, invitationDeclineIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // TODO: Add a better "X" image for declining an invitation, Done?
        builder.addAction(R.mipmap.ic_action_decline, "Decline", invitationDeclinePendingIntent);
    }

    //--------------------------------------------------------------------------------------------
    private int getEventCategoryIconFromDataMap(Map<String, String> data) {
        String category = data.get("category");
        int smallIcon;
        if (category == null) {                                     // Default to Food
            smallIcon = R.drawable.burger_128;
        } else if (category.equals(FOOD.getText())) {               // Food
            smallIcon = R.drawable.burger_128;
        } else if (category.equals(DRINK.getText())) {              // Drink
            smallIcon = R.drawable.drink_128;
        } else if (category.equals(PLAY.getText())) {               // Play
            smallIcon = R.drawable.play_128;
        } else if (category.equals(RANDOM.getText())) {             // SlapDash
            smallIcon = R.drawable.random_128;
        } else {                                                    // Default to Food
            // TODO: Update if the category was extended
            smallIcon = R.drawable.burger_128;
        }
        return smallIcon;
    }

    //--------------------------------------------------------------------------------------------
    private void SetupEventViewNotificationAction(NotificationCompat.Builder builder,
                                                    Map<String, String> data,
                                                    Intent intent) {
        Place place = getPlace(data);
        if (place != null) {
            intent.putExtra("place_id", place.getId());
            setDecisionNotificationBuilder(builder, data, intent, place);
            DecisionNotificationIntentBuild(intent, data);

            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle(builder);

            setInboxStyleProperties(builder, inboxStyle, intent, place);
        }
    }

    //--------------------------------------------------------------------------------------------
    private Place getPlace(Map<String, String> data) {

        String placeId = data.get("place_id");
        if (!StringUtils.isBlank(placeId)) {
            mGoogleApiClient.connect();

            PlaceBuffer places = Places
                    .GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId)
                    .await();

            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                return places.get(0);
            }

            places.release();
            mGoogleApiClient.disconnect();
        }
        return null;
    }

    //--------------------------------------------------------------------------------------------
    private void setInboxStyleProperties(final NotificationCompat.Builder builder,
                                         NotificationCompat.InboxStyle inboxStyle,
                                         Intent intent,
                                         Place place) {
        CharSequence charName = place.getName();
        if (charName != null) {
            String name = charName.toString();
            if (!StringUtils.isBlank(name)) {
                builder.setContentText(name);
                inboxStyle.addLine(name); // Name of Place
                intent.putExtra("name", name);
            }
        }

        float rating = place.getRating();
        if (rating >= 0 && rating <= 5) {
            String ratingString = "Rating: " + rating;
            inboxStyle.addLine(ratingString); // Rating of Place
            intent.putExtra("rating", ratingString);
        }

        CharSequence charAddress = place.getAddress();
        if (charAddress != null) {
            String address = charAddress.toString();
            if (!StringUtils.isBlank(address)) {
                inboxStyle.addLine(address); // Address of Place
                intent.putExtra("address", address);
            }
        }

        CharSequence charPhoneNumber = place.getPhoneNumber();
        if (charPhoneNumber != null) {
            String phoneNumber = charPhoneNumber.toString();
            if (!StringUtils.isBlank(phoneNumber)) {
                inboxStyle.addLine(phoneNumber); // Address of Place
                intent.putExtra("phone", phoneNumber);
            }
        }
    }

    //--------------------------------------------------------------------------------------------
    private void setInvitationNotificationBuilder(final NotificationCompat.Builder builder,
                                                  Map<String, String> data) {

        String admin = data.get("admin");
        if (!StringUtils.isBlank(admin)) {
            builder.setContentTitle("Invitation from " + admin);

            String adminFbTokenId = data.get("adminFbTokenId");
            if (!StringUtils.isBlank(admin)) {

                String adminFbProfilePicUrl =
                        "https://graph.facebook.com/v2.8/"
                                + adminFbTokenId
                                + "/picture?type=normal";

                try {
                    Bitmap adminFbProfilePic = Picasso.with(getApplicationContext())
                            .load(adminFbProfilePicUrl).get();
                    builder.setLargeIcon(adminFbProfilePic);
                } catch (IOException e) {
                    Log.e(TAG, "Picasso failed to get " + adminFbProfilePicUrl);
                }
            }
        }

        String eventTitle = data.get("title");
        if (!StringUtils.isBlank(eventTitle)) {
            builder.setContentText("You have been invited to " + eventTitle);
        }
    }

    //--------------------------------------------------------------------------------------------
    private void setDecisionNotificationBuilder(final NotificationCompat.Builder builder,
                                                Map<String, String> data,
                                                final Intent intent,
                                                Place place) {
        String placeId = place.getId();

        // Get a PlacePhotoMetadataResult containing metadata for the first 10 photos.
        PlacePhotoMetadataResult result = Places
                .GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId)
                .await();

        // Get a PhotoMetadataBuffer instance containing a list of photos (PhotoMetadata).
        if (result.getStatus().isSuccess()) {

            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0) {
                // Get the first photo in the list.
                PlacePhotoMetadata photo = photoMetadataBuffer.get(0);

                Bitmap largeImage = photo
                        .getScaledPhoto(mGoogleApiClient, 400, 400)
                        .await()
                        .getBitmap();

                String loc = saveToInternalStorage(largeImage);
                intent.putExtra("imageUrl", loc);

                // Get a full-size bitmap for the photo.
                builder.setLargeIcon(largeImage);
            }
            photoMetadataBuffer.release();
        }

        String eventTitle = data.get("eventTitle");
        if (!StringUtils.isBlank(eventTitle)) {
            builder.setContentTitle(eventTitle);
            intent.putExtra("eventTitle", eventTitle);
        }
    }

    //--------------------------------------------------------------------------------------------
    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    //--------------------------------------------------------------------------------------------
    private Class getRedirectActivity(Map<String, String> data) {

        String type = data.get("type");
        Class redirectActivity = null;
        if (type.equals(INVITATION.getText())) {
            redirectActivity = EventInvitationActivity.class;
        } else if (type.equals(DECISION.getText())) {
            redirectActivity = EventViewActivity.class;
        } else {
            // TODO: Add here when notification system gets updated
        }

        return redirectActivity;
    }

    //--------------------------------------------------------------------------------------------
    private void InvitationNotificationIntentBuild(Intent intent, Map<String, String> data) {
        intent.putExtra("title", data.get("title"));
        intent.putExtra("description", data.get("description"));
        intent.putExtra("starttimeLong", Long.parseLong(data.get("starttimeLong")));
        intent.putExtra("category", data.get("category"));
        intent.putExtra("eventid", data.get("_id"));
        intent.putExtra("admin", data.get("admin"));
    }

    //--------------------------------------------------------------------------------------------
    private void DecisionNotificationIntentBuild(Intent intent, Map<String, String> data) {
        intent.putExtra("eventId", data.get("eventId"));
        intent.putExtra("eventDescription", data.get("eventDescription"));
        intent.putExtra("starttimeLong", Long.parseLong(data.get("starttimeLong")));
        intent.putExtra("category", data.get("category"));
    }
}
