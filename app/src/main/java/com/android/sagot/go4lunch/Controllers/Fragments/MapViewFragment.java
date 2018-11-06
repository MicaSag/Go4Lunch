package com.android.sagot.go4lunch.Controllers.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.sagot.go4lunch.Controllers.Base.BaseFragment;
import com.android.sagot.go4lunch.Models.Go4LunchViewModel;
import com.android.sagot.go4lunch.Models.RestaurantDetails;
import com.android.sagot.go4lunch.R;
import com.android.sagot.go4lunch.api.UserHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**************************************************************************************************
 *
 *  FRAGMENT that displays the Restaurant List
 *  ------------------------------------------
 *  IN = Last Know Location : Location
 *
 **************************************************************************************************/
public class MapViewFragment extends BaseFragment implements OnMapReadyCallback,  GoogleMap.OnInfoWindowClickListener {

    // For debug
    private static final String TAG = MapViewFragment.class.getSimpleName();

    // Parameter for the construction of the fragment
    private static final String KEY_LAST_KNOW_LOCATION = "KEY_LAST_KNOW_LOCATION";

    // For add Google Map in Fragment
    private SupportMapFragment mMapFragment;

    // ==> For use Api Google Play Service : map
    private GoogleMap mMap;

    // ==> For update UI Location
    private static final float DEFAULT_ZOOM = 16f;

    //==> For use Api Google Play Service : Location
    // _ The geographical location where the device is currently located.
    // _ That is, the last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

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
    // ---------------------------------------------------------------------------------------------
    //                               FRAGMENT INSTANTIATION
    // ---------------------------------------------------------------------------------------------
    public static MapViewFragment newInstance(Location lastKnownLocation) {

        // Create new fragment
        MapViewFragment mapViewFragment = new MapViewFragment();

        // Create bundle and add it some data
        Bundle args = new Bundle();
        final Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
        String json = gson.toJson(lastKnownLocation);
        args.putString(KEY_LAST_KNOW_LOCATION, json);

        mapViewFragment.setArguments(args);

        return mapViewFragment;
    }
    // ---------------------------------------------------------------------------------------------
    //                                    ENTRY POINT
    // ---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        // Get data from Bundle (created in method newInstance)
        // Restoring the Date with a Gson Object
        Gson gson = new Gson();
        mLastKnownLocation = gson.fromJson(getArguments().getString(KEY_LAST_KNOW_LOCATION, ""),Location.class);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        // Configure the Maps Service of Google
        configurePlayServiceMaps();

        return rootView;
    }

    // ---------------------------------------------------------------------------------------------
    //                            GOOGLE PLAY SERVICE : MAPS
    // ---------------------------------------------------------------------------------------------
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
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        mMap = googleMap;

        // Disable 3D Building
        mMap.setBuildingsEnabled(false);

        // Activate OnMarkerClickListener
        mMap.setOnInfoWindowClickListener(this);

        // Show current Location
        showCurrentLocation();

        // Display Restaurants pin
        displayRestaurantsMarker();
    }
    // ---------------------------------------------------------------------------------------------
    //                                       ACTIONS
    // ---------------------------------------------------------------------------------------------
    // Click on Restaurants Map Marker
    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "onMarkerClick: ");

        Log.d(TAG, "onMarkerClick: marker.tag = "+marker.getTag());

        //Launch Restaurant Card Activity with restaurantIdentifier
        startRestaurantCardActivity((String) marker.getTag());
    }
    // ---------------------------------------------------------------------------------------------
    //                                       METHODS
    // ---------------------------------------------------------------------------------------------
    /**
     * Method that places the map on a current location
     */
    private void showCurrentLocation() {
        Log.d(TAG, "showCurrentLocation: ");
        Log.d(TAG, "showCurrentLocation: mLastKnownLocation.getLatitude()  = " + mLastKnownLocation.getLatitude());
        Log.d(TAG, "showCurrentLocation: mLastKnownLocation.getLongitude() = " + mLastKnownLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

        // Update Location UI
        updateLocationUI();
    }
    /**
     * Method that display restaurants Markers
     */
    private void displayRestaurantsMarker() {
        Log.d(TAG, "displayRestaurantsMarker: ");

        // Get additional data from FireStore : restaurantIdentifier
        UserHelper.getAllUserInfos().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> querySnapshotTask) {

                if (querySnapshotTask.isSuccessful()) {

                    // Load Restaurant List in ViewModel
                    Go4LunchViewModel model = ViewModelProviders.of(MapViewFragment.this.getActivity()).get(Go4LunchViewModel.class);
                    List<RestaurantDetails> listRestaurants = model.getRestaurantsDetails();

                    for (RestaurantDetails restaurantDetails : listRestaurants){

                        // Declare a Marker for current Restaurant
                        Marker marker = null;

                        // Find out if the restaurant is chosen by a Workmates
                        for (QueryDocumentSnapshot document : querySnapshotTask.getResult()) {

                            if (restaurantDetails.getId().equals(document.get("restaurantIdentifier"))) {
                                Log.d(TAG, "onComplete: GREENNNNN");
                                Log.d(TAG, "onComplete: restaurantDetails.getId() = " + restaurantDetails.getId());
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(restaurantDetails.getLat()),
                                                        Double.parseDouble(restaurantDetails.getLng())
                                                )
                                        )
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_green))
                                        .title(restaurantDetails.getName())
                                );
                                break;
                            }
                        }
                        if (marker == null){
                            // Default Marker
                            Log.d(TAG, "onComplete: ORANGEEEE");
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(restaurantDetails.getLat()),
                                                    Double.parseDouble(restaurantDetails.getLng())
                                            )
                                    )
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_orange))
                                    .title(restaurantDetails.getName())
                            );
                        }
                        marker.setTag(restaurantDetails.getId());
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    // Update Location UI
                    MapViewFragment.this.updateLocationUI();
                } else {
                    Log.d(TAG, "onComplete: Error getting documents : ", querySnapshotTask.getException());
                }
            }
        });
    }
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    public void updateLocationUI() {
        Log.d(TAG, "updateLocationUI: ");
        if (mMap != null) {
            try {
                Go4LunchViewModel model = ViewModelProviders.of(getActivity()).get(Go4LunchViewModel.class);
                Log.d(TAG, "updateLocationUI: isLocationPermissionGranted = "+model.isLocationPermissionGranted());
                if (model.isLocationPermissionGranted()) {
                    Log.d(TAG, "updateLocationUI: Permission Granted");
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                } else {
                    Log.d(TAG, "updateLocationUI: Permission not Granted");
                    mMap.setMyLocationEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            } catch (SecurityException e) {
                Log.e("updateLocationUI %s", e.getMessage());
            }
        }
    }
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
