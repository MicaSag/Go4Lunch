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
import com.android.sagot.go4lunch.Models.RestaurantDetails;
import com.android.sagot.go4lunch.Models.WorkmateDetails;
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

/**************************************************************************************************
 *
 *  FRAGMENT that displays the Workmates List
 *  -----------------------------------------
 *  IN = Caller Name     : String
 *       Workmates List : List<WorkmateDetails>
 *
 **************************************************************************************************/
public class ListWorkmatesViewFragment extends Fragment {

    // FOR TRACES
    private static final String TAG = ListWorkmatesViewFragment.class.getSimpleName();

    // Parameters for the construction of the fragment
    // 1 ==> Key Caller
    public static final String KEY_CALLER_LIST_WORKMATES_VIEW = "KEY_CALLER_LIST_WORKMATES_VIEW";
    // 2 ==> Workmates List
    public static final String KEY_LIST_WORKMATES_VIEW = "KEY_LIST_WORKMATES";

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
    // ---------------------------------------------------------------------------------------------
    //                                  FRAGMENT INSTANTIATION
    // ---------------------------------------------------------------------------------------------
    public static ListWorkmatesViewFragment newInstance(List<WorkmateDetails> workmatesDetails,
                                                        String caller) {
        Log.d(TAG, "newInstance: ");

        // Create new fragment
        ListWorkmatesViewFragment listWorkmatesViewFragment = new ListWorkmatesViewFragment();

        // Create bundle and add it some data
        Bundle args = new Bundle();
        final Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
        String json = gson.toJson(workmatesDetails);

        // 1 ==> Add Caller
        args.putString(KEY_CALLER_LIST_WORKMATES_VIEW, caller);
        // 2 ==> Add Workmates List
        args.putString(KEY_LIST_WORKMATES_VIEW, json);

        listWorkmatesViewFragment.setArguments(args);

        return listWorkmatesViewFragment;
    }
    // ---------------------------------------------------------------------------------------------
    //                                    ENTRY POINT
    // ---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        // Inflate the layout for this fragment
        mListView = inflater.inflate(R.layout.fragment_list_workmates_view,
                container, false);

        // Telling ButterKnife to bind all views in layout
        ButterKnife.bind(this, mListView);

        // Get data from Bundle (created in method newInstance)
        // 1 ==>  Workmates List
        Type collectionType = new TypeToken<List<WorkmateDetails>>() {}.getType();
        mWorkmatesDetails = new ArrayList<>();
        mWorkmatesDetails = new Gson().fromJson(getArguments()
                .getString(KEY_LIST_WORKMATES_VIEW, ""),collectionType);
        // 2 ==> Caller
        String caller = getArguments().getString(KEY_CALLER_LIST_WORKMATES_VIEW, "");
        Log.d(TAG, "onCreateView: caller = "+caller);

        // Configure RecyclerView
        this.configureRecyclerView();

        // Calling the method that configuring click on RecyclerView
        // Only if tha caller is WelcomeActivity
        if (caller.equals("WelcomeActivity")) this.configureOnClickRecyclerView();

        return mListView;
    }
    // ---------------------------------------------------------------------------------------------
    //                                    CONFIGURATION
    // ---------------------------------------------------------------------------------------------
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
    // ---------------------------------------------------------------------------------------------
    //                                       ACTIONS
    // ---------------------------------------------------------------------------------------------
    //  Configure clickListener on Item of the RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_list_workmates_view_item)
                .setOnItemClickListener((recyclerView, position, v) -> {

                    //Launch Restaurant Card Activity with placeDetails
                    startRestaurantCardActivity(mAdapter.getRestaurantIdentifier(position));
                });
    }
    // ---------------------------------------------------------------------------------------------
    //                                    CALL ACTIVITY
    // ---------------------------------------------------------------------------------------------
    private void startRestaurantCardActivity(String restaurantIdentifier){
        Log.d(TAG, "startRestaurantCardActivity: ");

        // Create a intent for call RestaurantCardActivity
        Intent intent = new Intent(getActivity(), RestaurantCardActivity.class);

        final Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
        String json;

        // Browse the list of restaurants loaded in the ViewModel
        Go4LunchViewModel model = ViewModelProviders.of(getActivity()).get(Go4LunchViewModel.class);
        for (RestaurantDetails restaurantDetails :  model.getRestaurantsDetails()){

            // Search restaurant details
            if (restaurantIdentifier.equals(restaurantDetails.getId())) {

                // 1 ==> Sends the Restaurant details
                json = gson.toJson(restaurantDetails);
                intent.putExtra(RestaurantCardActivity.KEY_DETAILS_RESTAURANT_CARD, json);

                // Go out as soon as the restaurant details are found
                break;
            }
        }

        // 2 ==> Sends the workmates list
        json = gson.toJson(mWorkmatesDetails);
        intent.putExtra(RestaurantCardActivity.KEY_LIST_WORKMATES_RESTAURANT_CARD, json);

        // Call RestaurantCardActivity
        startActivity(intent);
    }
}
