package com.android.sagot.go4lunch.Models;

import android.arch.lifecycle.ViewModel;
import android.location.Location;

import com.android.sagot.go4lunch.Models.firestore.Restaurant;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  ViewModel of the WelcomeActivity
 */

public class Go4LunchViewModel extends ViewModel {

    // Current Location
    private Location mCurrentLocation;
    // For save the status of the location permission granted
    private boolean mLocationPermissionGranted;
    // For save Restaurants List
    private Map<String,Restaurant> mListRestaurant = new LinkedHashMap<>();



    //--- GETTER
    public boolean isLocationPermissionGranted() {
        return mLocationPermissionGranted;
    }

    public void setLocationPermissionGranted(boolean locationPermissionGranted) {
        mLocationPermissionGranted = locationPermissionGranted;
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public Map<String,Restaurant> getListRestaurant() {
        return mListRestaurant;
    }



    //--- SETTER
    public void setListRestaurant(Map<String,Restaurant> listRestaurant) {
        mListRestaurant = listRestaurant;
    }

    public void setCurrentLocation(Location mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }
}
