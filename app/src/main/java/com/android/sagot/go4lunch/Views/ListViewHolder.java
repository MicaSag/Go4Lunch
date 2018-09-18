package com.android.sagot.go4lunch.Views;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sagot.go4lunch.Models.PlaceDetails;
import com.android.sagot.go4lunch.R;
import com.bumptech.glide.RequestManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListViewHolder extends RecyclerView.ViewHolder {

    // For debug
    private static final String TAG = ListViewHolder.class.getSimpleName();

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.fragment_list_view_item_card) CardView mCard;
    @BindView(R.id.fragment_list_view_item_restaurant_name) TextView mName;
    @BindView(R.id.fragment_list_view_item_restaurant_distance) TextView mDistance;
    @BindView(R.id.fragment_list_view_item_restaurant_address) TextView mAddress;
    @BindView(R.id.fragment_list_view_item_restaurant_participants) TextView mParticipants;
    @BindView(R.id.fragment_list_view_item_restaurant_opening_hours) TextView mOpeningHours;
    @BindView(R.id.fragment_list_view_item_restaurant_stars) TextView mStars;
    @BindView(R.id.fragment_list_view_item_restaurant_image) ImageView mImage;


    public ListViewHolder(View itemView) {
        super(itemView);
        Log.d(TAG, "ListViewHolder: ");
        ButterKnife.bind(this, itemView);
    }

    public void updateWithPlaceDetails(PlaceDetails placeDetails, RequestManager glide){
        Log.d(TAG, "updateWithPlaceDetails: ");

        this.mName.setText(placeDetails.getName());
        this.mAddress.setText(placeDetails.getAddress());
        this.mOpeningHours.setText(placeDetails.getOpeningTime());
        this.mDistance.setText(placeDetails.getDistance());
        this.mParticipants.setText(placeDetails.getNbrParticipants());
        //this.mStars.setText(placeDetails.getNbrStars());

        glide.load(placeDetails.getPhotoUrl()).into(this.mImage);

    }
}
