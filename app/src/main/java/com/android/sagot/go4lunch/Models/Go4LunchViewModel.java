package com.android.sagot.go4lunch.Models;

import android.arch.lifecycle.ViewModel;

import com.android.sagot.go4lunch.Models.firestore.Restaurant;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  ViewModel of the WelcomeActivity
 */

public class Go4LunchViewModel extends ViewModel {

    // For save the status of the location permission granted
    private boolean mLocationPermissionGranted;

    // For save Restaurants List
    private Map<String,Restaurant> mListRestaurant = new LinkedHashMap<>();

    public boolean isLocationPermissionGranted() {
        return mLocationPermissionGranted;
    }

    public void setLocationPermissionGranted(boolean locationPermissionGranted) {
        mLocationPermissionGranted = locationPermissionGranted;
    }

    public Map<String,Restaurant> getListRestaurant() {
        return mListRestaurant;
    }

    public void setListRestaurant(Map<String,Restaurant> listRestaurant) {
        mListRestaurant = listRestaurant;
    }
}
