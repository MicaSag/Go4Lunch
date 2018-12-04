package com.android.sagot.go4lunch.Models;

import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.location.Location;

import com.android.sagot.go4lunch.Models.firestore.Restaurant;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  ViewModel of the WelcomeActivity
 */

public class Go4LunchViewModel extends ViewModel {

    // Defined Preferences of the application
    SharedPreferences mSharedPreferences;
    // Model data saved in the SharedPreferences
    private SavedPreferences mSavedPreferences;

    // Current Location
    private Location mCurrentLocation;
    // For save the status of the location permission granted
    private boolean mLocationPermissionGranted;
    // For save Restaurants List
    private Map<String,Restaurant> mListRestaurant = new LinkedHashMap<>();

    //--- GETTER
    public SavedPreferences getSavedPreferences() {
        return mSavedPreferences;
    }

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

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    //--- SETTER
    public void setSavedPreferences(SavedPreferences mSavedPreferences) {
        this.mSavedPreferences = mSavedPreferences;
    }

    public void setListRestaurant(Map<String,Restaurant> listRestaurant) {
        mListRestaurant = listRestaurant;
    }

    public void setCurrentLocation(Location mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }

    public void setSharedPreferences(SharedPreferences mSharedPreferences) {
        this.mSharedPreferences = mSharedPreferences;
    }
}
