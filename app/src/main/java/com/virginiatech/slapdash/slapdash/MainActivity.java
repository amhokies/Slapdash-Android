package com.virginiatech.slapdash.slapdash;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.virginiatech.slapdash.slapdash.DataModelClasses.Event;
import com.virginiatech.slapdash.slapdash.EventDisplayFragment.EventDisplayFragment;
import com.virginiatech.slapdash.slapdash.EventList_Fragment.EventListAdapter;
import com.virginiatech.slapdash.slapdash.EventList_Fragment.EventListFragment;
import com.virginiatech.slapdash.slapdash.FriendList_Fragment.FriendListFragment;
import com.virginiatech.slapdash.slapdash.HelperClasses.EventObjectId;
import com.virginiatech.slapdash.slapdash.Map_Fragment.MapFragment;
import com.virginiatech.slapdash.slapdash.RetainedFragments.MainEventRetainedFragment;
import com.virginiatech.slapdash.slapdash.api.ErrorSuccess;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPI;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPIBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The Main Activity for the SlapDash
 */
public class MainActivity extends AppCompatActivity implements
        FriendListFragment.OnFragmentInteractionListener,
        EventListFragment.OnFragmentInteractionListener,
        MapFragment.OnMapFragmentInteractionListener,
        EventDisplayFragment.OnFragmentInteractionListener,
        GoogleApiClient.OnConnectionFailedListener {
    public static final String DEBUG = "SLAPDASH_DEBUG_MAIN";

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                  Private Members                                        //
    /////////////////////////////////////////////////////////////////////////////////////////////
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private SlidingUpPanelLayout mLayout;
    private ViewPager mViewPager;
    private TextView toolBarTitle;
    private Toolbar mainToolbar;
    private ListView eventListLv;
    private EventListAdapter eventListAdapter;
    private Button eventList;
    private static Stack<Integer> backTraceStack;
    private GoogleApiClient mGoogleApiClient;

    @Getter
    private static final String MAIN_EVENT_FRAGMENT_TAG = "MAIN_EVENT";

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                  Request Code                                           //
    /////////////////////////////////////////////////////////////////////////////////////////////
    /* NOTE: All the request code for activity result should be here */
    @Getter
    private static final int EVENT_CREATION_ACTIVITY = 1;

    @Getter
    private static final int MY_PERMISSIONS_FINE_LOCATIONS = 2;

    @Getter
    private static final int LOG_IN_RESULT_REQUEST_CODE = 3;

    @Getter
    private static final int LOG_IN_RESULT_SUCCESSFUL = 4;

    @Getter
    private static final int LOG_IN_RESULT_UNSUCCESSFUL = 5;

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                  Android Override                                       //
    /////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //---------------------------------------------------------------------------------------
        //Facebook Set-up
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        if (!isLoggedIn()) {
            Intent startLoginActivityIntent = new Intent(getApplicationContext(),
                    LoginActivity.class);
            startActivityForResult(startLoginActivityIntent, LOG_IN_RESULT_REQUEST_CODE);
        } else { /* Logged In */
            shouldCreate();
        }
    }


    //-------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        if (mLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            return;
        }

        EventListFragment tempEventListFHolder = (EventListFragment) getSupportFragmentManager()
                .findFragmentByTag(FriendListFragment.EVENT_FRAGMENT_TAG);

        if (!(tempEventListFHolder != null && tempEventListFHolder.isVisible()) &&
                !backTraceStack.empty()) {

            backTraceStack.pop();
            if (!backTraceStack.empty()) {
                mViewPager.setCurrentItem(backTraceStack.pop());
                return;
            }
        }
        super.onBackPressed();
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_FINE_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MapFragment mapFrag = (MapFragment) mSectionsPagerAdapter
                            .getFragment(SectionsPagerAdapter.MAP_FRAGMENT);
                    mapFrag.reloadWithCurrentLocation();
                } else {
                    // TODO: Declined access decline them the map
                }
                break;
            }
        }
    }

    //-------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO: Should check the result from Login Activity
        switch (requestCode) {
            case LOG_IN_RESULT_REQUEST_CODE:
                if (resultCode == LOG_IN_RESULT_SUCCESSFUL) {
                    shouldCreate();
                } else if (resultCode == LOG_IN_RESULT_UNSUCCESSFUL) {
                    finish();
                } else {
                    /* TODO
                        Probably Should make a modal that will alarm the user
                        That they can't use all the features and that is in
                        the case that user is log-in by checking to see
                        if we have any user if at stored. Otherwise just close
                        this activity.
                     */
                }
                break;
            default:
                /* TODO: For now don't do anything, but add as needed. */
                break;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                   Other Overrides                                       //
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void OnCurrentEventRequested() {
        String eventObjectId = EventObjectId.getInstance(getApplicationContext()).
                getCurrentEventObjectIdToken();
        if (eventObjectId != null) {
            getEventByIdAsync(eventObjectId);
        }
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public void onRetrieveListOfEvents() {
        // Get the EventListFragment
        final EventListFragment eventListFrag = (EventListFragment) getSupportFragmentManager()
                .findFragmentByTag(FriendListFragment.EVENT_FRAGMENT_TAG);

        // Get an access token
        String accessToken = AccessToken.getCurrentAccessToken().getToken();

        // Get the user's facebook ID token
        String fbIdToken = Profile.getCurrentProfile().getId();

        // Validate the Facebook info
        if (fbIdToken == null || accessToken == null) {
            return;
        }

        // Make the API call to the server to retrieve all of the user's current events
        SlapDashAPI service = SlapDashAPIBuilder.getAPI();
        Call<List<Event>> call = service.getUsersEvents(fbIdToken, accessToken);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Success: Retrieved user's events",
                            Toast.LENGTH_LONG).show();

                    // Get the EventListFragment
                    EventListFragment tempEventListFrag =
                            (EventListFragment) getSupportFragmentManager()
                                    .findFragmentByTag(FriendListFragment.EVENT_FRAGMENT_TAG);

                    eventListAdapter.clear();
                    eventListAdapter.addAll(response.body());
                    eventListAdapter.notifyDataSetChanged();
                    getMainEventFragment().setCurrentEventList(response.body());

                    if (tempEventListFrag != null) {
                        tempEventListFrag.setEventList(response.body());
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Couldn't get the events!",
                            Toast.LENGTH_LONG).show();

                    if (eventListFrag != null) {
                        eventListFrag.setEventList(new ArrayList<Event>());
                    }
                }
            }

            //-----------------------------------------------------
            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Failed to retrieve a events!", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public void OnLockinEvent() {
        // Get an access token
        String accessToken = AccessToken.getCurrentAccessToken().getToken();

        // Call the proper API
        SlapDashAPI service = SlapDashAPIBuilder.getAPI();
        Call<ErrorSuccess> call = service
                .lockinEvent(EventObjectId.getInstance(getApplicationContext())
                        .getCurrentEventObjectIdToken(), accessToken);

        call.enqueue(new Callback<ErrorSuccess>() {
            @Override
            public void onResponse(Call<ErrorSuccess> call, Response<ErrorSuccess> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Successfully Locked the event.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "Error, Locking!",
                            Toast.LENGTH_LONG).show();
                }
            }

            //-----------------------------------------------------
            @Override
            public void onFailure(Call<ErrorSuccess> call, Throwable t) {
                // TODO: Should handle this in future
            }
        });
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public void OnRerollEvent() {
        // Get an access token
        String accessToken = AccessToken.getCurrentAccessToken().getToken();

        // Call the proper API
        SlapDashAPI service = SlapDashAPIBuilder.getAPI();
        Call<ErrorSuccess> call = service
                .rerollEvent(EventObjectId.getInstance(getApplicationContext())
                        .getCurrentEventObjectIdToken(), accessToken);

        call.enqueue(new Callback<ErrorSuccess>() {
            @Override
            public void onResponse(Call<ErrorSuccess> call, Response<ErrorSuccess> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Successfully rerolled the event.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "Error re-rolling!",
                            Toast.LENGTH_LONG).show();
                }
            }

            //-----------------------------------------------------
            @Override
            public void onFailure(Call<ErrorSuccess> call, Throwable t) {
                // TODO: Should Handle this in future
            }
        });
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO: Should handle this in future
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public void onDefaultEventIdChanged(Event event) {
        EventObjectId.getInstance(getApplicationContext())
                .updateEventObjectIdToken(event.get_id().toString());
        getEventByIdAsync(event.get_id().toString());
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public MainEventRetainedFragment getMainEventFragment() {
        MainEventRetainedFragment fragment = (MainEventRetainedFragment)
                getSupportFragmentManager().findFragmentByTag(MAIN_EVENT_FRAGMENT_TAG);

        if (fragment == null) {
            fragment = MainEventRetainedFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(fragment, MAIN_EVENT_FRAGMENT_TAG)
                    .commit();
        }
        return fragment;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                  Private Methods                                        //
    /////////////////////////////////////////////////////////////////////////////////////////////

    private void shouldCreate() {
        initializeGoogleApiClient();
        initializeGUIElements();

        //Current Event
        //---------------------------------------------------------------------------------------
        String currentEventToken = EventObjectId.getInstance(getApplicationContext()).
                getCurrentEventObjectIdToken();
        if (currentEventToken != null) { // There exist a eventToken Stored in app
            Event possibleCurrentEvent = getMainEventFragment().getCurrentEvent();
            if (possibleCurrentEvent != null && currentEventToken.equals(
                    possibleCurrentEvent.get_id().toString())) {// If the events is stored correctly
                updateMainActivityFragment(possibleCurrentEvent);
            } else { // If the retained fragment doesn't have the correct event
                getEventByIdAsync(currentEventToken);
            }
        } else {
            // TODO: show the make a new event page
        }
    }

    //--------------------------------------------------------------------------------------------
    private void initializeGUIElements() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setTouchEnabled(false);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        eventListLv = (ListView) findViewById(R.id.event_list_view);
        eventListLv.setAdapter((eventListAdapter = new EventListAdapter(getApplicationContext(),
                new ArrayList<Event>())));
        eventListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                onDefaultEventIdChanged(eventListAdapter.getItem(position));
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        eventList = (Button) findViewById(R.id.justbutton);
        eventList.setOnClickListener(new View.OnClickListener() {

            //-----------------------------------------------------
            @Override
            public void onClick(View view) {
                onRetrieveListOfEvents();
                mLayout.setPanelState(
                        mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED ?
                                SlidingUpPanelLayout.PanelState.EXPANDED :
                                SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        toolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        backTraceStack = new Stack<>();

        //Tab Set-up
        //---------------------------------------------------------------------------------------
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab thisTab = tabLayout.getTabAt(i);
                if (thisTab != null) {
                    switch (i) {
                        case 0:
                            thisTab.setIcon(R.drawable.map_48);
                            break;
                        case 1:
                            thisTab.setIcon(R.drawable.goto_48);
                            break;
                        case 2:
                            thisTab.setIcon(R.drawable.list_48);
                            break;
                    }
                    Drawable thisIcon = thisTab.getIcon();
                    if (thisIcon != null) {
                        thisIcon.setColorFilter(Color.parseColor("#9E9E9E"),
                                PorterDuff.Mode.SRC_IN);
                    }
                }
            }
            tabLayout.addOnTabSelectedListener(
                    new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            super.onTabSelected(tab);
                            // Add the number of layout for backtracing
                            removeCoveringFragments();
                            int position = tab.getPosition();
                            if (position == 2) {
                                mainToolbar.setVisibility(Toolbar.VISIBLE);
                                toolBarTitle.setText("Dashers");
                            } else {
                                mainToolbar.setVisibility(Toolbar.GONE);
                            }
                            backTraceStack.push(position);
                            Drawable newIcon = tab.getIcon();
                            if (newIcon != null) {
                                newIcon.setColorFilter(Color.parseColor("#212121"),
                                        PorterDuff.Mode.SRC_IN);
                            }
                        }

                        //-----------------------------------------------------
                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                            super.onTabUnselected(tab);

                            Drawable newIcon = tab.getIcon();
                            if (newIcon != null) {
                                tab.getIcon().setColorFilter(Color.parseColor("#9E9E9E"),
                                        PorterDuff.Mode.SRC_IN);
                            }
                        }

                        //-----------------------------------------------------
                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                            super.onTabReselected(tab);
                            removeCoveringFragments();
                        }
                    }
            );
        }
        // Route to the main page which is event creation
        mViewPager.setCurrentItem(1);
    }

    //-------------------------------------------------------------------------------------------
    private boolean isLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    //-------------------------------------------------------------------------------------------
    private void getEventByIdAsync(String objectId) {
        if (objectId == null) return;

        String fbAccessToken = AccessToken.getCurrentAccessToken().getToken();

        // Make the API call to the server to retrieve all of the user's current events
        SlapDashAPI service = SlapDashAPIBuilder.getAPI();
        Call<Event> call = service.getEvent(objectId, fbAccessToken);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    updateMainActivityFragment(response.body());
                    String googleId = response.body().getYelpId();
                    if (googleId != null) {
                        refreshCurrentGooglePlace(googleId);
                        refreshCurrentGooglePlacePictureAsync(googleId);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            //-----------------------------------------------------
            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //-------------------------------------------------------------------------------------------
    private void refreshCurrentGooglePlace(String googleId) {
        Places.GeoDataApi
                .getPlaceById(mGoogleApiClient, googleId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            updateMainActivityFragment(myPlace);
                        }
                        places.release();
                    }
                });
    }

    //-------------------------------------------------------------------------------------------
    private void refreshCurrentGooglePlacePictureAsync(String googleId) {
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, googleId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                    @Override
                    public void onResult(@NonNull PlacePhotoMetadataResult photos) {
                        if (!photos.getStatus().isSuccess()) {
                            return;
                        }

                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        updateMainActivityFragment(photoMetadataBuffer);
                        photoMetadataBuffer.release();
                    }
                });
    }

    //--------------------------------------------------------------------------------------------
    private void updateMainActivityFragment(Event newEvent) {
        // For the retained fragment
        getMainEventFragment().onCurrentMainEventChanged(newEvent);

        // For the friendList fragment
        FriendListFragment friendFragment = (FriendListFragment) mSectionsPagerAdapter
                .getFragment(SectionsPagerAdapter.FRIENDS_LIST_FRAGMENT);

        if (friendFragment != null) {
            friendFragment.onCurrentMainEventChanged(newEvent);
        }

        // For the mapFragment fragment
        MapFragment mapFragment = (MapFragment) mSectionsPagerAdapter
                .getFragment(SectionsPagerAdapter.MAP_FRAGMENT);

        if (mapFragment != null) {
            mapFragment.onCurrentMainEventChanged(newEvent);
        }

        // For the eventFragment fragment
        EventDisplayFragment eventFragment = (EventDisplayFragment) mSectionsPagerAdapter
                .getFragment(SectionsPagerAdapter.EVENT_CREATION_FRAGMENT);

        if (eventFragment != null) {
            eventFragment.onCurrentMainEventChanged(newEvent);
        }
    }

    //--------------------------------------------------------------------------------------------
    private void updateMainActivityFragment(Place newPlace) {
        // For the retained fragment
        getMainEventFragment().setCurrentEventPlace(newPlace);

        // For the friendList fragment
        FriendListFragment friendFragment = (FriendListFragment) mSectionsPagerAdapter
                .getFragment(SectionsPagerAdapter.FRIENDS_LIST_FRAGMENT);

        if (friendFragment != null) {
            friendFragment.onCurrentEventPlaceChanged(newPlace);
        }

        // For the mapFragment fragment
        MapFragment mapFragment = (MapFragment) mSectionsPagerAdapter
                .getFragment(SectionsPagerAdapter.MAP_FRAGMENT);

        if (mapFragment != null) {
            mapFragment.onCurrentEventPlaceChanged(newPlace);
        }

        // For the eventFragment fragment
        EventDisplayFragment eventFragment = (EventDisplayFragment) mSectionsPagerAdapter
                .getFragment(SectionsPagerAdapter.EVENT_CREATION_FRAGMENT);

        if (eventFragment != null) {
            eventFragment.onCurrentEventPlaceChanged(newPlace);
        }
    }

    //--------------------------------------------------------------------------------------------
    private void updateMainActivityFragment(PlacePhotoMetadataBuffer buff) {
        // For the retained fragment
        //getMainEventFragment().setCurrentEventPlace(newPlace);

        // For the friendList fragment
        FriendListFragment friendFragment = (FriendListFragment) mSectionsPagerAdapter
                .getFragment(SectionsPagerAdapter.FRIENDS_LIST_FRAGMENT);

        if (friendFragment != null) {
            friendFragment.onCurrentEventPictureChanged(buff, mGoogleApiClient);
        }

        // For the mapFragment fragment
        MapFragment mapFragment = (MapFragment) mSectionsPagerAdapter
                .getFragment(SectionsPagerAdapter.MAP_FRAGMENT);

        if (mapFragment != null) {
            mapFragment.onCurrentEventPictureChanged(buff, mGoogleApiClient);
        }

        // For the eventFragment fragment
        EventDisplayFragment eventFragment = (EventDisplayFragment) mSectionsPagerAdapter
                .getFragment(SectionsPagerAdapter.EVENT_CREATION_FRAGMENT);

        if (eventFragment != null) {
            eventFragment.onCurrentEventPictureChanged(buff, mGoogleApiClient);
        }
    }

    //--------------------------------------------------------------------------------------------
    private void removeCoveringFragments() {
        if (mLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }

        EventListFragment tempEventListFHolder = (EventListFragment) getSupportFragmentManager()
                .findFragmentByTag(FriendListFragment.EVENT_FRAGMENT_TAG);

        if (tempEventListFHolder != null && tempEventListFHolder.isVisible()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(tempEventListFHolder)
                    .commit();
        }
    }

    //--------------------------------------------------------------------------------------------
    private void initializeGoogleApiClient() {
        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Places.GEO_DATA_API)
                .build();

    }
}
