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
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceDetails.PlaceDetails;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceNearBySearch.PlaceNearBySearch;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceNearBySearch.PlaceNearBySearchResult;
import com.android.sagot.go4lunch.Models.RestaurantDetails;
import com.android.sagot.go4lunch.R;
import com.android.sagot.go4lunch.Utils.GooglePlaceStreams;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

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
    private List<RestaurantDetails> mListRestaurants;

    // Declare Subscription
    protected Disposable mDisposable;

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
        //configurePlayServicePlaces();
        //if (mLocationPermissionGranted) {
        //    configureGooglePlaceService();
        //}
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
                        // Configure the Places Service of Google and Search proximity Restaurant
                        configureGooglePlaceService();
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

    public static String locationStringFromLocation(final Location location) {
        return Location.convert(location.getLatitude(), Location.FORMAT_DEGREES) + "," + Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
    }

    // -------------------------------
    //  GOOGLE PLAY SERVICE : PLACES
    // -------------------------------
    public void configureGooglePlaceService() {
        Log.d(TAG, "configureGooglePlaceService: ");

        // Get api_key
        //String key = getResources().getString(R.string.google_maps_key);
        String key = "AIzaSyC8l-LPDTEqpJxWbJ-VbUgdUoj8TdXlcK4";

        // Criteria of the query
        Map<String, String> mQuery;
        // Create filters
        mQuery = new HashMap<>();
        // --> Add Criteria <--
        // -- Location Paris Gare de lyon
        mQuery.put("key", "AIzaSyC8l-LPDTEqpJxWbJ-VbUgdUoj8TdXlcK4");
        /*static String latLng = Location.convert(mLastKnownLocation.getLatitude(),Location.FORMAT_DEGREES)+
                        ","+
                        Location.convert(mLastKnownLocation.getLongitude(),Location.FORMAT_DEGREES);
                        */
        Log.d(TAG, "configureGooglePlaceService: Location = "+locationStringFromLocation(mLastKnownLocation));
        mQuery.put("location",locationStringFromLocation(mLastKnownLocation));
        mQuery.put("radius", "1000");
        mQuery.put("type", "restaurant");


        // Execute the stream subscribing to Observable defined inside GooglePlaceStreams
        mDisposable = GooglePlaceStreams.streamFetchListPlacesNearBySearchId(mQuery)
                .subscribeWith(new DisposableObserver<List<PlaceDetails>>() {
                    @Override
                    public void onNext( List<PlaceDetails> placesDetails) {
                        Log.d(TAG, "onNext: ");
                        // Analyze the answer
                       // Log.d(TAG, "onNext: ids(0) ="+ids.get(0).getResult().getId());
                       // Log.d(TAG, "onNext: ids(1) ="+
                        for (PlaceDetails placeDetails : placesDetails){
                            Log.d(TAG, "onNext: Name = "+placeDetails.getResult().getName());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Display a toast message
                        //updateUIWhenErrorHTTPRequest();
                        Log.d(TAG, "onError: ");
                    }
                    @Override
                    public void onComplete() { Log.d(TAG,"On Complete !!"); }
                });
    }

    // Analyze the answer of HttpRequestWithRetrofit
    protected void first_responseHttpRequestAnalyze(List<String> ids) {
        Log.d(TAG, "first_responseHttpRequestAnalyze: ");
        for (String id : ids) {
            Log.d(TAG, "first_responseHttpRequestAnalyze: id = "+id);
        }
    }

    // Analyze the answer of HttpRequestWithRetrofit
    //protected void responseHttpRequestAnalyze(PlaceDetails placeDetails) {
        //Log.d(TAG, "responseHTTPRequestAnalyze: ");

       // Log.d(TAG, "responseHttpRequestAnalyze: listPlaceDetails = "+listPlaceDetails.size());
        //if (listPlaceDetails.size() != 0) {
            // Creating a list of RestaurantDetails Restaurants
           // mListRestaurants = new ArrayList<>();

            //Instantiate a RestaurantDetails Restaurant Variable
           // RestaurantDetails restaurant;

            //Here we recover only the elements of the query that interests us
            //for (PlaceDetails placeDetails : listPlaceDetails) {

                // Creating a New RestaurantDetails Restaurant Variable
                //restaurant = new RestaurantDetails();
                //restaurant.setId(placeDetails.getResult().getId());
                //restaurant.setName(placeDetails.getResult().getName());
               // restaurant.setAddress(placeDetails.getResult().getAdrAddress());
                //restaurant.setOpeningTime(results.getOpeningHours().getOpenNow().toString());
                //.setLat(results.getGeometry().getLocation().getLat().toString());
                //restaurant.setLng(results.getGeometry().getLocation().getLng().toString());
                //restaurant.setNbrStars(2);
                //restaurant.setNbrParticipants(9);
                //restaurant.setPhotoUrl("https://cdn.pixabay.com/photo/2018/07/14/15/27/cafe-3537801_960_720.jpg");
                //mListRestaurants.add(restaurant);

           /*     Log.d(TAG, "responseHttpRequestAnalyze: ***************************************");
                Log.d(TAG, "responseHttpRequestAnalyze:      Place Id    = "+placeDetails.getResult().getId());
                Log.d(TAG, "responseHttpRequestAnalyze:      Name        = "+placeDetails.getResult().getName());
                Log.d(TAG, "responseHttpRequestAnalyze:      Address     = "+placeDetails.getResult().getAdrAddress());
                Log.d(TAG, "responseHttpRequestAnalyze:      OpeningTime = "+placeDetails.getResult().getOpeningHours());
                Log.d(TAG, "responseHttpRequestAnalyze:      Lat         = "+placeDetails.getResult().getGeometry().getLocation().getLng());
                Log.d(TAG, "responseHttpRequestAnalyze:      Lng         = "+placeDetails.getResult().getGeometry().getLocation().getLat());
            }*/

            //Go4LunchViewModel model = ViewModelProviders.of(getActivity()).get(Go4LunchViewModel.class);
            //model.setListPlaceDetails(mListRestaurants);

        /*} else {
            mListener.showSnackBar("No placeNearBySearch found for these criteria of searches");
        }*/
    //}

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

    // -----------
    //  ( OUT )
    // -----------
    @Override
    public void onDestroy() {
        //Unsubscribe the stream when the fragment is destroyed so as not to create a memory leaks
        this.disposeWhenDestroy();
        super.onDestroy();
    }

    //  Unsubscribe the stream when the fragment is destroyed so as not to create a memory leaks
    private void disposeWhenDestroy(){
        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
    }
}
