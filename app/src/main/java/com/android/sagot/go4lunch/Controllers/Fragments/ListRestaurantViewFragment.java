package com.android.sagot.go4lunch.Controllers.Fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.sagot.go4lunch.Controllers.Activities.RestaurantCardActivity;
import com.android.sagot.go4lunch.Models.Go4LunchViewModel;
import com.android.sagot.go4lunch.Models.PlaceDetails;
import com.android.sagot.go4lunch.R;
import com.android.sagot.go4lunch.Utils.ItemClickSupport;
import com.android.sagot.go4lunch.Views.ListRestaurantViewAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListRestaurantViewFragment extends Fragment {

    // FOR TRACES
    private static final String TAG = ListRestaurantViewFragment.class.getSimpleName();

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.fragment_list_view_recycler_view) RecyclerView mRecyclerView;

    // View of the Fragment
    private View mListView;

    // Declare list of PlaceDetails & Adapter
    private List<PlaceDetails> mPlacesDetails;
    private ListRestaurantViewAdapter mAdapter;

    // Create the key place details
    public static final String KEY_RESTAURANT_NAME = "RESTAURANT_NAME";
    public static final String KEY_RESTAURANT_ADDRESS = "RESTAURANT_ADDRESS";
    public static final String KEY_RESTAURANT_NBR_STARS = "RESTAURANT_NBR_STARS";
    public static final String KEY_RESTAURANT_PHOTO_URL = "RESTAURANT_PHOTO_URL";
    public static final String KEY_RESTAURANT_WEB_SITE_URL = "RESTAURANT_WEB_SITE_URL";

    public ListRestaurantViewFragment() {
        Log.d(TAG, "ListViewFragment: ");
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        mListView = inflater.inflate(R.layout.fragment_list_restaurant_view, container, false);

        // Telling ButterKnife to bind all views in layout
        ButterKnife.bind(this, mListView);

        // Configure RecyclerView
        this.configureRecyclerView();

        // Calling the method that configuring click on RecyclerView
        this.configureOnClickRecyclerView();

        return mListView;
    }

    // -----------------
    // CONFIGURATION
    // -----------------
    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView(){
        Log.d(TAG, "configureRecyclerView: ");

        mPlacesDetails = new ArrayList<>();

        // Create adapter passing the list of PlaceDetails
        this.mAdapter = new ListRestaurantViewAdapter(mPlacesDetails, Glide.with(this));
        // Attach the adapter to the recycler view to populate items
        this.mRecyclerView.setAdapter(this.mAdapter);
        // Set layout manager to position the items
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // -----------------
    //     ACTIONS
    // -----------------
    //  Configure clickListener on Item of the RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_list_restaurant_view_item)
                .setOnItemClickListener((recyclerView, position, v) -> {

                    //Launch Restaurant Card Activity with placeDetails
                    startRestaurantCardActivity(mAdapter.getPlaceDetails(position));
                    });
    }

    // --------------------
    //   CALL ACTIVITY
    // -------------------
    private void startRestaurantCardActivity(PlaceDetails placeDetails){
        Log.d(TAG, "startRestaurantCardActivity: ");

        // Create a intent for call RestaurantCardActivity
        Intent intent = new Intent(getActivity(), RestaurantCardActivity.class);

        // Sends the position of the selected item
        intent.putExtra(KEY_RESTAURANT_NAME, placeDetails.getName());
        intent.putExtra(KEY_RESTAURANT_ADDRESS, placeDetails.getAddress());
        intent.putExtra(KEY_RESTAURANT_NBR_STARS, placeDetails.getNbrStars());
        intent.putExtra(KEY_RESTAURANT_PHOTO_URL, placeDetails.getPhotoUrl());

        // Call RestaurantCardActivity
        startActivity(intent);
    }

    // --------------------
    //         UI
    // -------------------
    public void updateUI() {
        Log.d(TAG, "updateUI: ");

        // Load the ViewModel with the list of PlaceDetails
        Go4LunchViewModel model = ViewModelProviders.of(getActivity()).get(Go4LunchViewModel.class);
        mPlacesDetails.addAll(model.getListPlaceDetails());

        // For Debugging : display name of the restaurants
        for (PlaceDetails placeDetails : mPlacesDetails){
            Log.d(TAG, "configureRecyclerView: placeDetails Name = "+placeDetails.getName());
        }

        // Notify the change to the adapter
        mAdapter.notifyDataSetChanged();
    }
}
