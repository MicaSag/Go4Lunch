package com.android.sagot.go4lunch.Controllers.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sagot.go4lunch.Controllers.Base.BaseActivity;
import com.android.sagot.go4lunch.Controllers.Fragments.ListRestaurantViewFragment;
import com.android.sagot.go4lunch.Models.RestaurantDetails;
import com.android.sagot.go4lunch.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import butterknife.BindView;
import butterknife.OnClick;

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

    // Declare a RestaurantDetails
    private RestaurantDetails mPlaceDetails;

    // Declaring a Glide object
    private RequestManager mGlide;

    // -------------------------
    // DECLARATION BASE METHODS
    // -------------------------
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

    // --------------------
    //     ENTRY POINT
    // --------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain RestaurantDetails
        getPlaceDetails();

        // Update UI
        updateUI();
    }

    // -----------------------------
    //        ACTION BUTTONS
    // -----------------------------
    // Click on Call Button
    @OnClick(R.id.activity_restaurant_call_button)
    protected void submitCallButton(View view){
        Log.d(TAG, "submitCallButton: ");
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
    }

    /**
     * Method for recovering data sent by the caller
     */
    private void getPlaceDetails(){
        Log.d(TAG, "getPlaceDetails: ");

        // Instantiate RestaurantDetails Object
        mPlaceDetails = new RestaurantDetails();
        // Get back Intent send to parameter by the ListViewFragment
        Intent intent = getIntent();
        mPlaceDetails.setName(intent.getStringExtra(ListRestaurantViewFragment.KEY_RESTAURANT_NAME));
        mPlaceDetails.setAddress(intent.getStringExtra(ListRestaurantViewFragment.KEY_RESTAURANT_ADDRESS));
        mPlaceDetails.setNbrStars(intent.getIntExtra(ListRestaurantViewFragment.KEY_RESTAURANT_NBR_STARS,0));
        mPlaceDetails.setPhotoUrl(intent.getStringExtra(ListRestaurantViewFragment.KEY_RESTAURANT_PHOTO_URL));
        mPlaceDetails.setWebSiteUrl(intent.getStringExtra(ListRestaurantViewFragment.KEY_RESTAURANT_WEB_SITE_URL));
    }

    /**
     * Method for updating the UI
     */
    private void updateUI(){
        Log.d(TAG, "updateUI: ");

        this.mName.setText(mPlaceDetails.getName());
        this.mAddress.setText(mPlaceDetails.getAddress());

        // Display Stars
        if (mPlaceDetails.getNbrStars() < 3 ) mStarThree.setVisibility(View.INVISIBLE);
        if (mPlaceDetails.getNbrStars() < 2 ) mStarTwo.setVisibility(View.INVISIBLE);
        if (mPlaceDetails.getNbrStars() < 1 ) mStarOne.setVisibility(View.INVISIBLE);

        // Display Photo of the restaurant
        mGlide = Glide.with(this);
        mGlide.load(mPlaceDetails.getPhotoUrl()).into(this.mImage);
    }
}
