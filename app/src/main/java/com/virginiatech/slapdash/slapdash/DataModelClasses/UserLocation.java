package com.virginiatech.slapdash.slapdash.DataModelClasses;

import lombok.Data;
import lombok.Getter;

/**
 * Created by Matt on 10/11/2016.
 */

@Data
public class UserLocation {
    @Getter
    private double lat;
    @Getter
    private double lon;

    public UserLocation(double newLat, double newLon){
        lat = newLat;
        lon = newLon;
    }
}
