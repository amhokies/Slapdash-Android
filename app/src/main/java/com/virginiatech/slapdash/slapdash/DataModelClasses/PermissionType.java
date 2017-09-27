package com.virginiatech.slapdash.slapdash.DataModelClasses;

/**
 * Created by nima on 10/3/16.
 *
 * Reperesents Different permission type an event could have.
 */
enum PermissionType {
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
