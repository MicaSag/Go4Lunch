package com.android.sagot.go4lunch.Controllers.Activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.sagot.go4lunch.Controllers.Base.BaseActivity;
import com.android.sagot.go4lunch.Controllers.Fragments.ListRestaurantsViewFragment;
import com.android.sagot.go4lunch.Controllers.Fragments.MapViewFragment;
import com.android.sagot.go4lunch.Controllers.Fragments.ListWorkmatesViewFragment;
import com.android.sagot.go4lunch.Models.Go4LunchViewModel;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceDetails.PlaceDetails;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceNearBySearch.PlaceNearBySearch;
import com.android.sagot.go4lunch.Models.RestaurantDetails;
import com.android.sagot.go4lunch.Models.WorkmateDetails;
import com.android.sagot.go4lunch.R;
import com.android.sagot.go4lunch.Utils.GooglePlaceStreams;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Michaël SAGOT on 17/08/2018.
 */

public class WelcomeActivity extends BaseActivity
                            implements  NavigationView.OnNavigationItemSelectedListener,
                                        MapViewFragment.ShowSnackBarListener  {

    // FOR TRACES
    private static final String TAG = WelcomeActivity.class.getSimpleName();

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.activity_welcome_coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.activity_welcome_bottom_navigation) BottomNavigationView bottomNavigationView;
    @BindView(R.id.activity_welcome_drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.activity_welcome_nav_view) NavigationView mNavigationView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    // -- Bottom Navigation View configuration
    // Declare three fragment for used with the Bottom Navigation view
    private Fragment mMapViewFragment;
    private Fragment mListRestaurantsViewFragment;
    private Fragment mListWorkmatesViewFragment;
    // Declare an object fragment which will contain the active fragment
    private Fragment mActiveFragment;
    // Declare an object fragment Manager
    private FragmentManager mFragmentManager;

    // list of participating workmates
    private List<WorkmateDetails> mWorkmatesDetails;

    //--------------------------
    // FOR DETERMINATE LOCATION
    //--------------------------
    // ==> For use location permission
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;  // Request Code
    // For save the status of the location permission granted
    private boolean mLocationPermissionGranted;
    // Default location if not permission granted ( Paris )
    private final LatLng mDefaultLocation = new LatLng(48.844304, 2.374377);
    // The geographical location where the device is currently located.
    // That is, the last-known location retrieved by the Fused Location Provider.
    // OR the default Location if permission not Granted
    private Location mLastKnownLocation;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //--------------------------
    //   FOR USE API REST
    //--------------------------
    // Declare Subscription
    protected Disposable mDisposable;

    // -------------------------
    // DECLARATION BASE METHODS
    // -------------------------
    // BASE METHOD Implementation
    // Get the activity layout
    // CALLED BY BASE METHOD 'onCreate(...)'
    @Override
    protected int getActivityLayout() {
        return R.layout.activity_welcome;
    }

    // BASE METHOD Implementation
    // Get the coordinator layout
    // CALLED BY BASE METHOD
    @Override
    protected View getCoordinatorLayout() {
        return mCoordinatorLayout;
    }

    // ---------------------------------------------------------------------------------------------
    //                                      ENTRY POINT
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the list of Workmates registered in the
        this.getWorkmates();

        // Get Location permission
        getLocationPermission();

        // NAVIGATION DRAWER
        // Configure all views of the Navigation Drawer
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();
    }

    // ---------------------------------------------------------------------------------------------
    //                                     TOOLBAR
    // ---------------------------------------------------------------------------------------------
    protected void configureToolBar() {
        Log.d(TAG, "configureToolBar: ");

        // Change the toolbar Tittle
        setTitle("l'm Hungry!");
        // Sets the Toolbar
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        //Inflate the toolbar  and add it to the Toolbar
        // With one search button
        getMenuInflater().inflate(R.menu.activity_welcome_menu_toolbar, menu);
        return true;
    }

    // ---------------------------------------------------------------------------------------------
    //                                     NAVIGATION DRAWER
    // ---------------------------------------------------------------------------------------------
    // >> CONFIGURATION <-------
    // Configure Drawer Layout and connects him the ToolBar and the NavigationView
    private void configureDrawerLayout() {
        Log.d(TAG, "configureDrawerLayout: ");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // Configure NavigationView
    private void configureNavigationView() {
        Log.d(TAG, "configureNavigationView: ");
        // Subscribes to listen the navigationView
        mNavigationView.setNavigationItemSelectedListener(this);
        // Mark as selected the first menu item
        this.mNavigationView.getMenu().getItem(0).setChecked(true);
    }

    // >> ACTIONS <-------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected: ");

        // Handle Navigation Item Click
        int id = item.getItemId();

        switch (id) {
            case R.id.activity_welcome_drawer_your_lunch:
                Intent intent = new Intent(this, RestaurantCardActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.activity_welcome_drawer_settings:
                break;
            case R.id.activity_welcome_drawer_logout:
                this.signOutUserFromFireBase();
                break;
            default:
                break;
        }
        // Close menu drawer
        this.mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        // Close the menu so open and if the touch return is pushed
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    // ----------------------------
    // REST REQUESTS FOR SIGN OUT
    // ----------------------------
    // Create http requests (SignOut)
    private void signOutUserFromFireBase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted());
    }

    // Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(){
        return aVoid -> finish();
    }

    // ------------------------------
    // GET LIST WORKMATES REGISTERED
    // ------------------------------
    private void getWorkmates(){

        mWorkmatesDetails = new ArrayList<>();

        WorkmateDetails workmateDetails = new WorkmateDetails();
        workmateDetails.setName("Julien");
        workmateDetails.setRestaurantName("Le Grill du Barbu");
        workmateDetails.setRestaurantIdentifier("ChIJZ7eTV3Ua3UcRU8Fzi4HfXG0");
        workmateDetails.setParticipantPhotoUrl("https://9d4912fdd2045774d0d6-e8a43afd12aae363bbf177a3329a9da5.ssl.cf2.rackcdn.com/images/500xblank/ANDRU.jpg");
        mWorkmatesDetails.add(workmateDetails);

        workmateDetails = new WorkmateDetails();
        workmateDetails.setName("Hélène");
        workmateDetails.setRestaurantName("La cuise de la chauve souris verte");
        workmateDetails.setRestaurantIdentifier("ChIJZ7eTV3Ua3UcRU8Fzi4HfXG0");
        workmateDetails.setParticipantPhotoUrl("https://9d4912fdd2045774d0d6-e8a43afd12aae363bbf177a3329a9da5.ssl.cf2.rackcdn.com/images/500xblank/ANDRU.jpg");
        mWorkmatesDetails.add(workmateDetails);

        workmateDetails = new WorkmateDetails();
        workmateDetails.setName("Bubule");
        workmateDetails.setRestaurantName("La cuise de la chauve souris verte");
        workmateDetails.setRestaurantIdentifier("ChIJZ7eTV3Ua3UcRU8Fzi4HfXG0");
        workmateDetails.setParticipantPhotoUrl("https://9d4912fdd2045774d0d6-e8a43afd12aae363bbf177a3329a9da5.ssl.cf2.rackcdn.com/images/500xblank/ANDRU.jpg");
        mWorkmatesDetails.add(workmateDetails);

        workmateDetails = new WorkmateDetails();
        workmateDetails.setName("Tarzan");
        workmateDetails.setRestaurantName("En haut de l''arbre");
        workmateDetails.setRestaurantIdentifier("ChIJZ7eTV3Ua3UcRU8Fzi4HfXG0");
        workmateDetails.setParticipantPhotoUrl("https://9d4912fdd2045774d0d6-e8a43afd12aae363bbf177a3329a9da5.ssl.cf2.rackcdn.com/images/500xblank/ANDRU.jpg");
        mWorkmatesDetails.add(workmateDetails);

        Go4LunchViewModel model = ViewModelProviders.of(this).get(Go4LunchViewModel.class);
        model.setWorkmatesDetails(mWorkmatesDetails);
    }

    // ---------------------------------------------------------------------------------------------
    //                                    PERMISSION METHODS
    // ---------------------------------------------------------------------------------------------
    /**
     * Controls location permission.
     * If they aren't allowed, prompts the user for permission to use the device location.
     * The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: ");
        // Request for unnecessary permission before version Android 6.0 (API level 23)
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            // Check if permissions are already authorized
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permissions Granted
                // Get the last know location of the phone
                Log.d(TAG, "getLocationPermission: Permission already granted by User");
                mLocationPermissionGranted = true;
                getLastKnownCurrentLocationDevice();
            } else {
                // Permissions not Granted
                Log.d(TAG, ">>-- Ask the user for Location Permission --<<");
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
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
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permissions Granted
                    // Get the last know location of the phone
                    Log.d(TAG, "onRequestPermissionsResult: Permission Granted by User :-)");
                    mLocationPermissionGranted = true;
                    getLastKnownCurrentLocationDevice();
                }
                else{
                    Log.d(TAG, "onRequestPermissionsResult: Permission not Granted by User :-(");
                    mLocationPermissionGranted = false;
                    // The last know location will be the default position
                    mLastKnownLocation = new Location("");
                    mLastKnownLocation.setLatitude(mDefaultLocation.latitude);
                    mLastKnownLocation.setLongitude(mDefaultLocation.longitude);

                    // Configure PlacesService
                    this.configureGooglePlaceService();
                }
            }
        }
    }

    /**
     * Retrieves Coordinates of the best and most recent device location information if Exists
     */
    private void getLastKnownCurrentLocationDevice() {
        Log.d(TAG, "getLastKnownCurrentLocationDevice: ");
        // Construct a FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            // Retrieves information if existing
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    mLastKnownLocation = task.getResult();
                    if( mLastKnownLocation != null ) {
                        Log.d(TAG, "getLastKnownCurrentLocationDevice: getLastLocation EXIST");
                        Log.d(TAG, "getLastKnownCurrentLocationDevice: mLastKnownLocation.getLatitude()  = " + mLastKnownLocation.getLatitude());
                        Log.d(TAG, "getLastKnownCurrentLocationDevice: mLastKnownLocation.getLongitude() = " + mLastKnownLocation.getLongitude());
                    }
                    else {
                        Log.d(TAG, "getLastKnownCurrentLocationDevice: getLastLocation NO EXIST");
                        // The last know location will be the default position
                        mLastKnownLocation = new Location("");
                        mLastKnownLocation.setLatitude(mDefaultLocation.latitude);
                        mLastKnownLocation.setLongitude(mDefaultLocation.longitude);
                    }
                    // Configure PlacesService
                    this.configureGooglePlaceService();
                }
            });
        } catch (SecurityException e)  {
            Log.e("getDeviceLocation %s", e.getMessage());
        }
    }

    // ---------------------------------------------------------------------------------------------
    //                          GOOGLE PLAY SERVICE : PLACES
    // ---------------------------------------------------------------------------------------------
    public static String locationStringFromLocation(final Location location) {
        return Location.convert(location.getLatitude(), Location.FORMAT_DEGREES)
                + "," + Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
    }

    public void configureGooglePlaceService() {
        Log.d(TAG, "configureGooglePlaceService: ");

        // Get api_key
        String key = getResources().getString(R.string.google_maps_key);

        // Criteria of the query
        Map<String, String> mQuery;
        // Create filters
        mQuery = new HashMap<>();
        // --> Add Criteria <--
        // -- Location Paris Gare de lyon
        mQuery.put("key", key);
        mQuery.put("placeid", "ChIJZ7eTV3Ua3UcRU8Fzi4HfXG0");


        // Execute the stream subscribing to Observable defined inside GooglePlaceStreams
        mDisposable = GooglePlaceStreams.streamFetchPlaceDetails(mQuery)
                .subscribeWith(new DisposableObserver<PlaceDetails>() {
                    @Override
                    public void onNext(PlaceDetails placeDetails) {
                        Log.d(TAG, "onNext: ");
                        // Analyze the answer
                        responseHttpRequestAnalyze(placeDetails);
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

    public void configureGooglePlaceService2() {
        Log.d(TAG, "configureGooglePlaceService: ");

        // Get api_key
        String key = getResources().getString(R.string.google_maps_key);

        // Criteria of the query
        Map<String, String> mQuery;
        // Create filters
        mQuery = new HashMap<>();
        // --> Add Criteria <--
        // -- Location Paris Gare de lyon
        mQuery.put("key", key);
        Log.d(TAG, "configureGooglePlaceService: Location = "+locationStringFromLocation(mLastKnownLocation));
        mQuery.put("location",locationStringFromLocation(mLastKnownLocation));
        mQuery.put("radius", "100");
        mQuery.put("type", "restaurant");

        // Execute the stream subscribing to Observable defined inside GooglePlaceStreams
        mDisposable = GooglePlaceStreams.streamFetchPlacesNearBySearch(mQuery)
                .subscribeWith(new DisposableObserver<PlaceNearBySearch>() {
                    @Override
                    public void onNext(PlaceNearBySearch placeDetails) {
                        Log.d(TAG, "onNext: ");
                        // Analyze the answer
                        //responseHttpRequestAnalyze(placeDetails);
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
    protected void responseHttpRequestAnalyze(PlaceDetails placeDetails) {
        Log.d(TAG, "responseHTTPRequestAnalyze: ");

        //List of restaurants found
        //List<RestaurantDetails> mListRestaurantsDetails;
        List<PlaceDetails> mListPlaceDetails;

        // Creating a list of RestaurantDetails Restaurants
        mListPlaceDetails = new ArrayList<>();
        mListPlaceDetails.add(placeDetails);

        //Instantiate a RestaurantDetails Restaurant Variable
        RestaurantDetails restaurant;
        List<RestaurantDetails> mListRestaurantsDetails;
        mListRestaurantsDetails = new ArrayList<>();

        //Here we recover only the elements of the query that interests us
        for (PlaceDetails placeD : mListPlaceDetails) {

            // Creating a New RestaurantDetails Restaurant Variable
            restaurant = new RestaurantDetails();
            restaurant.setId(placeD.getResult().getPlaceId());
            restaurant.setName(placeD.getResult().getName());
            if (placeD.getResult().getOpeningHours().getOpenNow() == true){
                restaurant.setOpeningTime(placeD.getResult().getOpeningHours().getPeriods().get(0).getOpen().getTime());
            }else restaurant.setOpeningTime("Closing soon");
            restaurant.setAddress(placeD.getResult().getFormattedAddress());
            restaurant.setLat(placeD.getResult().getGeometry().getLocation().getLat().toString());
            restaurant.setLng(placeD.getResult().getGeometry().getLocation().getLng().toString());
            restaurant.setNbrStars(2);
            restaurant.setNbrParticipants(9);
            restaurant.setPhotoUrl("https://cdn.pixabay.com/photo/2018/07/14/15/27/cafe-3537801_960_720.jpg");

            mListRestaurantsDetails.add(restaurant);
            Log.d(TAG, "responseHttpRequestAnalyze: ***************************************");
            Log.d(TAG, "responseHttpRequestAnalyze:      Place Id    = "+restaurant.getId());
            Log.d(TAG, "responseHttpRequestAnalyze:      Name        = "+restaurant.getName());
            Log.d(TAG, "responseHttpRequestAnalyze:      Address     = "+restaurant.getAddress());
            Log.d(TAG, "responseHttpRequestAnalyze:      OpeningTime = "+restaurant.getOpeningTime());
            Log.d(TAG, "responseHttpRequestAnalyze:      Lat         = "+restaurant.getLat());
            Log.d(TAG, "responseHttpRequestAnalyze:      Lng         = "+restaurant.getLng());
        }

        Go4LunchViewModel model = ViewModelProviders.of(this).get(Go4LunchViewModel.class);
        // Save Restaurant List
        model.setRestaurantsDetails(mListRestaurantsDetails);
        // Save Location Permission
        model.setLocationPermissionGranted(mLocationPermissionGranted);

        Log.d(TAG, "responseHttpRequestAnalyze: isLocationPermissionGranted = "+model.isLocationPermissionGranted());

        // We have recovered all the data necessary for the display
        // We can display and make the interface available
        configureBottomView();
    }

    // ---------------------------------------------------------------------------------------------
    //                                 BOTTOM NAVIGATION VIEW
    // ---------------------------------------------------------------------------------------------
    // Configure the BottomNavigationView
    private void configureBottomView(){
        Log.d(TAG, "configureBottomView: ");

        // Configure the BottomNavigationView Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> updateMainFragment(item.getItemId()));

        // Add three fragments used by the FragmentManager and activates only the Fragment MapViewFragment
        addFragmentsInFragmentManager();
    }

    // >> ACTIONS <-------
    private Boolean updateMainFragment(Integer integer){
        switch (integer) {
            case R.id.action_map_view:
                // Hide the active fragment and activates the fragment mMapViewFragment
                mFragmentManager.beginTransaction().hide(mActiveFragment).show(mMapViewFragment).commit();
                mActiveFragment = mMapViewFragment;
                break;
            case R.id.action_list_view:
                // Hide the active fragment and activates the fragment mListViewFragment
                mFragmentManager.beginTransaction().hide(mActiveFragment).show(mListRestaurantsViewFragment).commit();
                mActiveFragment = mListRestaurantsViewFragment;
                break;
            case R.id.action_workmates:
                // Hide the active fragment and activates the fragment mWorkmatesFragment
                mFragmentManager.beginTransaction().hide(mActiveFragment).show(mListWorkmatesViewFragment).commit();
                mActiveFragment = mListWorkmatesViewFragment;
                break;
        }
        return true;
    }

    // -----------
    //  FRAGMENTS
    // -----------
    private void addFragmentsInFragmentManager(){
        Log.d(TAG, "addFragments: ");

        //Instantiate fragment used by BottomNavigationView
        mMapViewFragment = MapViewFragment.newInstance(mLastKnownLocation);
        mListRestaurantsViewFragment = ListRestaurantsViewFragment.newInstance();
        mListWorkmatesViewFragment = ListWorkmatesViewFragment.newInstance(mWorkmatesDetails);

        // Save the active Fragment
        mActiveFragment = mMapViewFragment;

        // Obtain SupportFragmentManager Object
        mFragmentManager = getSupportFragmentManager();
        // Add the three fragment in fragmentManager and leave active only the fragment MapViewFragment
        mFragmentManager.beginTransaction()
                .add(R.id.activity_welcome_frame_layout_bottom_navigation, mListWorkmatesViewFragment,"ListWorkmatesViewFragment")
                .hide(mListWorkmatesViewFragment).commit();
        mFragmentManager.beginTransaction()
                .add(R.id.activity_welcome_frame_layout_bottom_navigation, mListRestaurantsViewFragment,"ListViewFragment")
                .hide(mListRestaurantsViewFragment).commit();
        mFragmentManager.beginTransaction()
                .add(R.id.activity_welcome_frame_layout_bottom_navigation, mMapViewFragment,"MapViewFragment")
                .commit();
    }


}
