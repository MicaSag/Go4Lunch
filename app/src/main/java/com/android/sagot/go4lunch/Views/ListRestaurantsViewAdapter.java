package com.android.sagot.go4lunch.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.android.sagot.go4lunch.R;
import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import io.reactivex.annotations.NonNull;

public class ListRestaurantsViewAdapter extends FirestoreRecyclerAdapter<Restaurant, ListRestaurantsViewHolder> {

    // Declaring a Glide object
    private RequestManager mGlide;

    // Declare Options<User>
    FirestoreRecyclerOptions<Restaurant> mOptions;

    // CONSTRUCTOR
    public ListRestaurantsViewAdapter(@NonNull FirestoreRecyclerOptions<Restaurant> options
                                            , RequestManager glide) {
        super(options);
        mOptions = options;
        mGlide = glide;
    }

    @Override
    public ListRestaurantsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_list_restaurants_view_item, parent, false);

        return new ListRestaurantsViewHolder(view);
    }

    // UPDATE VIEW HOLDER WITH A DETAILS RESTAURANT
    @Override
    public void onBindViewHolder(@NonNull ListRestaurantsViewHolder viewHolder, int position,
                                 @NonNull Restaurant restaurant) {
        viewHolder.updateWithRestaurantDetails(restaurant, mGlide);
    }

    // Returns the Restaurant Identifier of the current position
    public String getRestaurantIdentifier(int position){
        return mOptions.getSnapshots().get(position).getIdentifier();
    }
}
