package com.android.sagot.go4lunch.Controllers.Base;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.sagot.go4lunch.Models.AdapterRestaurant;
import com.android.sagot.go4lunch.Models.Go4LunchViewModel;
import com.android.sagot.go4lunch.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedHashMap;

import butterknife.ButterKnife;

import static com.android.sagot.go4lunch.Utils.Toolbox.isNetworkAvailable;

/**
 * Created by Michaël SAGOT on 15/08/2018.
 */

public abstract class BaseActivity extends AppCompatActivity {

    // Force developer implement those methods
    protected abstract int getActivityLayout(); // Layout of the Child Activity
    protected abstract View getCoordinatorLayout(); // Layout of the CoordinatorLayout of the Child Activity

    // For debugging Mode
    private static final String TAG = BaseActivity.class.getSimpleName();

    // ---------------------------------------------------------------------------------------------
    //                                        ENTRY POINT
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getActivityLayout());

        // Get & serialise all views
        ButterKnife.bind(this);
    }

    // ---------------------------------------------------------------------------------------------
    //                                     VIEW MODEL ACCESS
    // ---------------------------------------------------------------------------------------------
    // Get Model
    public Go4LunchViewModel getModel(){
        Log.d(TAG, "getModel: ");

        return ViewModelProviders.of(this).get(Go4LunchViewModel.class);
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

    // ---------------------------------------------------------------------------------------------
    //                                             UI
    // ---------------------------------------------------------------------------------------------
    // Show Snack Bar with a message
    public void showSnackBar(String message){
        Log.d(TAG, "showSnackBar: ");

        Snackbar.make(getCoordinatorLayout(), message, Snackbar.LENGTH_LONG).show();
    }

    // ---------------------------------------------------------------------------------------------
    //                                        PERMISSIONS
    // ---------------------------------------------------------------------------------------------
    // Return Current User
    protected FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // Check if current user is logged in
    protected Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }

    // ---------------------------------------------------------------------------------------------
    //                                       ERROR HANDLER
    // ---------------------------------------------------------------------------------------------
    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message;
                if (isNetworkAvailable(getApplicationContext()))
                     message = getString(R.string.common_not_network);
                else message = getString(R.string.error_unknown_error);
                showSnackBar(message);
            }
        };
    }
}
