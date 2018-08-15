package com.android.sagot.go4lunch.Controllers.Base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.ButterKnife;

/**
 * Created by Michaël SAGOT on 15/08/2018.
 */

public abstract class BaseActivity extends AppCompatActivity {

    // Force developer implement those methods
    protected abstract int getActivityLayout(); // Layout of the Child Activity

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

    // --------------
    //    TOOLBAR
    // --------------
    protected void configureToolBar(){
        //Set the toolbar
        //setSupportActionBar(mToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }

    // -------------------
    //  DISPLAY FOR DEBUG
    // -------------------
    protected void displayCriteria() {
        Log.d(TAG, "displayCriteria: Query               = " );
    }
}
