package com.virginiatech.slapdash.slapdash.FireBaseService;

/**
 * Created by Matt on 10/23/2016.
 */

public enum FCMType {
    INVITATION, DECISION;

    private String text;
    public String getText() {
        return text;
    }

    static {
        INVITATION.text = "INVITATION";
        DECISION.text = "DECISION";
    }
}
