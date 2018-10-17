package com.android.sagot.go4lunch.Controllers.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sagot.go4lunch.Controllers.Base.BaseActivity;
import com.android.sagot.go4lunch.Controllers.Fragments.ListWorkmatesViewFragment;
import com.android.sagot.go4lunch.Models.RestaurantDetails;
import com.android.sagot.go4lunch.Models.WorkmateDetails;
import com.android.sagot.go4lunch.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**************************************************************************************************
 *
 *  ACTIVITY that displays the Restaurant
 *  -----------------------------------------
 *  IN = Key Caller     : String
 *       Workmates List : List<WorkmateDetails>
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
    // Create the key workmates List
    public static final String KEY_LIST_WORKMATES_RESTAURANT_CARD = "KEY_LIST_WORKMATES_RESTAURANT_CARD";

    // --> Data retrieved from the caller
    // Declare a RestaurantDetails
    private RestaurantDetails mRestaurantDetails;
    // Declare list of WorkmatesDetails
    private List<WorkmateDetails> mWorkmatesDetails;

    // Declaring a Glide object
    private RequestManager mGlide;

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
    // Click on Call Button
    @OnClick(R.id.activity_restaurant_call_button)
    // Ask permission when accessing to this listener
    @AfterPermissionGranted(RC_CALL_PHONE_PERMISSION)
    public void submitCallButton(View view){
        Log.d(TAG, "submitCallButton: ");

        if (!EasyPermissions.hasPermissions(this, PERMISSION_CALL_PHONE)) {
            EasyPermissions.requestPermissions(this, "Permission CALL_PHONE", RC_CALL_PHONE_PERMISSION, PERMISSION_CALL_PHONE);
            return;
        }
        Log.d(TAG, "submitCallButton: Permission GRANTED :-)");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 2 - Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // Click on Like Button
    @OnClick(R.id.activity_restaurant_like_button)
    protected void submitLikeButton(View view){
        Log.d(TAG, "submitLikeButton: ");
    }

    // Click on Web Site Button
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
    //                                       METHODS
    // ---------------------------------------------------------------------------------------------
    /**
     * Method for recovering data sent by the caller
     */
    private void getRestaurantDetails(){
        Log.d(TAG, "getRestaurantDetails: ");

        // Recover intent of the Caller
        Intent intent = getIntent();

        // 1 ==> Retrieves the details of the restaurant sent by Caller
        mRestaurantDetails = new RestaurantDetails();
        String restaurantDetails = intent.getStringExtra(KEY_DETAILS_RESTAURANT_CARD);
        mRestaurantDetails = new Gson().fromJson(restaurantDetails,RestaurantDetails.class);

        // 2 ==> Retrieves the Workmates List who will eat at this restaurant sent by Caller
        Type collectionType = new TypeToken<List<WorkmateDetails>>() {}.getType();
        mWorkmatesDetails = new ArrayList<>();
        String workmatesDetails = intent.getStringExtra(KEY_LIST_WORKMATES_RESTAURANT_CARD);
        mWorkmatesDetails = new Gson().fromJson(workmatesDetails,collectionType);
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
        mGlide = Glide.with(this);
        mGlide.load(mRestaurantDetails.getPhotoUrl()).into(this.mImage);

        // Display Workmates List
        // Use The FragmentManager(Support) for Display the listWorkmatesViewFragment
        ListWorkmatesViewFragment listWorkmatesViewFragment =
                (ListWorkmatesViewFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.activity_restaurant_workmates_list);
        // If no created ListWorkmatesViewFragment
        if (listWorkmatesViewFragment == null) {
            // Create new ListWorkmatesViewFragment and send Workmates List and caller Name in parameters
            String callerName = this.getClass().getSimpleName();
            listWorkmatesViewFragment = ListWorkmatesViewFragment.newInstance(builtWorkmatesListToDisplay(), callerName);
            // Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_restaurant_workmates_list,listWorkmatesViewFragment)
                    .commit();
        }
    }
    /**
     * Method who builds the list of Workmates to display
     * Only the Workmates that eats in this restaurant this afternoon will be displayed
     */
    private List<WorkmateDetails> builtWorkmatesListToDisplay(){
        Log.d(TAG, "builtWorkmatesListToDisplay: ");

        List<WorkmateDetails> workmatesDetails = new ArrayList<>();
        for (WorkmateDetails workmateDetails : mWorkmatesDetails){

            if (workmateDetails.getRestaurantIdentifier()
                    .equals(mRestaurantDetails.getId())) workmatesDetails.add(workmateDetails);
        }
        return workmatesDetails;
    }
}
