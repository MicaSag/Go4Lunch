package com.android.sagot.go4lunch.Views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sagot.go4lunch.Models.firestore.User;
import com.android.sagot.go4lunch.R;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListWorkmatesViewHolder extends RecyclerView.ViewHolder {

    // For debug
    private static final String TAG = ListWorkmatesViewHolder.class.getSimpleName();

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.fragment_list_workmates_view_item_card) CardView mCard;
    @BindView(R.id.fragment_list_workmates_view_item_workmate_photo) ImageView mPhoto;
    @BindView(R.id.fragment_list_workmates_view_item_workmate_details) TextView mDetails;

    // View of the item
    View mItemView;

    public ListWorkmatesViewHolder(View itemView) {
        super(itemView);
        Log.d(TAG, "ListViewHolder: ");

        mItemView = itemView;
        // Get & serialise all views
        ButterKnife.bind(this, itemView);
    }

    // Method to update the current item
    public void updateWithParticipantDetails(User user
                                            , RequestManager glide
                                            , String restaurantIdentifier
                                            , Context context){
        Log.d(TAG, "updateWithParticipantDetails: ");

        // display details
        String details;

        // If restaurantIdentifier is null, then this is the activity Welcome that calls
        if ( restaurantIdentifier == "" ) {

            // If the user has not yet chosen a restaurant
            if (user.getRestaurantName() != null) {
                mDetails.setTextAppearance(mItemView.getContext(), R.style.NormalBoldText);
                details = user.getUserName()
                        + context.getString(R.string.workmates_list_text_1)
                        + user.getRestaurantName()
                        + ")";
            } else {
                // Otherwise we will display the name of the restaurant
                mDetails.setTextAppearance(mItemView.getContext(), R.style.NormalItalicText);
                details = user.getUserName()
                        + context.getString(R.string.workmates_list_text_2);
            }
        } else{
            // If restaurantIdentifier is not null, then this is the activity Restaurant Card that calls
            mDetails.setTextAppearance(mItemView.getContext(), R.style.NormalBoldText);
            details = user.getUserName()
                    + context.getString(R.string.workmates_list_text_3);
        }
        mDetails.setText(details);

        glide.load(user.getUrlPicture())
                .apply(RequestOptions.circleCropTransform())
                .into(mPhoto);
    }
}
