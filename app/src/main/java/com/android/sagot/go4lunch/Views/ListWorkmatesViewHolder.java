package com.android.sagot.go4lunch.Views;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sagot.go4lunch.Models.firestore.User;
import com.android.sagot.go4lunch.R;
import com.bumptech.glide.RequestManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListWorkmatesViewHolder extends RecyclerView.ViewHolder {

    // For debug
    private static final String TAG = ListWorkmatesViewHolder.class.getSimpleName();

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.fragment_list_workmates_view_item_card) CardView mCard;
    @BindView(R.id.fragment_list_workmates_view_item_workmate_photo) ImageView mPhoto;
    @BindView(R.id.fragment_list_workmates_view_item_workmate_details) TextView mDetails;


    public ListWorkmatesViewHolder(View itemView) {
        super(itemView);
        Log.d(TAG, "ListViewHolder: ");

        // Get & serialise all views
        ButterKnife.bind(this, itemView);
    }

    // Method to update the current item
    public void updateWithParticipantDetails(User user, RequestManager glide){
        Log.d(TAG, "updateWithParticipantDescription: ");

        // display details
        String details;
        if (user.getRestaurantName() != null) {
            details = user.getUserName()
                    + " is eating at ("
                    + user.getRestaurantName()
                    + ")";
        } else {
            details = user.getUserName()
                    + " hasn't decided yet";
        }
        this.mDetails.setText(details);

        glide.load(user.getUrlPicture()).into(this.mPhoto);

    }
}
