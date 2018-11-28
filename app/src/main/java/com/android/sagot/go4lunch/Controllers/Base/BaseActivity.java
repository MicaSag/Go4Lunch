package com.android.sagot.go4lunch.Controllers.Base;


import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.sagot.go4lunch.Models.Go4LunchViewModel;
import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.android.sagot.go4lunch.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

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

    // For use CALL_PHONE permission
    // 1 _ Permission name
    public static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;
    // 2 _ Request Code
    public static final int RC_CALL_PHONE_PERMISSION = 1;


    // For use LOCATION permissions
    // 1 _ Permissions name
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    // 2 _ Requests Code
    public static final int RC_ACCESS_COARSE_LOCATION_PERMISSION = 2;
    public static final int RC_ACCESS_FINE_LOCATION_PERMISSION = 3;


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
    //                                        VIEW MODEL ACCESS
    // ---------------------------------------------------------------------------------------------
    // Save current restaurant list in Model
    public void saveRestaurantMapInModel(Map<String,Restaurant> listRestaurant){
        Log.d(TAG, "saveRestaurantMapInModel: ");

        Go4LunchViewModel model = ViewModelProviders.of(this).get(Go4LunchViewModel.class);
        model.setListRestaurant(listRestaurant);
    }

    // Get current restaurant list of the Model
    public Map<String,Restaurant> getRestaurantMapOfTheModel(){
        Log.d(TAG, "getRestaurantMapOfTheModel: ");

        Go4LunchViewModel model = ViewModelProviders.of(this).get(Go4LunchViewModel.class);

        return model.getListRestaurant();
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
    /**
     * Check the permission corresponding to the 'requestCode' parameter
     * If they aren't allowed, prompts the user for permission to use the request.
     * The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    /*rotected void getPermission(int requestCode) {
        Log.d(TAG, "getCallPhonePermission: ");

        // Request for unnecessary permission before version Android 6.0 (API level 23)
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            // Check if permissions are already authorized
            switch (requestCode) {
                // Case of permission CALL_PHONE
                case CALL_PHONE_PERMISSION_REQUEST_CODE: {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        // Permissions Granted
                        Log.d(TAG, "getCallPhonePermission: Permission already granted by User");
                        mCallPhonePermissionGranted = true;
                    } else {
                        // Permissions not Granted
                        Log.d(TAG, ">>-- Ask the user for Location Permission --<<");
                        requestPermissions(
                                new String[]{Manifest.permission.CALL_PHONE},
                                CALL_PHONE_PERMISSION_REQUEST_CODE);
                    }
                }
            }
        }
    }*/
    /**
     * Method that processes the response to a request for permission made
     * by the function "requestPermissions(..)"
     */
    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");

        switch (requestCode) {
            case CALL_PHONE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permissions Granted
                    // Get the last know location of the phone
                    Log.d(TAG, "onRequestPermissionsResult: Permission Granted by User :-)");
                    mCallPhonePermissionGranted = true;
                }
                else{
                    Log.d(TAG, "onRequestPermissionsResult: Permission not Granted by User :-(");
                    mCallPhonePermissionGranted = false;
                }
            }
        }
    }*/

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
