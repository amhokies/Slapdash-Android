package com.virginiatech.slapdash.slapdash.DataModelClasses;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Matt on 10/9/2016.
 */

@Data
public class User implements Serializable {

    // Fields required for creating an user
    private String _id;
    private String fbtokenid;
    private String email;
    private String androidregid;
    private String firebaseregid;
    private String[] eventsjoined;
    private InvitationStatus status;
    private UserLocation recentlocation;
    private Name name;

    public User(String firstname,
                  String lastname,
                  String email,
                  String fbtokenid,
                  String androidregid) {

        this.fbtokenid = fbtokenid;
        this.email = email;
        this.androidregid = androidregid;
        this.name = new Name(firstname, lastname);
        this.androidregid = androidregid;
    }

    public User(String firstname, String lastname, String fbToken, String androidregid) {
        this.name = new Name(firstname, lastname);
        this.fbtokenid = fbToken;
        this.androidregid = androidregid;
    }

    public String getFullName() {
        if(name == null)
            return null;

        String fullName = name.firstname != null ? name.firstname : "";
        fullName += " ";
        fullName += name.lastname != null ? name.lastname : "";
        return fullName;
    }

    public User(){}

    private class Name {

        @Getter
        @Setter
        private String firstname;

        @Getter
        @Setter
        private String lastname;

        Name(String firstname, String lastname) {
            this.firstname = firstname;
            this.lastname = lastname;
        }

        @Override
        public String toString() {
            return this.firstname + " " + this.lastname;
        }
    }

    public enum InvitationStatus {
        ADMIN, GOING, PENDING, NOTGOING;

        @Getter
        private String text;

        static {
            ADMIN.text = "Admin";
            GOING.text = "Going";
            PENDING.text = "Thinking";
            NOTGOING.text = "Not Going";
        }
    }
}
