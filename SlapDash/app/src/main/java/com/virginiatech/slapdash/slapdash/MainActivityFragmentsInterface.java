package com.virginiatech.slapdash.slapdash;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.virginiatech.slapdash.slapdash.DataModelClasses.Event;

/**
 * Created by nima on 10/21/16.
 */

public interface MainActivityFragmentsInterface {
    boolean onCurrentMainEventChanged(Event newCurrentEvent);
    boolean onCurrentEventPlaceChanged(Place newPlace);
    boolean onCurrentEventPictureChanged(PlacePhotoMetadataBuffer buffer, GoogleApiClient mGoogleApiClient);
}
