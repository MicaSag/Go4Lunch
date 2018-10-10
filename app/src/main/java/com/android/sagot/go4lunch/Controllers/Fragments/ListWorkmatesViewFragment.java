package com.android.sagot.go4lunch.Controllers.Fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Location;
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
import com.android.sagot.go4lunch.Models.WorkmateDetails;
import com.android.sagot.go4lunch.Models.RestaurantDetails;
import com.android.sagot.go4lunch.R;
import com.android.sagot.go4lunch.Utils.ItemClickSupport;
import com.android.sagot.go4lunch.Views.ListWorkmatesViewAdapter;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListWorkmatesViewFragment extends Fragment {

    // FOR TRACES
    private static final String TAG = ListWorkmatesViewFragment.class.getSimpleName();

    // Parameter for the construction of the fragment
    public static final String KEY_LIST_WORKMATES = "KEY_LIST_WORKMATES";

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.fragment_list_workmates_view_recycler_view) RecyclerView mRecyclerView;

    // View of the Fragment
    private View mListView;

    // Declare list of WorkmatesDetails & his Adapter
    private List<WorkmateDetails> mWorkmatesDetails;
    private ListWorkmatesViewAdapter mAdapter;

    public ListWorkmatesViewFragment() {
        // Required empty public constructor
    }

    public static ListWorkmatesViewFragment newInstance(List<WorkmateDetails> workmatesDetails) {
        Log.d(TAG, "newInstance: ");

        for (WorkmateDetails workmatesD : workmatesDetails) {
            Log.d(TAG, "newInstance: workmatesDetails = "+workmatesD.getName());
        }

        // Create new fragment
        ListWorkmatesViewFragment listWorkmatesViewFragment = new ListWorkmatesViewFragment();

        // Create bundle and add it some data
        Bundle args = new Bundle();
        final Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
        String json = gson.toJson(workmatesDetails);
        args.putString(KEY_LIST_WORKMATES, json);

        listWorkmatesViewFragment.setArguments(args);

        return listWorkmatesViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        // Inflate the layout for this fragment
        mListView = inflater.inflate(R.layout.fragment_list_workmates_view, container, false);

        // Telling ButterKnife to bind all views in layout
        ButterKnife.bind(this, mListView);

        // Get data from Bundle (created in method newInstance)
        // Restoring the Date with a Gson Object
        Type collectionType = new TypeToken<List<WorkmateDetails>>() {}.getType();
        mWorkmatesDetails = new ArrayList<>();
        mWorkmatesDetails = new Gson().fromJson(getArguments().getString(KEY_LIST_WORKMATES, ""),collectionType);

        for (WorkmateDetails workmatesD : mWorkmatesDetails) {
            Log.d(TAG, "newInstance: mWorkmatesDetails = "+workmatesD.getName());
        }

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

        // Create adapter passing the list of RestaurantDetails
        this.mAdapter = new ListWorkmatesViewAdapter(mWorkmatesDetails, Glide.with(this));
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
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_list_workmates_view_item)
                .setOnItemClickListener((recyclerView, position, v) -> {

                    //Launch Restaurant Card Activity with placeDetails
                    startRestaurantCardActivity(mAdapter.getRestaurantIdentifier(position));
                });
    }

    // --------------------
    //   CALL ACTIVITY
    // -------------------
    private void startRestaurantCardActivity(String restaurantIdentifier){
        Log.d(TAG, "startRestaurantCardActivity: ");

        // Create a intent for call RestaurantCardActivity
        Intent intent = new Intent(getActivity(), RestaurantCardActivity.class);

        Go4LunchViewModel model = ViewModelProviders.of(getActivity()).get(Go4LunchViewModel.class);

        final Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
        String json;

        for (RestaurantDetails restaurantDetails :  model.getRestaurantsDetails()){
            Log.d(TAG, "startRestaurantCardActivity: restaurantIdentifier      = "+restaurantIdentifier);
            Log.d(TAG, "startRestaurantCardActivity: restaurantDetails.getId() = "+restaurantDetails.getId());
            if (restaurantIdentifier.equals(restaurantDetails.getId())) {

                // Create à KEY_RESTAURANT_CARD String with a Gson Object
                json = gson.toJson(restaurantDetails);

                // Sends the position of the selected item
                intent.putExtra(RestaurantCardActivity.KEY_DETAILS_RESTAURANT_CARD, json);

                // Go out as soon as the restaurant details are found
                break;
            }
        }

        for (WorkmateDetails workmatesD : model.getWorkmatesDetails()) {
            Log.d(TAG, "startRestaurantCardActivity: workmatesDetails = "+workmatesD.getName());
        }

        json = gson.toJson(mWorkmatesDetails);
        // Sends the workmates list
        intent.putExtra(RestaurantCardActivity.KEY_LIST_WORKMATES_RESTAURANT_CARD, json);

        // Call RestaurantCardActivity
        startActivity(intent);
    }
}
