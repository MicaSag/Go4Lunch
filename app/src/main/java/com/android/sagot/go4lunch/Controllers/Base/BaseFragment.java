package com.android.sagot.go4lunch.Controllers.Base;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.sagot.go4lunch.Models.Go4LunchViewModel;
import com.android.sagot.go4lunch.Models.firestore.Restaurant;

import java.util.Map;


public class BaseFragment extends Fragment {

    private static final String TAG = BaseFragment.class.getSimpleName();


    // ---------------------------------------------------------------------------------------------
    //                                        VIEW MODEL ACCESS
    // ---------------------------------------------------------------------------------------------
    // Get current restaurant list of the Model
    public Map<String,Restaurant> getRestaurantMapOfTheModel(){
        Log.d(TAG, "getRestaurantMapOfTheModel: ");

        Go4LunchViewModel model = ViewModelProviders.of(getActivity()).get(Go4LunchViewModel.class);

        return model.getListRestaurant();
    }
}
