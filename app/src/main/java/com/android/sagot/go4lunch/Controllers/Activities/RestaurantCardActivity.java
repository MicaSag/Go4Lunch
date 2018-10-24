package com.android.sagot.go4lunch.Controllers.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sagot.go4lunch.Controllers.Base.BaseActivity;
import com.android.sagot.go4lunch.Controllers.Fragments.ListRestaurantsViewFragment;
import com.android.sagot.go4lunch.Controllers.Fragments.ListWorkmatesViewFragment;
import com.android.sagot.go4lunch.Models.RestaurantDetails;
import com.android.sagot.go4lunch.R;
import com.android.sagot.go4lunch.api.UserHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**************************************************************************************************
 *
 *  ACTIVITY that displays the Restaurant
 *  -----------------------------------------
 *  IN = Restaurant Details     : RestaurantDetails
 *
 **************************************************************************************************/

public class RestaurantCardActivity extends BaseActivity {

    // For debugging Mode
    private static final String TAG = RestaurantCardActivity.class.getSimpleName();

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.activity_restaurant_coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.activity_restaurant_card_name) TextView mName;
    @BindView(R.id.activity_restaurant_card_address) TextView mAddress;
    @BindView(R.id.activity_restaurant_card_star_one) ImageView mStarOne;
    @BindView(R.id.activity_restaurant_card_star_two) ImageView mStarTwo;
    @BindView(R.id.activity_restaurant_card_star_three) ImageView mStarThree;
    @BindView(R.id.activity_restaurant_card_image) ImageView mImage;
    @BindView(R.id.activity_restaurant_web_site_button) ImageButton mWebSiteButton;
    @BindView(R.id.activity_restaurant_web_site_text) TextView mWebSiteText;

    // Create the key details restaurant
    public static final String KEY_DETAILS_RESTAURANT_CARD = "KEY_DETAILS_RESTAURANT_CARD";

    // --> Data retrieved from the caller
    // Declare a RestaurantDetails
    private RestaurantDetails mRestaurantDetails;

    // Declaring a Glide object
    private RequestManager mGlide;

    // Declare ListRestaurantsView FRAGMENT
    private ListWorkmatesViewFragment mListWorkmatesViewFragment;

    // ---------------------------------------------------------------------------------------------
    //                                DECLARATION BASE METHODS
    // ---------------------------------------------------------------------------------------------
    // BASE METHOD Implementation
    // Get the activity layout
    // CALLED BY BASE METHOD 'onCreate(...)'
    @Override
    protected int getActivityLayout() {
        return R.layout.activity_restaurant_card;
    }

    // BASE METHOD Implementation
    // Get the coordinator layout
    // CALLED BY BASE METHOD
    @Override
    protected View getCoordinatorLayout() {
        return mCoordinatorLayout;
    }
    // ---------------------------------------------------------------------------------------------
    //                                    ENTRY POINT
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain RestaurantDetails
        getRestaurantDetails();

        // Update UI
        updateUI();
    }
    // ---------------------------------------------------------------------------------------------
    //                                       ACTIONS
    // ---------------------------------------------------------------------------------------------
    // CALL Button
    //**************
    @OnClick(R.id.activity_restaurant_call_button)
    // Ask permission when accessing to this listener
    @AfterPermissionGranted(RC_CALL_PHONE_PERMISSION)
    public void submitCallButton(View view){
        Log.d(TAG, "submitCallButton: ");

        if (!EasyPermissions.hasPermissions(this, PERMISSION_CALL_PHONE)) {
            EasyPermissions.requestPermissions(this, "Permission CALL_PHONE", RC_CALL_PHONE_PERMISSION, PERMISSION_CALL_PHONE);
            return;
        }
        try {
            Log.d(TAG, "submitCallButton: Permission GRANTED :-)");
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:0616176149"));
            startActivity(callIntent);
        }catch (SecurityException e){
            Log.d(TAG, "submitCallButton: EXCEPTION ALERTE ALERTE ALERTE");
        }
    }

    //**************
    // LIKE Button
    //**************
    @OnClick(R.id.activity_restaurant_like_button)
    protected void submitLikeButton(View view){
        Log.d(TAG, "submitLikeButton: ");

        UserHelper.updateRestaurantIdentifier(getCurrentUser().getUid(),mRestaurantDetails.getId());
        UserHelper.updateRestaurantName(getCurrentUser().getUid(),mRestaurantDetails.getName());
        mListWorkmatesViewFragment.updateUI();
    }

    //*****************
    // WEB SITE Button
    //*****************
    @OnClick(R.id.activity_restaurant_web_site_button)
    protected void submitWebSiteButton(View view){
        Log.d(TAG, "submitWebSiteButton: ");

        // Launch WebViewActivity
        // Param : Url to display
        Intent myIntent = new Intent(this, WebViewActivity.class);
        myIntent.putExtra(WebViewActivity.KEY_RESTAURANT_WEB_SITE_URL,mRestaurantDetails.getWebSiteUrl());
        this.startActivity(myIntent);
    }
    // ---------------------------------------------------------------------------------------------
    //                                   REQUEST PERMISSION
    // ---------------------------------------------------------------------------------------------
    /**
     * Method to ask the user for permission to make calls
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    // ---------------------------------------------------------------------------------------------
    //                                       METHODS
    // ---------------------------------------------------------------------------------------------
    /**
     * Method for recovering data sent by the caller
     */
    private void getRestaurantDetails(){
        Log.d(TAG, "getRestaurantDetails: ");

        // Recover intent of the Caller
        Intent intent = getIntent();

        // ==> Retrieves the details of the restaurant sent by Caller
        mRestaurantDetails = new RestaurantDetails();
        String restaurantDetails = intent.getStringExtra(KEY_DETAILS_RESTAURANT_CARD);
        mRestaurantDetails = new Gson().fromJson(restaurantDetails,RestaurantDetails.class);
    }
    /**
     * Method for updating the UI
     */
    private void updateUI(){
        Log.d(TAG, "updateUI: ");

        // Display Name of the restaurant
        this.mName.setText(mRestaurantDetails.getName());
        // Display Address of the Restaurant
        this.mAddress.setText(mRestaurantDetails.getAddress());

        // Display Stars
        if (mRestaurantDetails.getNbrStars() < 3 ) mStarThree.setVisibility(View.INVISIBLE);
        if (mRestaurantDetails.getNbrStars() < 2 ) mStarTwo.setVisibility(View.INVISIBLE);
        if (mRestaurantDetails.getNbrStars() < 1 ) mStarOne.setVisibility(View.INVISIBLE);

        // Display WebSite Button
        if (mRestaurantDetails.getWebSiteUrl() == null) {
            mWebSiteButton.setVisibility(View.INVISIBLE);
            mWebSiteText.setVisibility(View.INVISIBLE);
        }

        // Display Photo of the restaurant
        if (mRestaurantDetails.getPhotoUrl() != null) {
            mGlide = Glide.with(this);
            mGlide.load(mRestaurantDetails.getPhotoUrl()).into(this.mImage);
        }

        // Display Workmates List
        // Use The FragmentManager(Support) for Display the listWorkmatesViewFragment
        mListWorkmatesViewFragment = (ListWorkmatesViewFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.activity_restaurant_workmates_list);
        // If no created ListWorkmatesViewFragment
        if (mListWorkmatesViewFragment == null) {
            // Create new ListWorkmatesViewFragment and send the restaurant identifier in parameter
            String restaurantIdentifier = mRestaurantDetails.getId();
            mListWorkmatesViewFragment = ListWorkmatesViewFragment.newInstance(restaurantIdentifier);
            // Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_restaurant_workmates_list,mListWorkmatesViewFragment)
                    .commit();
        }
    }
}
