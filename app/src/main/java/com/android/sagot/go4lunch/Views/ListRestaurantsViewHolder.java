package com.android.sagot.go4lunch.Views;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.android.sagot.go4lunch.R;
import com.bumptech.glide.RequestManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListRestaurantsViewHolder extends RecyclerView.ViewHolder {

    // For debug
    private static final String TAG = ListRestaurantsViewHolder.class.getSimpleName();

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.fragment_list_restaurant_view_item_card) CardView mCard;
    @BindView(R.id.fragment_list_restaurant_view_item_name) TextView mName;
    @BindView(R.id.fragment_list_restaurant_view_item_distance) TextView mDistance;
    @BindView(R.id.fragment_list_restaurant_view_item_address) TextView mAddress;
    @BindView(R.id.fragment_list_restaurant_view_item_participants_smiley) ImageView mParticipantsSmiley;
    @BindView(R.id.fragment_list_restaurant_view_item_participants) TextView mParticipants;
    @BindView(R.id.fragment_list_restaurant_view_item_opening_hours) TextView mOpeningHours;
    @BindView(R.id.fragment_list_restaurant_view_item_star_one) ImageView mStarOne;
    @BindView(R.id.fragment_list_restaurant_view_item_star_two) ImageView mStarTwo;
    @BindView(R.id.fragment_list_restaurant_view_item_star_three) ImageView mStarThree;
    @BindView(R.id.fragment_list_restaurant_view_item_image) ImageView mImage;


    public ListRestaurantsViewHolder(View itemView) {
        super(itemView);
        Log.d(TAG, "ListViewHolder: ");

        // Get & serialise all views
        ButterKnife.bind(this, itemView);
    }

    // Method to update the current item
    public void updateWithRestaurantDetails(Restaurant restaurant, RequestManager glide) {
        Log.d(TAG, "updateWithPlaceDetails: ");

        this.mName.setText(restaurant.getName());
        this.mAddress.setText(restaurant.getAddress());

        // Display OpeningTime
        this.mOpeningHours.setText(restaurant.getOpeningTime());


        this.mDistance.setText(restaurant.getDistance());

        // Display Number of Participants
        String participants = "(" + restaurant.getNbrParticipants() + ")";
        this.mParticipants.setText(participants);
        // do not show participant information if the number of participants is null
        if (restaurant.getNbrParticipants() == 0) {
            mParticipantsSmiley.setVisibility(View.INVISIBLE);
            mParticipants.setVisibility(View.INVISIBLE);
        } else {
            mParticipantsSmiley.setVisibility(View.VISIBLE);
            mParticipants.setVisibility(View.VISIBLE);
        }

        // Display Stars
        if (restaurant.getNbrLikes() < 3) mStarThree.setVisibility(View.INVISIBLE);
        else mStarThree.setVisibility(View.VISIBLE);
        if (restaurant.getNbrLikes() < 2) mStarTwo.setVisibility(View.INVISIBLE);
        else mStarTwo.setVisibility(View.VISIBLE);
        if (restaurant.getNbrLikes() < 1) mStarOne.setVisibility(View.INVISIBLE);
        else mStarOne.setVisibility(View.VISIBLE);

        // Display restaurant Photo
        if (restaurant.getPhotoUrl() != null) glide.load(restaurant.getPhotoUrl()).into(this.mImage);
    }
}
