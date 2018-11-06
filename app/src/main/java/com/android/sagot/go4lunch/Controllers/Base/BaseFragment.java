package com.android.sagot.go4lunch.Controllers.Base;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.sagot.go4lunch.Controllers.Activities.RestaurantCardActivity;
import com.android.sagot.go4lunch.Models.Go4LunchViewModel;
import com.android.sagot.go4lunch.Models.RestaurantDetails;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BaseFragment extends Fragment {

    private static final String TAG = BaseFragment.class.getSimpleName();

    // ---------------------------------------------------------------------------------------------
    //                                    CALL ACTIVITY
    // ---------------------------------------------------------------------------------------------
    public void startRestaurantCardActivity(String restaurantIdentifier){
        Log.d(TAG, "startRestaurantCardActivity: ");

        // Create a intent for call RestaurantCardActivity
        Intent intent = new Intent(getActivity(), RestaurantCardActivity.class);

        // Create a Gson Object
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

                // ==> Sends the Restaurant details
                json = gson.toJson(restaurantDetails);
                intent.putExtra(RestaurantCardActivity.KEY_DETAILS_RESTAURANT_CARD, json);

                // Go out as soon as the restaurant details are found
                break;
            }
        }

        // Call RestaurantCardActivity with 3 parameters
        startActivity(intent);
    }
}
