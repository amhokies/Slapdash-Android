package com.virginiatech.slapdash.slapdash.EventCreationActivity;

import android.app.Service;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.virginiatech.slapdash.slapdash.DataModelClasses.Event;
import com.virginiatech.slapdash.slapdash.DataModelClasses.EventType;
import com.virginiatech.slapdash.slapdash.HelperClasses.CompatibilityHelper;
import com.virginiatech.slapdash.slapdash.InvitingFriendList_Fragment.FbFriendFragment;
import com.virginiatech.slapdash.slapdash.MainActivity;
import com.virginiatech.slapdash.slapdash.R;
import com.virginiatech.slapdash.slapdash.Services.LocationService;
import com.virginiatech.slapdash.slapdash.api.CreateEventRequest;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPI;
import com.virginiatech.slapdash.slapdash.api.SlapDashAPIBuilder;

import java.net.HttpURLConnection;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventCreationActivity extends AppCompatActivity implements
        FbFriendFragment.OnListFragmentInteractionListener,
        EventCreationActivityFragment.OnEventCreationFragmentInteractionListener{
    private FragmentManager mFragmentManager;
    private FloatingActionButton fab;

    /**
     * The FriendList fragment declarations
     */
    private static FbFriendFragment mFbFriendFragment;
    private static final int FBFRIEND_COLUMN_SIZE = 1;
    private static final String FBFRIEND_TAG = "FBFRIEND_FRAGMENT";


    /**
     * The event populated in this activity and sub fragments
     */
    public static Event thisEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);
        mFragmentManager = this.getSupportFragmentManager();

        /* Initialize the Event this activity is going to return */
        thisEvent = new Event();
        //EventType mEventType = (EventType) getIntent().getSerializableExtra("EventType");
        thisEvent.setCategory(EventType.FOOD.getText());
        //Log.d("SLAPDASH", "EventCreationActivity type = " + mEventType);

        /* Initialize the view and move to first fragment */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Event");

        mFragmentManager
                .beginTransaction()
                .replace(R.id.create_event_fragment, EventCreationActivityFragment.newInstance())
                .commit();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFbFriendFragment = (FbFriendFragment)
                        mFragmentManager.findFragmentByTag(FBFRIEND_TAG);

                if(mFbFriendFragment == null){
                    mFbFriendFragment = FbFriendFragment.newInstance(FBFRIEND_COLUMN_SIZE);
                }

                mFragmentManager
                        .beginTransaction()
                        .replace(R.id.create_event_fragment, mFbFriendFragment, FBFRIEND_TAG)
                        .commit();
            }
        });
    }

    /* Interface Handlers */
    @Override
    public void OnInvitedFriendButtonClicked(String friendId) {
        thisEvent.inviteFriend(friendId);
    }

    @Override
    public void OnUninvitedFriendButtonClicked(String friendId) {
        thisEvent.uninviteFriend(friendId);
    }

    @Override
    public void OnFbFriendFragmentCreated() {
        Drawable newFabDrawable = CompatibilityHelper.getDrawable(this, R.drawable.event_create_done);
        fab.setImageDrawable(newFabDrawable);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CreatingEventWaitDialogFragment wait = new CreatingEventWaitDialogFragment();
                wait.show(getFragmentManager(), "EVENT_CREATION_WAIT");

                String token = AccessToken.getCurrentAccessToken().getToken().toString();
                Event eventToBeCreated = thisEvent;
                SlapDashAPI service = SlapDashAPIBuilder.getAPI();

                Call<Event> call = service.createEvent(token,
                        new CreateEventRequest(eventToBeCreated,
                        LocationService.getRecentLocation(getApplicationContext(),
                                (LocationManager) getSystemService(Service.LOCATION_SERVICE))));
                call.enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(Call<Event> call, Response<Event> response) {
                        int resCode = response.code();
                        if (resCode == HttpURLConnection.HTTP_OK
                                || resCode == HttpURLConnection.HTTP_CREATED) {
                            Log.d(MainActivity.DEBUG, "Success: HTTP POST /events");
                            Log.d(MainActivity.DEBUG, response.body().toString());
                        } else {
                            Toast.makeText(getBaseContext(), "Couldn't create the event",
                                    Toast.LENGTH_LONG).show();
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                wait.dismiss();
                                finish();
                            }
                        }, 2000);

                    }

                    @Override
                    public void onFailure(Call<Event> call, Throwable t) {
                        t.printStackTrace();
                        Log.d(MainActivity.DEBUG, "Failure: HTTP POST /events");
                        Toast.makeText(getBaseContext(), "Something went wrong ... ",
                                Toast.LENGTH_LONG).show();
                    }
                });


            }
        });
    }

    @Override
    public void OnEventTitleChanged(String newTitle) {
        thisEvent.setTitle(newTitle);
    }

    @Override
    public void OnEventDescriptionChanged(String newDescription) {
        thisEvent.setDescription(newDescription);
    }

    @Override
    public void OnEventStartTimeChanged(Date newStartDate) {
        thisEvent.setStarttime(newStartDate.toString());
    }

    @Override
    public void OnEventTimeoutChanged(int newTimeout) {
        thisEvent.setTimeout(newTimeout);
    }

    @Override
    public void OnEventPermissionChanged(String newPermission) {
        thisEvent.setUserpermissions(newPermission);
    }
}
