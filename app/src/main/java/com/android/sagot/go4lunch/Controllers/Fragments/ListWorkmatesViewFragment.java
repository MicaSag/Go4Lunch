package com.android.sagot.go4lunch.Controllers.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.sagot.go4lunch.Controllers.Activities.RestaurantCardActivity;
import com.android.sagot.go4lunch.Controllers.Base.BaseFragment;
import com.android.sagot.go4lunch.Models.Go4LunchViewModel;
import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.android.sagot.go4lunch.Models.firestore.User;
import com.android.sagot.go4lunch.R;
import com.android.sagot.go4lunch.Utils.ItemClickSupport;
import com.android.sagot.go4lunch.Utils.Toolbox;
import com.android.sagot.go4lunch.Views.ListWorkmatesViewAdapter;
import com.android.sagot.go4lunch.api.UserHelper;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**************************************************************************************************
 *
 *  FRAGMENT that displays the Workmates List
 *  -----------------------------------------
 *  IN = Restaurant Identifier  : String
 *
 **************************************************************************************************/
public class ListWorkmatesViewFragment extends BaseFragment {

    // FOR TRACES
    private static final String TAG = ListWorkmatesViewFragment.class.getSimpleName();

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.fragment_list_workmates_view_recycler_view) RecyclerView mRecyclerView;

    // View of the Fragment
    private View mListView;

    // Declare Adapter of the RecyclerView
    private ListWorkmatesViewAdapter mAdapter;
    // And its parameter a restaurant identifier
    private String mRestaurantIdentifier;
    // Parameter for the construction of the fragment
    public static final String KEY_RESTAURANT_IDENTIFIER = "KEY_RESTAURANT_IDENTIFIER";

    public ListWorkmatesViewFragment() {
        // Required empty public constructor
    }
    // ---------------------------------------------------------------------------------------------
    //                                  FRAGMENT INSTANTIATION
    // ---------------------------------------------------------------------------------------------
    public static ListWorkmatesViewFragment newInstance(String restaurantIdentifier) {
        Log.d(TAG, "newInstance: ");

        // Create new fragment
        ListWorkmatesViewFragment listWorkmatesViewFragment = new ListWorkmatesViewFragment();

        // Create bundle and add it some data
        Bundle args = new Bundle();

        // ==> PUT restaurantIdentifier
        args.putString(KEY_RESTAURANT_IDENTIFIER, restaurantIdentifier);

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

        //==> GET restaurantIdentifier
        mRestaurantIdentifier = getArguments().getString(KEY_RESTAURANT_IDENTIFIER, "");
        Log.d(TAG, "onCreateView: restaurantIdentifier = "+mRestaurantIdentifier);

        // Configure RecyclerView
        this.configureRecyclerView();

        // Calling the method that configuring click on RecyclerView
        // Only if the restaurantIdentifier is empty
        if (mRestaurantIdentifier == "") this.configureOnClickRecyclerView();

        return mListView;
    }
    // ---------------------------------------------------------------------------------------------
    //                                    CONFIGURATION
    // ---------------------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView(){
        Log.d(TAG, "configureRecyclerView: ");

        // Create a FireStore Query
        Query query;
        // if the restaurant ID is empty then all workmates will be displayed
        if (mRestaurantIdentifier == "" ) {
            query = UserHelper.getAllUser();
        } else {
            // Otherwise the displayed workmates will only be those who have selected the restaurant
            // whose identifier has been passed as a parameter of the fragment
            query = UserHelper.getAllUser()
                    .whereEqualTo("restaurantIdentifier",mRestaurantIdentifier);
        }
        // Create adapter passing the list of RestaurantDetails
        this.mAdapter = new ListWorkmatesViewAdapter(generateOptionsForAdapter(query)
                                                                , Glide.with(this)
                                                                ,mRestaurantIdentifier
                                                                ,getActivity());

        // Attach the adapter to the recycler view to populate items
        this.mRecyclerView.setAdapter(this.mAdapter);
        // Set layout manager to position the items
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
    //  options for RecyclerView from a Query
    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }
    // ---------------------------------------------------------------------------------------------
    //                                       ACTIONS
    // ---------------------------------------------------------------------------------------------
    //  Configure clickListener on Item of the RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_list_workmates_view_item)
                .setOnItemClickListener((recyclerView, position, v) -> {

                    // The restaurant card is called if the workmate chooses one
                    if (mAdapter.getRestaurantIdentifier(position) != null) {
                        //Launch Restaurant Card Activity with restaurantIdentifier
                        Toolbox.startActivity(getActivity(),RestaurantCardActivity.class,
                                RestaurantCardActivity.KEY_DETAILS_RESTAURANT_CARD,
                                mAdapter.getRestaurantIdentifier(position));
                    }
                });
    }
}
