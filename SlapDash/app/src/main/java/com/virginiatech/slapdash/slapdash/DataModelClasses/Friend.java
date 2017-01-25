package com.virginiatech.slapdash.slapdash.DataModelClasses;

/**
 * Created by nima on 9/26/16.
 */

@SuppressWarnings("unused")
public class Friend {
    private String firstname;
    private String lastname;
    private String name;
    private String email;
    private String fbToken;
    private boolean invited = false;

    public Friend(String firstname,
                  String lastname,
                  String email,
                  String fbToken) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.name = firstname + " " + lastname;
        this.email = email;
        this.fbToken = fbToken;
    }

    public Friend(String name, String fbToken) {
        this.name = name;
        this.fbToken = fbToken;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFbToken() {
        return fbToken;
    }

    public void setFbToken(String fbToken) {
        this.fbToken = fbToken;
    }

    public String getFullName() {
        return this.name;
    }

    public boolean isInvited() {
        return invited;
    }

    public void setInvited(boolean invited) {
        this.invited = invited;
    }
}
