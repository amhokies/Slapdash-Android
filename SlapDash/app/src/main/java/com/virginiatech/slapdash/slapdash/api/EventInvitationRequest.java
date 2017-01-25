package com.virginiatech.slapdash.slapdash.api;

import com.virginiatech.slapdash.slapdash.DataModelClasses.UserLocation;

/**
 * Created by nima on 10/23/16.
 */

public class EventInvitationRequest {
    String eventid;
    boolean accepted;
    UserLocation recentlocation;

    public EventInvitationRequest(String eventid, boolean accepted, UserLocation recentLocation) {
        this.eventid = eventid;
        this.accepted = accepted;
        this.recentlocation = recentLocation;
    }
}
