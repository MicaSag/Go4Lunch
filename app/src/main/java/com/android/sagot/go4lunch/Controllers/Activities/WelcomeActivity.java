package com.android.sagot.go4lunch.Controllers.Activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sagot.go4lunch.Controllers.Base.BaseActivity;
import com.android.sagot.go4lunch.Controllers.Fragments.ListRestaurantsViewFragment;
import com.android.sagot.go4lunch.Controllers.Fragments.ListWorkmatesViewFragment;
import com.android.sagot.go4lunch.Controllers.Fragments.MapViewFragment;
import com.android.sagot.go4lunch.Models.Go4LunchViewModel;
import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.android.sagot.go4lunch.Models.firestore.User;
import com.android.sagot.go4lunch.R;
import com.android.sagot.go4lunch.Utils.GooglePlaceStreams;
import com.android.sagot.go4lunch.Utils.Toolbox;
import com.android.sagot.go4lunch.api.RestaurantHelper;
import com.android.sagot.go4lunch.api.UserHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**************************************************************************************************
 *
 *  ACTIVITY that displays Body of the application
 *  -----------------------------------------------
 *  EXTENDS     : BaseActivity
 *  IMPLEMENTS  : NavigationView.OnNavigationItemSelectedListener
 *                  => To use the actions performed on the NavigationView
 *              : MapViewFragment.ShowSnackBarListener
 *                  => For Showing MapViewFragment information messages
 *
 **************************************************************************************************/

public class WelcomeActivity extends BaseActivity
                            implements  NavigationView.OnNavigationItemSelectedListener,
                                        MapViewFragment.ShowSnackBarListener  {
    // For TRACES
    // -----------
    private static final String TAG = WelcomeActivity.class.getSimpleName();

    // For use VIEWS
    // --------------
    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.activity_welcome_coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.activity_welcome_bottom_navigation) BottomNavigationView bottomNavigationView;
    @BindView(R.id.activity_welcome_drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.activity_welcome_nav_view) NavigationView mNavigationView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    // For configuration Bottom Navigation View
    // -----------------------------------------
    // Declare three fragment for used with the Bottom Navigation view
    private Fragment mMapViewFragment;
    private Fragment mListRestaurantsViewFragment;
    private Fragment mListWorkmatesViewFragment;
    // Declare an object fragment which will contain the active fragment
    private Fragment mActiveFragment;
    // Declare an object fragment Manager
    private FragmentManager mFragmentManager;

    // For use LOCATION permission
    // ---------------------------
    // 1 _ Request Code
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    // 2 _ For save the status of the location permission granted
    private boolean mLocationPermissionGranted;

    // For determinate Location
    // -------------------------
    // Default location if not permission granted ( Paris )
    private final LatLng mDefaultLocation = new LatLng(48.844304, 2.374377);
    // The geographical location where the device is currently located.
    // That is, the last-known location retrieved by the Fused Location Provider.
    // OR the default Location if permission not Granted
    private Location mLastKnownLocation;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // For use API REST
    // -----------------
    // Declare Subscription
    protected Disposable mDisposable;

    List<Restaurant> mListRestaurant;

    int i;

    // ---------------------------------------------------------------------------------------------
    //                                DECLARATION BASE METHODS
    // ---------------------------------------------------------------------------------------------
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

        // Configure NavigationHeader
        configureNavigationHeader();

        // Subscribes to listen the navigationView
        mNavigationView.setNavigationItemSelectedListener(this);
        // Mark as selected the first menu item
        this.mNavigationView.getMenu().getItem(0).setChecked(true);
    }

    // Configure NavigationHeader
    private void configureNavigationHeader() {

        View navigationHeader = mNavigationView.inflateHeaderView(R.layout.activity_welcome_nav_header);
        ImageView userPhoto = navigationHeader.findViewById(R.id.navigation_header_user_photo);
        TextView userName = navigationHeader.findViewById(R.id.navigation_header_user_name);
        TextView userEmail = navigationHeader.findViewById(R.id.navigation_header_user_email);

        if (this.getCurrentUser() != null){

            //Get picture URL from FireBase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(userPhoto);
            }

            //Get email & username from FireBase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail())
                    ? getString(R.string.navigation_header_user_email) : this.getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName())
                    ? getString(R.string.navigation_header_user_name) : this.getCurrentUser().getDisplayName();

            //Update views with data
            userName.setText(username);
            userEmail.setText(email);
        }
    }

    // >> ACTIONS <-------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected: ");

        // Handle Navigation Item Click
        int id = item.getItemId();

        switch (id) {
            case R.id.activity_welcome_drawer_your_lunch:
                // Get additional data from FireStore : restaurantIdentifier of the User choice
                UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User currentUser = documentSnapshot.toObject(User.class);
                        Log.d(TAG, "onSuccess: currentUser.restoIdentifiant = "+currentUser.getRestaurantIdentifier());
                        goToRestaurantActivity(currentUser);
                    }
                });
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
    public void goToRestaurantActivity(User user){
        Toolbox.startActivity(this, RestaurantCardActivity.class,
                RestaurantCardActivity.KEY_DETAILS_RESTAURANT_CARD,
                getRestaurantMapOfTheModel().get(user.getRestaurantIdentifier()));
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        // Close the menu so open and if the touch return is pushed
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        }
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
                // Save Location Permission Granted
                Go4LunchViewModel model = ViewModelProviders.of(this).get(Go4LunchViewModel.class);
                model.setLocationPermissionGranted(mLocationPermissionGranted);
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

                    // Get List Restaurants Details
                    this.getListRestaurantsDetails();
                }
                // Save Location Permission Granted
                Go4LunchViewModel model = ViewModelProviders.of(this).get(Go4LunchViewModel.class);
                model.setLocationPermissionGranted(mLocationPermissionGranted);
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
                    // Get List restaurants Details ans save its in Model
                    this.getListRestaurantsDetails();
                }
            });
        } catch (SecurityException e)  {
            Log.e("getDeviceLocation %s", e.getMessage());
        }
    }
    // ---------------------------------------------------------------------------------------------
    //                                    REST REQUESTS
    // ---------------------------------------------------------------------------------------------
    //      FOR SIGN OUT REQUEST
    // -------------------------------
    // Create http requests (SignOut)
    private void signOutUserFromFireBase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted());
    }
    // Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        };
    }
    // ------------------------------
    //   FOR GOOGLE PLACES REQUEST
    // ------------------------------
    public void getListRestaurantsDetails() {
        Log.d(TAG, "getListRestaurantsDetails: ");

        Log.d(TAG, "getListRestaurantsDetails: mLastKnownLocation = "
                +Toolbox.locationStringFromLocation(mLastKnownLocation));
        // Execute the stream subscribing to Observable defined inside GooglePlaceStreams
        mDisposable = GooglePlaceStreams
                .streamFetchListRestaurantDetails(Toolbox.locationStringFromLocation(mLastKnownLocation),this)
                .subscribeWith(new DisposableObserver<List<Restaurant>>() {
                    @Override
                    public void onNext(List<Restaurant> listRestaurant) {
                        Log.d(TAG, "getListRestaurantsDetails : onNext: ");

                        // Load Restaurant List in ViewModel
                        Map<String,Restaurant> restos = new LinkedHashMap<>();
                        for (Restaurant restaurant : listRestaurant){
                            restos.put(restaurant.getIdentifier(),restaurant);
                        }
                        saveRestaurantMapInModel(restos);
                        Log.d(TAG, "onNext: getRestaurantMapOfTheModel().size()  = "+getRestaurantMapOfTheModel().size());

                        // Listen Current Restaurant List
                        listenCurrentListRestaurant();

                        // Save current Opening Hours in FireBase
                        saveOpeningHoursInFireBase();

                        // We have recovered all the data necessary for the display
                        // We can display and make the interface available
                        configureBottomView();
                    }
                    @Override
                    public void onError(Throwable e) {
                        // Display a toast message
                        updateUIWhenErrorHTTPRequest();
                        Log.d(TAG, "getListRestaurantsDetails : onError: "+e);
                    }
                    @Override
                    public void onComplete() {
                        Log.d(TAG,"getListRestaurantsDetails : On Complete !!");

                    }
                });
    }

    // Generate a toast Message if error during Downloading
    protected void updateUIWhenErrorHTTPRequest(){
        Log.d(TAG, "updateUIWhenErrorHTTPRequest: ");

        Toast.makeText(this, "Error during Downloading", Toast.LENGTH_LONG).show();
    }

    // Save current Opening Hours in FireBase
    protected void saveOpeningHoursInFireBase() {

        Set<Map.Entry<String, Restaurant>> setListRestaurant = getRestaurantMapOfTheModel().entrySet();
        Iterator<Map.Entry<String, Restaurant>> it = setListRestaurant.iterator();
        while(it.hasNext()){
            Map.Entry<String, Restaurant> e = it.next();
            RestaurantHelper.updateRestaurantOpeningTime(e.getValue().getIdentifier(),e.getValue().getOpeningTime())
                    .addOnFailureListener(this.onFailureListener());
        }
    }
    /**
     * Enables listening of the number of stars
     */
    public void listenCurrentListRestaurant() {
        Log.d(TAG, "listenCurrentListRestaurant: ");

        Set<Map.Entry<String, Restaurant>> setListRestaurant = getRestaurantMapOfTheModel().entrySet();
        Iterator<Map.Entry<String, Restaurant>> it = setListRestaurant.iterator();
        while(it.hasNext()){
            Map.Entry<String, Restaurant> restaurant = it.next();
            RestaurantHelper
                    .getRestaurantsCollection()
                    .document(restaurant.getValue().getIdentifier())
                    .addSnapshotListener((document, e) -> {
                        if (e != null) {
                            Log.d(TAG, "fireStoreListener.onEvent: Listen failed: " + e);
                            return;
                        }
                        Restaurant rest = document.toObject(Restaurant.class);
                        Log.d(TAG, "onEvent: Id restaurant = "+ rest.getIdentifier());
                        //ou Log.d(TAG, "onEvent: Id restaurant = "+document.get("identifier"));

                        getRestaurantMapOfTheModel().put(rest.getIdentifier(), rest);
                    });
        }
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
    // ---------------------------------------------------------------------------------------------
    //                                      FRAGMENTS
    // ---------------------------------------------------------------------------------------------
    private void addFragmentsInFragmentManager(){
        Log.d(TAG, "addFragments: ");

        //Instantiate fragment used by BottomNavigationView
        mMapViewFragment = MapViewFragment.newInstance(mLastKnownLocation);
        mListRestaurantsViewFragment = ListRestaurantsViewFragment.newInstance();
        mListWorkmatesViewFragment = ListWorkmatesViewFragment.newInstance(null);

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
    // ---------------------------------------------------------------------------------------------
    //                                        ( OUT )
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");

        //Unsubscribe the stream when the Activity is destroyed so as not to create a memory leaks
        this.disposeWhenDestroy();
        super.onDestroy();
    }
    //  Unsubscribe the stream when the Activity is destroyed so as not to create a memory leaks
    private void disposeWhenDestroy(){
        Log.d(TAG, "disposeWhenDestroy: ");

        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
    }
}
