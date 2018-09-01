package com.android.sagot.go4lunch.Controllers.Base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by Michaël SAGOT on 15/08/2018.
 */

public abstract class BaseActivity extends AppCompatActivity {

    // Force developer implement those methods
    protected abstract int getActivityLayout(); // Layout of the Child Activity
    protected abstract View getCoordinatorLayout(); // Layout of the CoordinatorLayout of the Child Activity

    // For debugging Mode
    private static final String TAG = BaseActivity.class.getSimpleName();

    // --------------------
    // ENTRY POINT
    // --------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getActivityLayout());

        // Get & serialise all views
        ButterKnife.bind(this);
    }

    // --------------------
    //         UI
    // --------------------
    // Show Snack Bar with a message
    public void showSnackBar(String message){
        Snackbar.make(getCoordinatorLayout(), message, Snackbar.LENGTH_LONG).show();
    }

    // -------------------
    //  DISPLAY FOR DEBUG
    // -------------------
    protected void displayCriteria() {
        Log.d(TAG, "displayCriteria: Query               = " );
    }
}
