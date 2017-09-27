package com.virginiatech.slapdash.slapdash.DataModelClasses;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Created by nima on 9/30/16.
 */

@Data
@SuppressWarnings("unused")
public class Event{

    private Object _id;
    private Object id;
    private String title;
    private String description;
    private User admin;
    private String category;
    private String userpermissions;

    private String yelpid;

    private int capacity;
    private int timeout;

    private String starttime;
    private String createtime;
    private long starttimeLong;
    private long createtimeLong;

    private List<User> invitations;
    private List<User> attendees;

    private boolean canceled;

    public Event(){
        invitations = new ArrayList<>();
        attendees = new ArrayList<>();
        timeout = 1;
        userpermissions = PermissionType.OPEN.getText();
        canceled = false;
    }

    public Event(Event newEvent){
        this._id = newEvent._id;
        this.id = newEvent.id;
        this.title = newEvent.title;
        this.description = newEvent.description;
        this.admin = newEvent.admin;
        this.category = newEvent.category;
        this.userpermissions = newEvent.userpermissions;
        this.yelpid = newEvent.yelpid;
        this.capacity = newEvent.capacity;
        this.timeout = newEvent.timeout;
        this.starttime = newEvent.starttime;
        this.createtime = newEvent.createtime;
        this.starttimeLong = newEvent.starttimeLong;
        this.createtimeLong = newEvent.createtimeLong;
        this.invitations = newEvent.invitations;
        this.attendees = newEvent.attendees;
        this.canceled = newEvent.canceled;
    }

    public String getCategory(){return category;}
    public String getYelpId(){return yelpid;}
    public boolean inviteFriend(String friendId){
        if(isUserInList(invitations, friendId) >= 0){
            return false;
        } else {
            User invitedFriend = new User();
            invitedFriend.setFbtokenid(friendId);
            invitations.add(invitedFriend);
            return true;
        }
    }

    public boolean uninviteFriend(String friendId) {
        int index;
        if(( index = isUserInList(invitations, friendId)) >= 0){
            invitations.remove(index);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString(){
        return new StringBuilder().append("Event:")
            .append("\n\t_id: ").append(id)
            .append("\n\tTitle: ").append(title)
            .append("\n\tDescription: ").append(description)
            .append("\n\tAdmin: ").append(admin)
            .append("\n\tCategory: ").append(category)
            .append("\n\tUserPermissions: ").append(userpermissions)
            .append("\n\tIsCanceled: ").append(canceled)
            .append("\n\tStartTime: ").append(starttime)
            .append("\n\tTimeout: ").append(timeout)
            .append("\n\tInvitationSize: ").append(invitations.size())
            .toString();
    }

    /* -1 if doesn't exist, index otherwise */
    private int isUserInList(List<User> list, String fbId){
        int size = invitations.size();
        for(int i = 0; i < size; i++){
            if(invitations.get(i).getFbtokenid().equals(fbId)){
                return i;
            }
        }
        return -1;
    }
}
