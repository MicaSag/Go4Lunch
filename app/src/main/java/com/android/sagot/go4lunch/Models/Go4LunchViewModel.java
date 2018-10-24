package com.android.sagot.go4lunch.Models;

import android.arch.lifecycle.ViewModel;

import java.util.List;

/**
 *  ViewModel of the WelcomeActivity
 */

public class Go4LunchViewModel extends ViewModel {

    // For save the status of the location permission granted
    private boolean mLocationPermissionGranted;

    // For save PlacesDetails List
    private List<RestaurantDetails> mRestaurantsDetails ;

    public boolean isLocationPermissionGranted() {
        return mLocationPermissionGranted;
    }

    public void setLocationPermissionGranted(boolean locationPermissionGranted) {
        mLocationPermissionGranted = locationPermissionGranted;
    }

    public List<RestaurantDetails> getRestaurantsDetails() {
        return mRestaurantsDetails;
    }

    public void setRestaurantsDetails(List<RestaurantDetails> restaurantsDetails) {
        mRestaurantsDetails = restaurantsDetails;
    }
}
