package com.android.sagot.go4lunch.Controllers.Base;

import android.arch.lifecycle.ViewModelProviders;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.sagot.go4lunch.Models.AdapterRestaurant;
import com.android.sagot.go4lunch.Models.Go4LunchViewModel;

import java.util.LinkedHashMap;


public abstract class BaseFragment extends Fragment {

    // Force developer implement those methods
    //protected abstract int getActivity(); // Layout of the Child Activity

    // For Debugging Mode
    private static final String TAG = BaseFragment.class.getSimpleName();


    // ---------------------------------------------------------------------------------------------
    //                                     VIEW MODEL ACCESS
    // ---------------------------------------------------------------------------------------------
    // Get Model
    public Go4LunchViewModel getModel(){
        Log.d(TAG, "getModel: ");

        return ViewModelProviders.of(getActivity()).get(Go4LunchViewModel.class);
    }

    // Get the current Complete restaurant list of the model
    public LinkedHashMap<String,AdapterRestaurant> getCompleteMapAdapterRestaurantOfTheModel(){
        Log.d(TAG, "getCompleteMapAdapterRestaurantOfTheModel: ");

        return getModel().getCompleteMapAdapterRestaurant();
    }

    // Set the current Complete restaurant list of the model
    public void setCompleteMapAdapterRestaurantInModel(LinkedHashMap<String,AdapterRestaurant> completeMapAdapterRestaurant){
        Log.d(TAG, "setCompleteMapAdapterRestaurantInModel: ");

        getModel().setCompleteMapAdapterRestaurant(completeMapAdapterRestaurant);
    }

    // Get the current filtered restaurant list of the model
    public LinkedHashMap<String,AdapterRestaurant> getFilteredMapAdapterRestaurantOfTheModel(){
        Log.d(TAG, "getFilteredMapAdapterRestaurantOfTheModel: ");

        return getModel().getFilteredMapAdapterRestaurant();
    }

    // Set the current filtered restaurant list of the model
    public void setFilteredMapAdapterRestaurantInModel(LinkedHashMap<String,AdapterRestaurant> filteredMapAdapterRestaurant){
        Log.d(TAG, "setFilteredMapAdapterRestaurantInModel: ");

        getModel().setFilteredMapAdapterRestaurant(filteredMapAdapterRestaurant);
    }

    // Get Current Location of the Model
    public Location getCurrentLocationOfTheModel(){
        Log.d(TAG, "getCurrentLocationOfTheModel: ");

        return getModel().getCurrentLocation();
    }
}
