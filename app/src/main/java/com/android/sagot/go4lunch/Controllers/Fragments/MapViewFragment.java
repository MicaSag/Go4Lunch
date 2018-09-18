package com.android.sagot.go4lunch.Controllers.Fragments;


import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.sagot.go4lunch.Models.Go4LunchViewModel;
import com.android.sagot.go4lunch.Models.PlaceDetails;
import com.android.sagot.go4lunch.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michaël SAGOT on 23/08/2018.
 */

public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    // For debug
    private static final String TAG = MapViewFragment.class.getSimpleName();

    // For add Google Map in Fragment
    private SupportMapFragment mMapFragment;

    // ==> For use Api Google Play Service : map
    private GoogleMap mMap;

    // ==> For use location permission
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;  // Request Code
    private boolean mLocationPermissionGranted;                     // Boolean status
    // _ Default location if not permission granted ( Paris )
    private final LatLng mDefaultLocation = new LatLng(48.844304, 2.374377);
    private static final float DEFAULT_ZOOM = 16f;

    //==> For use Api Google Play Service : Location
    // _ The geographical location where the device is currently located.
    // _ That is, the last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    // _ The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //==> For use Api Google Play Service : Places
    // _ The entry points to the Places API
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    //List of restaurants found
    private List<PlaceDetails> mListRestaurants;

    // ==> CallBack
    // Interface for ShowSnakeBar
    public interface ShowSnackBarListener {
        void showSnackBar(String message);
    }
    // Interface Object for use CallBack
    ShowSnackBarListener mListener;

    public MapViewFragment() {
        // Required empty public constructor
    }

    // --------------------
    //     ENTRY POINT
    // --------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        // Configure the Maps Service of Google
        configurePlayServiceMaps();

        return rootView;
    }

    // -------------------------------
    //   GOOGLE PLAY SERVICE : MAPS
    // -------------------------------
    public void configurePlayServiceMaps() {
        Log.d(TAG, "configurePlayServiceMaps: ");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            mMapFragment.getMapAsync(this);
        }

        // Build the map
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_map_view, mMapFragment).commit();
    }
    /**
     * Manipulates the map when it's available
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        mMap = googleMap;

        // Disable 3D Building
        mMap.setBuildingsEnabled(false);

        // Prompt the user for permission and Update location map
        getLocationPermission();

        // Configure the Places Service of Google
        configurePlayServicePlaces();
    }
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    public void updateLocationUI() {
        Log.d(TAG, "updateLocationUI: ");
        if (mMap != null) {
            try {
                if (mLocationPermissionGranted) {
                    Log.d(TAG, "updateLocationUI: Permission Granted");
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    // Retrieves the best and most recent device location information
                    getLocationCoordinatesDevice();
                } else {
                    Log.d(TAG, "updateLocationUI: Permission not Granted");
                    mMap.setMyLocationEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mLastKnownLocation = null;
                    showDefaultLocation();
                }
            } catch (SecurityException e) {
                Log.e("updateLocationUI %s", e.getMessage());
            }
        }
    }
    /**
     * Retrieves Coordinates of the best and most recent device location information if Exists
     */
    private void getLocationCoordinatesDevice() {
        Log.d(TAG, "getLocationCoordinatesDevice: ");
        // Construct a FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        try {
            // Retrieves information if existing
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    mLastKnownLocation = task.getResult();
                    if( mLastKnownLocation != null ) {
                        Log.d(TAG, "getLocationCoordinatesDevice: getLastLocation EXIST");
                        Log.d(TAG, "getLocationCoordinatesDevice: mLastKnownLocation.getLatitude()  = " + mLastKnownLocation.getLatitude());
                        Log.d(TAG, "getLocationCoordinatesDevice: mLastKnownLocation.getLongitude() = " + mLastKnownLocation.getLongitude());
                        // Position yourself automatically on the current location of the device
                        showCurrentLocation();
                    }
                    else Log.d(TAG, "getLocationCoordinatesDevice: getLastLocation NO EXIST");
                }
            });
        } catch (SecurityException e)  {
            Log.e("getDeviceLocation %s", e.getMessage());
        }
    }

    /**
     * Method that places the map on a default location
     */
    private void showDefaultLocation() {
        Log.d(TAG, "showDefaultLocation: ");
        mListener.showSnackBar("Location permission not granted, " +
                "showing default location : Paris Lyon's train station");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
        mMap.addMarker(new MarkerOptions()
                .position(mDefaultLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
         //       .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_call_black_24)));
    }

    /**
     * Method that places the map on a current location
     */
    private void showCurrentLocation() {
        Log.d(TAG, "showCurrentLocation: ");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng( mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
    }

    // -------------------------------
    //       PERMISSION METHODS
    // -------------------------------
    /**
     * Controls location permission.
     * If they aren't allowed, prompts the user for permission to use the device location.
     * The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: ");
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            // Update the location on the map
            updateLocationUI();
        } else {
            Log.d(TAG, ">>-- Ask the user for Location Permission --<<");
            requestPermissions(
                    new String[]{   Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    /**
     * Method that processes the response to a request for permission made
     * by the function "requestPermissions(..)"
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        // Update the location on the map
        updateLocationUI();
    }

    // -------------------------------
    //  GOOGLE PLAY SERVICE : PLACES
    // -------------------------------
    public void configurePlayServicePlaces() {
        Log.d(TAG, "configurePlayServicePlaces: ");

        // Configure connection to Places Service
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getContext());

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext());

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                            // Creating a list of PlaceDetails Restaurants
                            mListRestaurants = new ArrayList<>();

                            //Instantiate a PlaceDetails Restaurant Variable
                            PlaceDetails restaurant;

                            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                // Build a list of likely places to show the user.

                                for (Integer placeType : placeLikelihood.getPlace().getPlaceTypes()) {
                                    if (placeType.equals(Place.TYPE_RESTAURANT)) {
                                        // Creating a New PlaceDetails Restaurant Variable
                                        restaurant = new PlaceDetails();
                                        restaurant.setId(placeLikelihood.getPlace().getId());
                                        restaurant.setName(placeLikelihood.getPlace().getName().toString());
                                        restaurant.setAddress(placeLikelihood.getPlace().getAddress().toString());
                                        if (placeLikelihood.getPlace().getAttributions() != null) restaurant.setOpeningTime(placeLikelihood.getPlace().getAttributions().toString());
                                        restaurant.setLatLngs(placeLikelihood.getPlace().getLatLng());
                                        //if (placeLikelihood.getPlace().getWebsiteUri() != null) restaurant.setPhotoUrl(placeLikelihood.getPlace().getWebsiteUri().toString());
                                        restaurant.setPhotoUrl("https://cdn.pixabay.com/photo/2018/07/14/15/27/cafe-3537801_960_720.jpg");
                                        mListRestaurants.add(restaurant);

                                        break;
                                    }
                                }
                            }
                            // Release the place likelihood buffer, to avoid memory leaks.
                            likelyPlaces.release();


                            PlaceDetails placeDetails = new PlaceDetails();
                            placeDetails.setId("001");
                            placeDetails.setAddress("5 rue des beaux jours");
                            placeDetails.setName("Resto du coeur");
                            placeDetails.setType("gratuit");
                            placeDetails.setOpeningTime("tous les jours de 00h00 à 24h00");
                            placeDetails.setPhotoUrl("https://cdn.pixabay.com/photo/2018/08/10/21/52/restaurant-3597677_960_720.jpg");
                            //placeDetails.setLatLngs( );
                            mListRestaurants.add(placeDetails);
                            Go4LunchViewModel model = ViewModelProviders.of(getActivity()).get(Go4LunchViewModel.class);
                            model.setListPlaceDetails(mListRestaurants);

                            //for (PlaceDetails placeD : mListRestaurants){
                            //    Log.d(TAG, "configurePlayServicePlaces : placeDetails Name = "+placeD.getName());
                            //}

                        } else {
                            Log.e(TAG, "configurePlayServicePlaces Exception: %s", task.getException());
                        }
                    });
        } else {
            showDefaultLocation();
        }
    }

    // --------------------
    //  CALLBACKS METHODS
    // --------------------
    /**
     *  Method use for CallBacks to the Welcome Activity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // CallBack for ShowSnackBar
        if (context instanceof ShowSnackBarListener) {
            mListener = (ShowSnackBarListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement showSnackBarListener");
        }
    }
}
