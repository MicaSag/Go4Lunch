package com.android.sagot.go4lunch.Controllers.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sagot.go4lunch.Controllers.Base.BaseActivity;
import com.android.sagot.go4lunch.Controllers.Fragments.ListViewFragment;
import com.android.sagot.go4lunch.Models.Go4LunchViewModel;
import com.android.sagot.go4lunch.Models.PlaceDetails;
import com.android.sagot.go4lunch.R;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

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

    // Declare a PlaceDetails
    private PlaceDetails mPlaceDetails;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain PlaceDetails
        getPlaceDetails();

        // Update UI
        updateUI();
    }

    private void getPlaceDetails(){
        Log.d(TAG, "getPlaceDetails: ");

        // Instantiate PlaceDetails Object
        mPlaceDetails = new PlaceDetails();
        // Get back Intent send to parameter by the ListViewFragment
        Intent intent = getIntent();
        mPlaceDetails.setName(intent.getStringExtra(ListViewFragment.KEY_RESTAURANT_NAME));
        mPlaceDetails.setAddress(intent.getStringExtra(ListViewFragment.KEY_RESTAURANT_ADDRESS));
        mPlaceDetails.setNbrStars(intent.getIntExtra(ListViewFragment.KEY_RESTAURANT_NBR_STARS,0));
    }

    private void updateUI(){
        Log.d(TAG, "updateUI: ");

        this.mName.setText(mPlaceDetails.getName());
        this.mAddress.setText(mPlaceDetails.getAddress());

        mStarTwo.setVisibility(View.INVISIBLE);

        //glide.load(mPlaceDetails.getPhotoUrl()).into(this.mImage);

    }
}
