package com.android.sagot.go4lunch.Views;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.sagot.go4lunch.Models.AdapterRestaurant;
import com.android.sagot.go4lunch.R;
import com.bumptech.glide.RequestManager;

import java.util.Map;

public class ListRestaurantsViewAdapter extends RecyclerView.Adapter<ListRestaurantsViewHolder> {

    // For Debug
    private static final String TAG = ListRestaurantsViewAdapter.class.getSimpleName();

    // Declaring a Glide object
    private RequestManager mGlide;

    // Declare Options<User>
    Map<String,AdapterRestaurant> mListAdapterRestaurant;

    // Current Location
    Location mLocation;

    // CONSTRUCTOR
    public ListRestaurantsViewAdapter(Location location, Map<String,AdapterRestaurant> listAdapterRestaurant
                                            , RequestManager glide) {
        Log.d(TAG, "ListRestaurantsViewAdapter: ");
        mLocation = location;
        mListAdapterRestaurant = listAdapterRestaurant;
        mGlide = glide;
    }

    @Override
    public ListRestaurantsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");

        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_list_restaurants_view_item, parent, false);

        return new ListRestaurantsViewHolder(view);
    }

    // UPDATE VIEW HOLDER WITH A DETAILS RESTAURANT
    @Override
    public void onBindViewHolder(ListRestaurantsViewHolder viewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: ");

            viewHolder.updateWithRestaurantDetails(mLocation, mListAdapterRestaurant.
                    get(getRestaurantIdentifier(position)), mGlide);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ");
        Log.d(TAG, "getItemCount: mListRestaurant size() = "+mListAdapterRestaurant.size());
        return mListAdapterRestaurant.size();
    }

    // Returns the Restaurant Identifier of the current position
    public String getRestaurantIdentifier(int position){
        Log.d(TAG, "getRestaurantIdentifier: ");

        int positionInMap = 0;
        String key = "not value";
        for (String keyValue : mListAdapterRestaurant.keySet()){
            if (position == positionInMap) {key = keyValue;break;}
            positionInMap++;
        }
        return key;
    }




}
