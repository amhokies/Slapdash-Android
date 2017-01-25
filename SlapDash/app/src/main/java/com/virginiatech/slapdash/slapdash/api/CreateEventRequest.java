package com.virginiatech.slapdash.slapdash.api;

import com.virginiatech.slapdash.slapdash.DataModelClasses.Event;
import com.virginiatech.slapdash.slapdash.DataModelClasses.UserLocation;

/**
 * Created by nima on 12/5/16.
 */

public class CreateEventRequest extends Event {
    private UserLocation recentlocation;
    public CreateEventRequest(Event event, UserLocation recentlocation){
        super(event);

        this.recentlocation = recentlocation;
    }
}
