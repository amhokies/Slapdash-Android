package com.virginiatech.slapdash.slapdash.Map_Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Profile;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.virginiatech.slapdash.slapdash.DataModelClasses.Event;
import com.virginiatech.slapdash.slapdash.DataModelClasses.User;
import com.virginiatech.slapdash.slapdash.DataModelClasses.UserLocation;
import com.virginiatech.slapdash.slapdash.HelperClasses.CompatibilityHelper;
import com.virginiatech.slapdash.slapdash.MainActivity;
import com.virginiatech.slapdash.slapdash.MainActivityFragmentsInterface;
import com.virginiatech.slapdash.slapdash.R;
import com.virginiatech.slapdash.slapdash.RetainedFragments.MainEventRetainedFragment;


public class MapFragment extends Fragment implements android.location.LocationListener,
        MainActivityFragmentsInterface {
    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                 Private Members                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////
    private OnMapFragmentInteractionListener mListener;
    private FloatingActionButton directionFab;
    private String directionAddr = "http://maps.google.com/maps?saddr=0,0&daddr=0,0";

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Google Map Properties~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private MapView mMapView;
    private GoogleMap googleMap;
    //public Marker currentLocMarker;

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~General Fragment Properties~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private Context mContext;
    private MapFragment self;
    private BitmapDescriptor markerIcon;

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                       Overrides                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getContext();
        self = this;
        // inflate and return the layout
        View v = inflater.inflate(R.layout.fragment_location_info, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap newGoogleMap) {
                googleMap = newGoogleMap;
                reloadWithCurrentLocation();
            }
        });


        // Create a custom listener for the direction fab on the map
        View.OnTouchListener dfabCustomListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(directionAddr));
                    startActivity(intent);
                    return true;
                }
                return true;
            }
        };

        directionFab = (FloatingActionButton) v.findViewById(R.id.directionFab);
        directionFab.setOnTouchListener(dfabCustomListener);

        // Perform any camera updates here
        return v;
    }

    //----------------------------------------------------------------------------------------------
    public void changeStartDirectionString(double lat, double lng) {
        String postStartString = directionAddr.substring(directionAddr.indexOf('&'));
        String finalStr = "http://maps.google.com/maps?saddr=".concat(String.valueOf(lat))
                .concat(",").concat(String.valueOf(String.valueOf(lng)));
        directionAddr = finalStr.concat(postStartString);
    }

    //----------------------------------------------------------------------------------------------
    public void changeDestDirectionString(double lat, double lng) {
        String preStartString = directionAddr.substring(0, directionAddr.lastIndexOf('=') + 1);
        directionAddr = preStartString.concat(String.valueOf(lat)).concat(",")
                .concat(String.valueOf(lng));
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMapFragmentInteractionListener) {
            mListener = (OnMapFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMapInteractionListener");
        }
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    //http://javapapers.com/android/android-show-current-location-on-map-using-google-maps-api/
    //----------------------------------------------------------------------------------------------
    @Override
    public void onLocationChanged(Location location) {
        //TextView locationTv = (TextView) findViewById(R.id.latlongLocation);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        //if (currentLocMarker != null)
        //    currentLocMarker.setPosition(latLng);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        changeStartDirectionString(latitude, longitude);
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public boolean onCurrentMainEventChanged(Event eventToShow) {
        if (googleMap != null)
            googleMap.clear();

        addMarker(eventToShow.getAdmin(), BitmapDescriptorFactory.HUE_RED);
        for (User u : eventToShow.getAttendees()) {
            if (u.getFbtokenid().equals(Profile.getCurrentProfile().getId())) {
                addMarker(u, BitmapDescriptorFactory.HUE_CYAN);
                UserLocation loc = u.getRecentlocation();
                LatLng latLng = new LatLng(loc.getLat(), loc.getLon());
                changeStartDirectionString(latLng.latitude, latLng.longitude);
            } else {
                addMarker(u, BitmapDescriptorFactory.HUE_GREEN);
            }
        }
        for (User u : eventToShow.getInvitations()) {
            if (u.getFbtokenid().equals(Profile.getCurrentProfile().getId())) {
                addMarker(u, BitmapDescriptorFactory.HUE_CYAN);
                UserLocation loc = u.getRecentlocation();
                LatLng latLng = new LatLng(loc.getLat(), loc.getLon());
                changeStartDirectionString(latLng.latitude, latLng.longitude);
            } else {
                addMarker(u, BitmapDescriptorFactory.HUE_YELLOW);
            }
        }
        return true;
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public boolean onCurrentEventPlaceChanged(Place newPlace) {
        //Bug if random is chosen and the place has not changed then ruh uh
        MainEventRetainedFragment frag = mListener.getMainEventFragment();
        if (frag != null && frag.getCurrentEvent() != null) {
            Event currentEvent = frag.getCurrentEvent();
            LatLng currentPlace = newPlace.getLatLng();

            onCurrentMainEventChanged(currentEvent);

            MarkerOptions options = new MarkerOptions().position(currentPlace)
                    .snippet(newPlace.getAddress().toString())
                    .title(newPlace.getName().toString()).icon(markerIcon);
            if (googleMap != null) {
                int iconRID;
                switch (currentEvent.getCategory()) {
                    case "Food":
                        iconRID = R.drawable.ic_restaurant_black_24dp;
                        break;
                    case "Play":
                        iconRID = R.drawable.ic_grade_black_24dp;
                        break;
                    case "SlapDash":
                        iconRID = R.drawable.ic_casino_black_24dp;
                        break;
                    case "Drink":
                        iconRID = R.drawable.ic_local_bar_black_24dp;
                        break;
                    default:
                        iconRID = R.drawable.ic_grade_black_24dp;
                        break;
                }

                Drawable iconDrawable = CompatibilityHelper.getDrawable(getActivity(), iconRID);
                markerIcon = getMarkerIconFromDrawable(iconDrawable);
                options.icon(markerIcon);
                googleMap.addMarker(options);
            }
        }
        changeDestDirectionString(newPlace.getLatLng().latitude, newPlace.getLatLng().longitude);
        return true;
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public boolean onCurrentEventPictureChanged(PlacePhotoMetadataBuffer buffer,
                                                final GoogleApiClient mGoogleApiClient) {
        return false;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                  Public methods                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //----------------------------------------------------------------------------------------------
    /**
     * Clears GoogleMap of all markers that it current has, and reloads it with just the current
     * users location marker.
     */
    public void reloadWithCurrentLocation() {
        googleMap.clear();
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.getMY_PERMISSIONS_FINE_LOCATIONS());
        }

        LocationManager locationManager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (bestProvider != null) {
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                onLocationChanged(location);
                locationManager.requestLocationUpdates(bestProvider, 20000, 0, self);
                // create marker
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions marker = new MarkerOptions().position(latLng);

                // Changing marker icon
                marker.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                // adding marker
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude)).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            }
        }

        MainEventRetainedFragment fragment = mListener.getMainEventFragment();

        if (googleMap != null) {
            if (fragment != null && fragment.getCurrentEvent() != null) {
                onCurrentMainEventChanged(fragment.getCurrentEvent());
            } else {
                mListener.OnCurrentEventRequested();
            }
        } else {
            // TODO: Can we do any handling here?
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                    Interface                                            //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public interface OnMapFragmentInteractionListener {
        MainEventRetainedFragment getMainEventFragment();

        void OnCurrentEventRequested();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                 Private Members                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////

    private void addMarker(User user, float color) {
        if (user != null && user.getRecentlocation() != null) {
            UserLocation loc = user.getRecentlocation();
            LatLng latLng = new LatLng(loc.getLat(), loc.getLon());
            MarkerOptions marker;
            if (user.getFbtokenid().equals(Profile.getCurrentProfile().getId())) {
                marker = new MarkerOptions().position(latLng).title("You");
            } else {
                marker = new MarkerOptions().position(latLng).title(user.getFullName());
            }

            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(color));

            // adding marker
            if (googleMap != null) {
                googleMap.addMarker(marker);
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
