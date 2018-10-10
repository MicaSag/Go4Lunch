package com.android.sagot.go4lunch.Views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.sagot.go4lunch.Models.RestaurantDetails;
import com.android.sagot.go4lunch.R;
import com.bumptech.glide.RequestManager;

import java.util.List;

public class ListRestaurantsViewAdapter extends RecyclerView.Adapter<ListRestaurantsViewHolder> {

    // FOR DATA
    private List<RestaurantDetails> mListRestaurantsDetails;

    // Declaring a Glide object
    private RequestManager mGlide;

    // CONSTRUCTOR
    public ListRestaurantsViewAdapter(List<RestaurantDetails> listRestaurantsDetails, RequestManager glide) {
       mListRestaurantsDetails = listRestaurantsDetails;
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

    // UPDATE VIEW HOLDER WITH A DETAILS PLACE
    @Override
    public void onBindViewHolder(ListRestaurantsViewHolder viewHolder, int position) {
        viewHolder.updateWithPlaceDetails(mListRestaurantsDetails.get(position), this.mGlide);
    }

    // RETURN THE TOTAL COUNT OF ITEMS IN THE LIST
    @Override
    public int getItemCount() {
        return mListRestaurantsDetails.size();
    }

    // Returns the current position
    public RestaurantDetails getRestaurantDetails(int position){
        return this.mListRestaurantsDetails.get(position);
    }
}
