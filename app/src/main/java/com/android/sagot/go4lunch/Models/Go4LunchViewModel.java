package com.android.sagot.go4lunch.Models;

import android.arch.lifecycle.ViewModel;

import java.util.List;

public class Go4LunchViewModel extends ViewModel {

    private List<PlaceDetails> mListPlacesDetails ;

    public void setListPlaceDetails(List<PlaceDetails> listPlaceDetails) {
        mListPlacesDetails = listPlaceDetails;
    }

    public List<PlaceDetails> getListPlaceDetails() {
        return mListPlacesDetails;
    }
}
