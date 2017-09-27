package com.virginiatech.slapdash.slapdash.EventDisplayFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.maps.model.LatLng;
import com.virginiatech.slapdash.slapdash.DataModelClasses.Event;
import com.virginiatech.slapdash.slapdash.EventCreationActivity.EventCreationActivity;
import com.virginiatech.slapdash.slapdash.HelperClasses.CompatibilityHelper;
import com.virginiatech.slapdash.slapdash.HelperClasses.UserIdToken;
import com.virginiatech.slapdash.slapdash.MainActivity;
import com.virginiatech.slapdash.slapdash.MainActivityFragmentsInterface;
import com.virginiatech.slapdash.slapdash.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventDisplayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDisplayFragment extends Fragment
        implements  MainActivityFragmentsInterface{
    private static final int ONE_SECOND = 1000;
    private static final int ONE_MINUTE = ONE_SECOND * 60;
    private static final int ONE_HOUR = ONE_MINUTE * 60;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);
    private Timer mTimer;
    private Handler mHandler;
    private OnFragmentInteractionListener mListener;
    private FloatingActionButton createEventFab;
    private ImageView eventImageView;
    private TextView titleTextView;
    private TextView slapperTextView;
    private TextView countGoing;
    private TextView countMaybe;
    private TextView countNotGoing;
    private TextView timerTextView;
    private TextView startAtTextView;
    private TextView isActiveTextView;
    private TextView namePlaceTextView;
    private RatingBar businessRankingRB;
    private ImageButton lockInBotton;
    private ImageButton rerollBotton;
    private ImageButton directionButton;
    private ImageButton callButton;
    private ImageButton webSearchButton;
    private CheckedTextView activeCheckMark;
    private long remainingTime;

    public EventDisplayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment EventDisplayFragment.
     */
    public static EventDisplayFragment newInstance() {
        EventDisplayFragment fragment = new EventDisplayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_display, container, false);


        /* Initialize elements */
        createEventFab = (FloatingActionButton) view.findViewById(R.id.create_event_new);
        createEventFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent startLoginActivityIntent = new Intent(getApplicationContext(), EventCreationActivity.class);
                startActivityForResult(startLoginActivityIntent,
                        MainActivity.getEVENT_CREATION_ACTIVITY());
            }
        });

        eventImageView = (ImageView) view.findViewById(R.id.display_event_image);
        titleTextView = (TextView) view.findViewById(R.id.display_event_textView);
        slapperTextView = (TextView) view.findViewById(R.id.display_event_slapper);
        countGoing = (TextView) view.findViewById(R.id.display_count_going);
        countMaybe = (TextView) view.findViewById(R.id.display_count_maybe);
        countNotGoing = (TextView) view.findViewById(R.id.display_count_notgoing);

        businessRankingRB = (RatingBar) view.findViewById(R.id.display_event_ratingBar);

        lockInBotton = (ImageButton) view.findViewById(R.id.display_event_lock);
        lockInBotton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                mListener.OnLockinEvent();
            }
        });

        rerollBotton = (ImageButton) view.findViewById(R.id.display_event_reroll);
        rerollBotton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                mListener.OnRerollEvent();
            }
        });

        directionButton = (ImageButton) view.findViewById(R.id.display_event_directions);
        callButton = (ImageButton) view.findViewById(R.id.display_event_call);
        webSearchButton = (ImageButton) view.findViewById(R.id.display_event_search);

        timerTextView = (TextView) view.findViewById(R.id.display_event_timeout);
        startAtTextView = (TextView) view.findViewById(R.id.display_event_start);
        activeCheckMark = (CheckedTextView) view.findViewById(R.id.display_count_active);
        isActiveTextView = (TextView) view.findViewById(R.id.display_count_is_active);
        namePlaceTextView = (TextView) view.findViewById(R.id.display_event_name);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                timerTextView.setText(formatIntoHHMMSS(remainingTime));
            }
        };

        return view;
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void OnLockinEvent();
        void OnRerollEvent();
    }

    private void startTimer(long createTime, int timeout){
        Log.d(MainActivity.DEBUG, "now = " + new Date().getTime());
        Log.d(MainActivity.DEBUG, "create time = " + createTime);
        Log.d(MainActivity.DEBUG, "timeout = " + timeout);
        remainingTime =  ((createTime + (timeout * ONE_MINUTE)) - new Date().getTime());
        if(remainingTime <= 0) {
            mHandler.sendEmptyMessage(0);
            remainingTime = 0;
            return;
        }
        if(mTimer != null) { mTimer.cancel(); }
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                Log.d(MainActivity.DEBUG, "remaingingTime = " + remainingTime);
                if(remainingTime <= 0){
                    mTimer.cancel();
                } else {
                    remainingTime -= ONE_SECOND;
                    mHandler.sendEmptyMessage(0);
                }
            }
        }, 0, 1000);
    }

    private String formatIntoHHMMSS(long current){
        long hour = current / ONE_HOUR;
        long minute = (current %= ONE_HOUR) / ONE_MINUTE;
        long seconds = current % ONE_MINUTE / ONE_SECOND;
        return String.format(Locale.US,"%02d:", hour) +
                String.format(Locale.US,"%02d:", minute) +
                String.format(Locale.US,"%02d", seconds) ;
    }

    @Override
    public boolean onCurrentMainEventChanged(Event newCurrentEvent) {
        refreshPage();
        titleTextView.setText(newCurrentEvent.getTitle());
        countGoing.setText(newCurrentEvent.getAttendees() != null?
                newCurrentEvent.getAttendees().size() + "": "0");
        countMaybe.setText(newCurrentEvent.getInvitations() != null?
                newCurrentEvent.getInvitations().size() + "": "0");

        // TODO: implement this after declined list of events is implemented
        countNotGoing.setText("0");
        if(newCurrentEvent.getYelpId() == null){
            lockInBotton.setVisibility(Button.VISIBLE);
            rerollBotton.setVisibility(Button.INVISIBLE);
            startTimer(newCurrentEvent.getCreatetimeLong(), newCurrentEvent.getTimeout());
        } else {
            lockInBotton.setVisibility(Button.INVISIBLE);
            rerollBotton.setVisibility(Button.VISIBLE);
        }

        String thisUser = UserIdToken.getInstance(getApplicationContext()).getCurrentUserIdToken();
        if(newCurrentEvent.getAdmin().get_id().equals(thisUser)){
            slapperTextView.setText("You");
        } else {
            slapperTextView.setText(newCurrentEvent.getAdmin().getFullName());
        }

        Date startDate = new Date(newCurrentEvent.getStarttimeLong());
        startAtTextView.setText(new SimpleDateFormat("hh:mm a EEEE MM/WW/yy", Locale.US).format(startDate));
        int activeCheckMarkedResource;
        if(startDate.before(Calendar.getInstance().getTime())) {
            activeCheckMarkedResource = android.R.drawable.presence_busy;
            isActiveTextView.setText("Not Active");
        } else {
            activeCheckMarkedResource =  android.R.drawable.presence_online;
            isActiveTextView.setText("Active");
        }

        activeCheckMark.setCheckMarkDrawable(
                CompatibilityHelper.getDrawable(getActivity(), activeCheckMarkedResource));
        return true;
    }

    @Override
    public boolean onCurrentEventPlaceChanged(final Place newPlace) {
        businessRankingRB.setRating(newPlace.getRating());
        namePlaceTextView.setText(newPlace.getName());
        final LatLng placeLatLng = newPlace.getLatLng();
        final CharSequence address = newPlace.getAddress();
        final CharSequence phoneNumber = newPlace.getPhoneNumber();
        final Uri webSiteUrl = newPlace.getWebsiteUri();
        if(webSiteUrl != null){
            Log.d(MainActivity.DEBUG, webSiteUrl.toString());
        }

        if(placeLatLng != null){
            directionButton.setVisibility(Button.VISIBLE);
            directionButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    view.startAnimation(buttonClick);

                    String uri = String.format(Locale.ENGLISH,
                            "http://maps.google.com/maps?daddr=%f,%f (%s)",
                            placeLatLng.latitude,
                            placeLatLng.longitude,
                            address);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
            });
        } else {
            directionButton.setVisibility(Button.INVISIBLE);
        }


        if(phoneNumber != null){
            callButton.setVisibility(Button.VISIBLE);
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(callIntent);
                }
            });
        } else {
            callButton.setVisibility(Button.INVISIBLE);
        }

        if(webSiteUrl != null){
            webSearchButton.setVisibility(Button.VISIBLE);
            webSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, webSiteUrl);
                    startActivity(intent);
                }
            });
        } else {
            webSearchButton.setVisibility(Button.INVISIBLE);
        }

        return true;
    }

    @Override
    public boolean onCurrentEventPictureChanged(PlacePhotoMetadataBuffer buffer, GoogleApiClient mGoogleApiClient) {

        PlacePhotoMetadataBuffer photoMetadataBuffer = buffer;
        if (photoMetadataBuffer.getCount() > 0) {
            // Display the first bitmap in an ImageView in the size of the view
            photoMetadataBuffer.get(0)
                    .getScaledPhoto(mGoogleApiClient, eventImageView.getWidth(),
                            eventImageView.getHeight())
                    .setResultCallback(new ResultCallback<PlacePhotoResult>() {
                        @Override
                        public void onResult(PlacePhotoResult placePhotoResult) {
                            if (!placePhotoResult.getStatus().isSuccess()) {
                                return;
                            }
                            eventImageView.setImageBitmap(placePhotoResult.getBitmap());
                        }
                    });
        }
        return true;
    }

    private void refreshPage(){
        titleTextView.setText("");
        countGoing.setText("0");
        countMaybe.setText("0");
        countNotGoing.setText("0");
        slapperTextView.setText("Slapper");
        startAtTextView.setText("");
        isActiveTextView.setText("");
        businessRankingRB.setRating(0);
        namePlaceTextView.setText("Location");
        eventImageView.setImageDrawable(null);
        directionButton.setOnClickListener(null);
        callButton.setOnClickListener(null);
        webSearchButton.setOnClickListener(null);
    }
}
