package com.virginiatech.slapdash.slapdash;

/**
 * Created by nima on 10/3/16.
 */

public enum PermissionType {
    OPEN,
    CLOSED,
    APPROVAL;

    private String text;
    public String getText() {
        return text;
    }

    static {
        OPEN.text = "Open";
        CLOSED.text = "Closed";
        APPROVAL.text = "Approval";
    }
}
