package com.android.sagot.go4lunch.Controllers.Fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.sagot.go4lunch.Controllers.Activities.RestaurantCardActivity;
import com.android.sagot.go4lunch.Controllers.Activities.WelcomeActivity;
import com.android.sagot.go4lunch.Controllers.Base.BaseFragment;
import com.android.sagot.go4lunch.Models.AdapterRestaurant;
import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.android.sagot.go4lunch.R;
import com.android.sagot.go4lunch.Utils.ItemClickSupport;
import com.android.sagot.go4lunch.Utils.Toolbox;
import com.android.sagot.go4lunch.Views.ListRestaurantsViewAdapter;
import com.android.sagot.go4lunch.api.RestaurantHelper;
import com.bumptech.glide.Glide;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**************************************************************************************************
 *
 *  FRAGMENT that displays the Restaurant List
 *  ------------------------------------------
 *  IN = No parameters
 *
 **************************************************************************************************/
public class ListRestaurantsViewFragment extends BaseFragment {

    // FOR TRACES
    private static final String TAG = ListRestaurantsViewFragment.class.getSimpleName();

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.fragment_list_restaurant_view_recycler_view) RecyclerView mRecyclerView;

    // View of the Fragment
    private View mListView;

    // Declare Adapter of the RecyclerView
    private ListRestaurantsViewAdapter mAdapter;

    public ListRestaurantsViewFragment() {
        // Required empty public constructor
    }
    // ---------------------------------------------------------------------------------------------
    //                                  FRAGMENT INSTANTIATION
    // ---------------------------------------------------------------------------------------------
    public static ListRestaurantsViewFragment newInstance() {
        Log.d(TAG, "newInstance: ");

        // Create new fragment
        return new ListRestaurantsViewFragment();
    }
    // ---------------------------------------------------------------------------------------------
    //                                    ENTRY POINT
    // ---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        // Inflate the layout for this fragment
        mListView = inflater.inflate(R.layout.fragment_list_restaurants_views, container, false);

        // Telling ButterKnife to bind all views in layout
        ButterKnife.bind(this, mListView);

        // Enables listening of restaurants in FireBase
        listenCurrentListRestaurant();

        // Configure RecyclerView
        this.configureRecyclerView(getFilteredMapAdapterRestaurantOfTheModel());

        // Calling the method that configuring click on RecyclerView
        this.configureOnClickRecyclerView();

        return mListView;
    }
    /**
     * Enables listening of restaurants in FireBase
     */
    public void listenCurrentListRestaurant() {
        Log.d(TAG, "listenCurrentListRestaurant: ");

        Set<Map.Entry<String, AdapterRestaurant>> setListAdapterRestaurant = getCompleteMapAdapterRestaurantOfTheModel().entrySet();
        Iterator<Map.Entry<String, AdapterRestaurant>> it = setListAdapterRestaurant.iterator();
        while(it.hasNext()){
            Map.Entry<String, AdapterRestaurant> adapterRestaurant = it.next();
            RestaurantHelper
                    .getRestaurantsCollection()
                    .document(adapterRestaurant.getValue().getRestaurant().getIdentifier())
                    .addSnapshotListener((document, e) -> {
                        if (e != null) {
                            Log.d(TAG, "fireStoreListener.onEvent: Listen failed: " + e);
                            return;
                        }
                        if (document != null) {
                            Restaurant restaurant = document.toObject(Restaurant.class);

                            AdapterRestaurant adRestaurant
                                    = new AdapterRestaurant(restaurant,
                                                            getCompleteMapAdapterRestaurantOfTheModel()
                                            .get(restaurant.getIdentifier()).getMarker());
                            Log.d(TAG, "onEvent: Id restaurant = "
                                    + adRestaurant.getRestaurant().getIdentifier());
                            Log.d(TAG, "onEvent: Name restaurant = "
                                    + adRestaurant.getRestaurant().getName());

                            // Update CompleteMapAdapterRestaurantOfTheModel
                            getCompleteMapAdapterRestaurantOfTheModel()
                                    .put(adRestaurant.getRestaurant().getIdentifier(),
                                            adRestaurant);

                            // FOR ListRestaurants Recycler View
                            getFilteredMapAdapterRestaurantOfTheModel()
                                    .put(adRestaurant.getRestaurant().getIdentifier(),
                                            adRestaurant);
                            // Update ListRestaurantsViewAdapter
                            if (mAdapter != null)
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }
    // ---------------------------------------------------------------------------------------------
    //                                    CONFIGURATION
    // ---------------------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView(LinkedHashMap<String,AdapterRestaurant> mapAdapterRestaurant){
        Log.d(TAG, "configureRecyclerView: ");

        this.mAdapter = new ListRestaurantsViewAdapter( getCurrentLocationOfTheModel(),
                                                        mapAdapterRestaurant,
                                                        Glide.with(this));

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
        Log.d(TAG, "configureOnClickRecyclerView: ");

        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_list_restaurants_view_item)
                .setOnItemClickListener((recyclerView, position, v) -> {

                    //Launch Restaurant Card Activity with Restaurant Details
                    Toolbox.startActivity(getActivity(),RestaurantCardActivity.class,
                            RestaurantCardActivity.KEY_DETAILS_RESTAURANT_CARD,
                            mAdapter.getRestaurantIdentifier(position));
                    });

    }
    // ---------------------------------------------------------------------------------------------
    //                                       UPDATE UI
    // ---------------------------------------------------------------------------------------------
    public void updateUI(){
        mAdapter.notifyDataSetChanged();
    }
}
