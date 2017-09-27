package com.virginiatech.slapdash.slapdash.Services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;

import com.virginiatech.slapdash.slapdash.DataModelClasses.UserLocation;

/**
 * Created by nima on 12/5/16.
 * <p>
 * Delegate the task of getting the user location to this class.
 * This function was used in many places therefore was made as a
 * service.
 */

public class LocationService {
    public static UserLocation getRecentLocation(Context context, LocationManager locationManager) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
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
