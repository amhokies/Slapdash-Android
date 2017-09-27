package com.virginiatech.slapdash.slapdash.FriendList_Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.virginiatech.slapdash.slapdash.DataModelClasses.Event;
import com.virginiatech.slapdash.slapdash.DataModelClasses.User;
import com.virginiatech.slapdash.slapdash.MainActivity;
import com.virginiatech.slapdash.slapdash.MainActivityFragmentsInterface;
import com.virginiatech.slapdash.slapdash.R;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class FriendListFragment extends Fragment implements MainActivityFragmentsInterface {
    @Getter
    private ListView friendListView;
    private FriendListAdapter adapter;
    private OnFragmentInteractionListener mListener;
    public static final String EVENT_FRAGMENT_TAG = "EVENTFRAGMENT";

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                 Public Methods                                          //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public FriendListFragment() {
        // Required empty public constructor
    }

    //--------------------------------------------------------------------------------------------
    public static FriendListFragment newInstance() {
        return new FriendListFragment();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                    Interface                                            //
    /////////////////////////////////////////////////////////////////////////////////////////////
    public interface OnFragmentInteractionListener {

    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                     Overrides                                           //
    /////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new FriendListAdapter(this.getContext(), new ArrayList<User>());
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View createdView = inflater.inflate(R.layout.fragment_friend_list, container, false);

        friendListView = (ListView) createdView.findViewById(R.id.friend_list_view);

        friendListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView,
                                             int scrollState) { /* Intentionally empty */ }

            @Override
            public void onScroll(AbsListView absListView,
                                 int firstVisibleItem,
                                 int visibleItemCount,
                                 int totalItemCount) {
                Log.d(MainActivity.DEBUG, "firstVisibleItem =" + firstVisibleItem);
            }
        });
        friendListView.getFirstVisiblePosition();
        friendListView.setAdapter(adapter);
        friendListView.setDivider(null);
        return createdView;
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public boolean onCurrentMainEventChanged(Event newCurrentEvent) {
        adapter.clear();
        addAdmin(newCurrentEvent.getAdmin());
        addAttended(newCurrentEvent.getAttendees());
        addInvited(newCurrentEvent.getInvitations());
        return true;
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public boolean onCurrentEventPlaceChanged(Place newPlace) {
        return false;
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public boolean onCurrentEventPictureChanged(PlacePhotoMetadataBuffer buffer,
                                                GoogleApiClient mGoogleApiClient) {
        return false;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                Private Methods                                          //
    /////////////////////////////////////////////////////////////////////////////////////////////

    private void addAdmin(User admin) {
        if (admin == null) return;
        admin.setStatus(User.InvitationStatus.ADMIN);
        adapter.add(admin);
    }

    //--------------------------------------------------------------------------------------------
    private void addAttended(List<User> attendees) {
        if (attendees == null) return;
        for (User user : attendees) {
            user.setStatus(User.InvitationStatus.GOING);
            adapter.add(user);
        }
    }

    //--------------------------------------------------------------------------------------------
    private void addInvited(List<User> invitees) {
        if (invitees == null) return;
        for (User user : invitees) {
            user.setStatus(User.InvitationStatus.PENDING);
            adapter.add(user);
        }
    }

    //--------------------------------------------------------------------------------------------
    private void addDeclined(List<User> declinedList) {
        if (declinedList == null) return;
        for (User user : declinedList) {
            user.setStatus(User.InvitationStatus.NOTGOING);
            adapter.add(user);
        }
    }
}
