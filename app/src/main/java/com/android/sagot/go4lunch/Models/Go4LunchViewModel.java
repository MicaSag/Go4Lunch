package com.android.sagot.go4lunch.Models;

import android.arch.lifecycle.ViewModel;

import java.util.List;

/**
 *  ViewModel of the WelcomeActivity
 */

public class Go4LunchViewModel extends ViewModel {

    // For save PlacesDetails List
    private List<PlaceDetails> mListPlacesDetails ;

    // for set PlacesDetails List
    public void setListPlaceDetails(List<PlaceDetails> listPlaceDetails) {
        mListPlacesDetails = listPlaceDetails;
    }

    // For get PlacesDetails List
    public List<PlaceDetails> getListPlaceDetails() {
        return mListPlacesDetails;
    }
}
