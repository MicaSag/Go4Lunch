package com.android.sagot.go4lunch.Models;

import android.arch.lifecycle.ViewModel;

import com.android.sagot.go4lunch.Models.firestore.Restaurant;

import java.util.List;

/**
 *  ViewModel of the WelcomeActivity
 */

public class Go4LunchViewModel extends ViewModel {

    // For save the status of the location permission granted
    private boolean mLocationPermissionGranted;

    // For save PlacesDetails List
    private List<Restaurant> mListRestaurants ;

    public boolean isLocationPermissionGranted() {
        return mLocationPermissionGranted;
    }

    public void setLocationPermissionGranted(boolean locationPermissionGranted) {
        mLocationPermissionGranted = locationPermissionGranted;
    }

    public List<Restaurant> getRestaurants() {
        return mListRestaurants;
    }

    public void setRestaurants(List<Restaurant> listRestaurants) {
        mListRestaurants = listRestaurants;
    }
}
