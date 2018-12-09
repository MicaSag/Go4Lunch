package com.android.sagot.go4lunch.Controllers.Activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sagot.go4lunch.Controllers.Base.BaseActivity;
import com.android.sagot.go4lunch.Controllers.Fragments.ListRestaurantsViewFragment;
import com.android.sagot.go4lunch.Controllers.Fragments.ListWorkmatesViewFragment;
import com.android.sagot.go4lunch.Controllers.Fragments.MapViewFragment;
import com.android.sagot.go4lunch.Models.Go4LunchViewModel;
import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.android.sagot.go4lunch.Models.firestore.User;
import com.android.sagot.go4lunch.Models.sharedPreferences.Preferences_SettingsActivity;
import com.android.sagot.go4lunch.R;
import com.android.sagot.go4lunch.Utils.GooglePlaceStreams;
import com.android.sagot.go4lunch.Utils.ManageRestaurantList;
import com.android.sagot.go4lunch.Utils.Toolbox;
import com.android.sagot.go4lunch.api.RestaurantHelper;
import com.android.sagot.go4lunch.api.UserHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
                    ManageRestaurantList.Listeners{
    // For TRACES
    // -----------
    private static final String TAG = WelcomeActivity.class.getSimpleName();

    // For use VIEWS
    // --------------
    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.activity_welcome_coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.activity_welcome_root_linear_layout) LinearLayout mRootLinearLayout;
    @BindView(R.id.activity_welcome_bottom_navigation) BottomNavigationView bottomNavigationView;
    @BindView(R.id.activity_welcome_drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.activity_welcome_nav_view) NavigationView mNavigationView;
    @BindView(R.id.activity_welcome_progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    // For Defined SharedPreferences of the application
    // -------------------------------------------------
    SharedPreferences mSharedPreferences;
    // Create variables for use preferences Settings Activity
    private String mPreferences_SettingsActivity_String;
    private Preferences_SettingsActivity mPreferences_SettingsActivity;

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

    // For use Place AutoComplete
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;

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
        Log.d(TAG, "onCreate: ");

        // Retrieve SharedPreferences
        retrieveSharedPreferences();

        // Place Autocomplete Configuration
        //configurePlaceAutoComplete();

        // Get Location permission
        getLocationPermission();

        // NAVIGATION DRAWER
        // Configure all views of the Navigation Drawer
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();

        // Allows the management of the change of position when moving
        this.configureLocationChangeRealTime();
    }
    // ---------------------------------------------------------------------------------------------
    //                                   SHARED PREFERENCES
    // ---------------------------------------------------------------------------------------------
    // >> SHARED PREFERENCES RETRIEVE <-------
    private void retrieveSharedPreferences() {
        Log.d(TAG, "retrieveSharedPreferences: ");
        // READ SharedPreferences
        mSharedPreferences = getPreferences(MODE_PRIVATE);

        // TEST == >>> Allows to erase all the preferences ( Useful for the test phase )
        //Log.i("MOOD","CLEAR COMMIT PREFERENCES");
        //mSharedPreferences.edit().clear().commit();

        // Retrieve Settings Activity Preferences
        getPreferencesSettingsActivity();
    }
    // Retrieve Settings Activity Preferences
    private void getPreferencesSettingsActivity(){
        Log.d(TAG, "getPreferencesSettingsActivity: ");

        mPreferences_SettingsActivity_String = mSharedPreferences
                .getString(SettingsActivity.SHARED_PREF_SETTINGS_ACTIVITY,null);

        Gson gson = new Gson();
        // If
        if (mPreferences_SettingsActivity_String != null) {
            Log.d(TAG, "retrievesPreferencesSettings: Restoration");
            mPreferences_SettingsActivity = gson.fromJson(mPreferences_SettingsActivity_String,
                    Preferences_SettingsActivity.class);
        }else{
            Log.d(TAG, "retrievesPreferencesSettings: First call of the App, No pref saved");
            mPreferences_SettingsActivity = new Preferences_SettingsActivity();
        }
        // If search Radius not exist, then it will be 500 by default
        if (mPreferences_SettingsActivity.getSearchRadius() == null)
            mPreferences_SettingsActivity.setSearchRadius("500");

        gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
        mPreferences_SettingsActivity_String = gson.toJson(mPreferences_SettingsActivity);
    }
    // >> SHARED PREFERENCES SAVE <-------
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");

        // Save Settings Activity Preferences
        mSharedPreferences.edit().putString(SettingsActivity
                .SHARED_PREF_SETTINGS_ACTIVITY, mPreferences_SettingsActivity_String).apply();
    }
    // ---------------------------------------------------------------------------------------------
    //                                     TOOLBAR
    // ---------------------------------------------------------------------------------------------
    protected void configureToolBar() {
        Log.d(TAG, "configureToolBar: ");

        // Change the toolbar Title
        setTitle(R.string.activity_welcome_title);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.toolbar_menu_search:
                callPlaceAutoComplete();
        }
        return super.onOptionsItemSelected(item);
    }
    // ---------------------------------------------------------------------------------------------
    //                              PLACE AUTOCOMPLETE CONFIGURATION
    // ---------------------------------------------------------------------------------------------
    private void callPlaceAutoComplete() {
        try {

            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }
    // ---------------------------------------------------------------------------------------------
    //                                    ON ACTIVITY RESULT
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode
                + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        // Result Returned by Place Auto Complete
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: Place auto Complete RETURN");
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: Place auto Complete RETURN : Result OK");
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Log.d(TAG, "onActivityResult: Place auto Complete RETURN : Result ERROR");
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        // Result Returned by Settings Activity
        if (requestCode == SettingsActivity.SETTINGS_ACTIVITY_RC) {
            Log.d(TAG, "onActivityResult: Settings Activity RETURN");
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: Settings Activity RETURN : Result OK");
                mPreferences_SettingsActivity_String
                        = data.getStringExtra(SettingsActivity.SHARED_PREF_SETTINGS_ACTIVITY);
            } else {
                Log.d(TAG, "onActivityResult: Settings Activity RETURN : Result KO");
            }
        }
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

        if (this.getCurrentUser() != null) {

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
            //==> Click on YOUR LUNCH
            case R.id.activity_welcome_drawer_your_lunch:
                goToRestaurantActivity();
                break;
            //==> Click on SETTINGS
            case R.id.activity_welcome_drawer_settings:
                goToSettingsActivity();
                break;
            //==> Click on LOGOUT
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

    // GO TO Restaurant Card Activity
    public void goToRestaurantActivity() {
        Log.d(TAG, "goToRestaurantActivity: ");

        // Get additional data from FireStore : restaurantIdentifier of the User choice
        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            User currentUser = documentSnapshot.toObject(User.class);
            if (currentUser.getRestaurantIdentifier() != null) {
                callRestaurantCardActivity(currentUser);
            } else showSnackBar(getString(R.string.restaurant_not_chosen));
        });
    }

    // CALL Restaurant Card activity
    public void callRestaurantCardActivity(User currentUser){
        Log.d(TAG, "callRestaurantCardActivity: ");

        // Go to restaurant card
        Toolbox.startActivity(this, RestaurantCardActivity.class,
                RestaurantCardActivity.KEY_DETAILS_RESTAURANT_CARD,
                currentUser.getRestaurantIdentifier());
    }

    // GO TO Settings Activity
    public void goToSettingsActivity() {
        Log.d(TAG, "goToSettingsActivity: ");

        callSettingsActivity();
    }

    // CALL Settings activity
    public void callSettingsActivity(){
        Log.d(TAG, "callSettingsActivity: ");

        // GO TO Settings Activity
        Intent intent = new Intent(WelcomeActivity.this,SettingsActivity.class);
        intent.putExtra(SettingsActivity.SHARED_PREF_SETTINGS_ACTIVITY,
                                            mPreferences_SettingsActivity_String);
        startActivityForResult(intent,SettingsActivity.SETTINGS_ACTIVITY_RC);
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
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
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
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Permission not Granted by User :-(");
                    mLocationPermissionGranted = false;
                    // The last know location will be the default position
                    mLastKnownLocation = new Location("");
                    mLastKnownLocation.setLatitude(mDefaultLocation.latitude);
                    mLastKnownLocation.setLongitude(mDefaultLocation.longitude);

                    // Save Location in Model
                    getModel().setCurrentLocation(mLastKnownLocation);

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
                    if (mLastKnownLocation != null) {
                        Log.d(TAG, "getLastKnownCurrentLocationDevice: getLastLocation EXIST");
                        Log.d(TAG, "getLastKnownCurrentLocationDevice: mLastKnownLocation.getLatitude()  = " + mLastKnownLocation.getLatitude());
                        Log.d(TAG, "getLastKnownCurrentLocationDevice: mLastKnownLocation.getLongitude() = " + mLastKnownLocation.getLongitude());
                    } else {
                        Log.d(TAG, "getLastKnownCurrentLocationDevice: getLastLocation NO EXIST");
                        // The last know location will be the default position
                        mLastKnownLocation = new Location("");
                        mLastKnownLocation.setLatitude(mDefaultLocation.latitude);
                        mLastKnownLocation.setLongitude(mDefaultLocation.longitude);
                    }
                    // Save Location in Model
                    getModel().setCurrentLocation(mLastKnownLocation);

                    // Get List restaurants Details and save its in Model
                    this.getListRestaurantsDetails();
                }
            });
        } catch (SecurityException e) {
            Log.e("getDeviceLocation %s", e.getMessage());
        }
    }
    // ---------------------------------------------------------------------------------------------
    //                                    REST REQUESTS
    // ---------------------------------------------------------------------------------------------
    //      FOR SIGN OUT REQUEST
    // -------------------------------
    // Create http requests (SignOut)
    private void signOutUserFromFireBase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted());
    }

    // Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted() {
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
                + Toolbox.locationStringFromLocation(mLastKnownLocation));
        // Execute the stream subscribing to Observable defined inside GooglePlaceStreams
        mDisposable = GooglePlaceStreams
                .streamFetchListRestaurantDetails(this, mLastKnownLocation,
                                                    mPreferences_SettingsActivity.getSearchRadius())
                .subscribeWith(new DisposableObserver<List<Restaurant>>() {
                    @Override
                    public void onNext(List<Restaurant> listRestaurant) {
                        Log.d(TAG, "getListRestaurantsDetails : onNext: ");

                        // Load Restaurant List in ViewModel
                        Map<String, Restaurant> restos = new LinkedHashMap<>();
                        for (Restaurant restaurant : listRestaurant) {
                            Log.d(TAG, "onNext: Distance = " + restaurant.getDistance());
                            restos.put(restaurant.getIdentifier(), restaurant);
                        }
                        saveRestaurantMapInModel(restos);
                        Log.d(TAG, "onNext: getRestaurantMapOfTheModel().size()  = "
                                + getRestaurantMapOfTheModel().size());

                        // Manage restaurant List
                        startManageRestaurantList();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Display a toast message
                        updateUIWhenErrorHTTPRequest();
                        Log.d(TAG, "getListRestaurantsDetails : onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "getListRestaurantsDetails : On Complete !!");

                    }
                });
    }

    // Generate a toast Message if error during Downloading
    protected void updateUIWhenErrorHTTPRequest() {
        Log.d(TAG, "updateUIWhenErrorHTTPRequest: ");

        Toast.makeText(this, "Error during Downloading", Toast.LENGTH_LONG).show();
    }
    // ---------------------------------------------------------------------------------------------
    //                         ASYNC TASK : MANAGE RESTAURANT LIST
    // ---------------------------------------------------------------------------------------------
    // 3 - We create and start our AsyncTask
    private void startManageRestaurantList() {
        Log.d(TAG, "startAsyncTask: ");

        new ManageRestaurantList(this).execute();
    }

    // Override methods of callback
    @Override
    public void onPreExecute() {
        Log.d(TAG, "onPreExecute: ");
        // We update our UI before task (starting ProgressBar)
        this.updateUIBeforeTask();
    }
    @Override
    public void doInBackground() {
        Log.d(TAG, "doInBackground: ");

        Set<Map.Entry<String, Restaurant>> setListRestaurant = getRestaurantMapOfTheModel().entrySet();
        Iterator<Map.Entry<String, Restaurant>> it = setListRestaurant.iterator();

        while (it.hasNext()) {
            Map.Entry<String, Restaurant> restaurant = it.next();
            Log.d(TAG, "listenCurrentListRestaurant: restaurant.getValue().getIdentifier() = "
                    + restaurant.getValue().getIdentifier());

            try {
                Task<DocumentSnapshot> task = RestaurantHelper.getRestaurant(restaurant.getValue().getIdentifier());

                //DocumentSnapshot documentSnapshot = Tasks.await(task, 500, TimeUnit.MILLISECONDS);
                DocumentSnapshot documentSnapshot = Tasks.await(task);

                Restaurant fireBaseRestaurant = documentSnapshot.toObject(Restaurant.class);

                if (fireBaseRestaurant == null) {
                    Log.d(TAG, "setListRestaurantsInFireBase: currentRestaurant not exist in FireBase Database");
                    Task<Void> restaurantTask = RestaurantHelper.createRestaurant(restaurant.getValue());

                    Tasks.await(restaurantTask);
                }

                // Listening the restaurant
                listenCurrentRestaurant(restaurant.getValue());

            } catch (ExecutionException e) {
                Log.d(TAG, "putRestaurantInFireBase:ExecutionException = " + e);
            } catch (InterruptedException e) {
                Log.d(TAG, "putRestaurantInFireBase:InterruptedException = " + e);
            //} catch (TimeoutException e) {
            //    Log.d(TAG, "putRestaurantInFireBase:TimeoutException = " + e);
            }
        }
    }
    @Override
    public void onPostExecute(Long taskEnd) {
        Log.d(TAG, "onPostExecute: ");
        // We update our UI before task (stopping ProgressBar)
        this.updateUIAfterTask(taskEnd);
        //  We update our UI before task (stopping ProgressBar)
        // We have recovered all the data necessary for the display
        // We can display and make the interface available
        configureBottomView();
    }
    /**
     * Enables listening of the current restaurant
     */
    private void listenCurrentRestaurant(Restaurant restaurant) {
        Log.d(TAG, "listenCurrentListRestaurant: ");


            Log.d(TAG, "listenCurrentListRestaurant: restaurant.getValue().getIdentifier() = "
                    + restaurant.getIdentifier());
            RestaurantHelper
                    .getRestaurantsCollection()
                    .document(restaurant.getIdentifier())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot document, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.d(TAG, "fireStoreListener.onEvent: Listen failed: " + e);
                                return;
                            }
                            if (document != null) {
                                Restaurant rest = document.toObject(Restaurant.class);
                                Log.d(TAG, "listenCurrentListRestaurant.onEvent : rest.getIdentifier() = " + rest.getIdentifier());
                                WelcomeActivity.this.getRestaurantMapOfTheModel().put(rest.getIdentifier(), rest);
                            }
                        }
                    });
    }
    // ---------------------------------------------------------------------------------------------
    //                                     UPDATE UI
    // ---------------------------------------------------------------------------------------------

    private void updateUIBeforeTask(){
        Log.d(TAG, "updateUIBeforeTask: ");

        Snackbar.make(mCoordinatorLayout,R.string.welcome_activity_download_progress,Snackbar.LENGTH_LONG).show();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void updateUIAfterTask(Long taskEnd){
        Log.d(TAG, "updateUIAfterTask: ");
        Log.d(TAG, "Task is finally finished at : "+taskEnd+" !");

        Snackbar.make(mCoordinatorLayout,R.string.welcome_activity_download_finish,Snackbar.LENGTH_LONG).show();
        mProgressBar.setVisibility(View.GONE);
    }
    // ---------------------------------------------------------------------------------------------
    //                                 BOTTOM NAVIGATION VIEW
    // ---------------------------------------------------------------------------------------------
    // Configure the BottomNavigationView
    private void configureBottomView() {
        Log.d(TAG, "configureBottomView: ");

        // Configure the BottomNavigationView Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> updateMainFragment(item.getItemId()));

        // Add three fragments used by the FragmentManager and activates only the Fragment MapViewFragment
        addFragmentsInFragmentManager();
    }

    // >> ACTIONS <-------
    private Boolean updateMainFragment(Integer integer) {
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
    private void addFragmentsInFragmentManager() {
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
                .add(R.id.activity_welcome_frame_layout_bottom_navigation, mListWorkmatesViewFragment, "ListWorkmatesViewFragment")
                .hide(mListWorkmatesViewFragment).commit();
        mFragmentManager.beginTransaction()
                .add(R.id.activity_welcome_frame_layout_bottom_navigation, mListRestaurantsViewFragment, "ListViewFragment")
                .hide(mListRestaurantsViewFragment).commit();
        mFragmentManager.beginTransaction()
                .add(R.id.activity_welcome_frame_layout_bottom_navigation, mMapViewFragment, "MapViewFragment")
                .commit();
    }
    // ---------------------------------------------------------------------------------------------
    //                                LOCATION IN REAL TIME
    // ---------------------------------------------------------------------------------------------
    private void configureLocationChangeRealTime() {
        Log.d(TAG, "configureLocationChangeRealTime: ");

        // The minimum time (in MilliSeconds) the system will wait until checking if the location changed
        int minTime = 60000;
        // The minimum distance (in meters) traveled until you will be notified
        float minDistance = 15;
        // Get the location manager from the system
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Get the criteria you would like to use
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setSpeedRequired(false);
        // Get the best provider from the criteria specified, and false to say it can turn the provider on if it isn't already
        String bestProvider = locationManager.getBestProvider(criteria, false);
        // Request location updates
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: ");

                if (location != null && mListRestaurantsViewFragment != null) {
                    getModel().setCurrentLocation(location);
                    ((ListRestaurantsViewFragment) mListRestaurantsViewFragment).updateUI();
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, locationListener);
    }
}
