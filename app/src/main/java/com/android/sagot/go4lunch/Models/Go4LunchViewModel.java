package com.android.sagot.go4lunch.Models;

import android.arch.lifecycle.ViewModel;
import android.location.Location;

import java.util.LinkedHashMap;

/**
 *  ViewModel of the WelcomeActivity
 */

public class Go4LunchViewModel extends ViewModel {

    // Current Location
    private Location mCurrentLocation;
    // For save the status of the location permission granted
    private boolean mLocationPermissionGranted;
    // For save the list of restaurants found
    private LinkedHashMap<String,AdapterRestaurant> mCompleteMapAdapterRestaurant = new LinkedHashMap<>();

    // For save the list of restaurants found
    // and allow its management in the application ( sorting, filter )
    private LinkedHashMap<String,AdapterRestaurant> mFilteredMapAdapterRestaurant = new LinkedHashMap<>();

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

    public LinkedHashMap<String,AdapterRestaurant> getCompleteMapAdapterRestaurant() {
        return mCompleteMapAdapterRestaurant;
    }

    public LinkedHashMap<String, AdapterRestaurant> getFilteredMapAdapterRestaurant() {
        return mFilteredMapAdapterRestaurant;
    }

    //--- SETTER
    public void setCurrentLocation(Location mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }

    public void setCompleteMapAdapterRestaurant(LinkedHashMap<String, AdapterRestaurant> mCompleteMapAdapterRestaurant) {
        this.mCompleteMapAdapterRestaurant = mCompleteMapAdapterRestaurant;
    }

    public void setFilteredMapAdapterRestaurant(LinkedHashMap<String, AdapterRestaurant> mFilteredMapAdapterRestaurant) {
        this.mFilteredMapAdapterRestaurant = mFilteredMapAdapterRestaurant;
    }
}
