package com.android.sagot.go4lunch.Controllers.Base;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.sagot.go4lunch.Controllers.Fragments.MapViewFragment;
import com.android.sagot.go4lunch.Models.Go4LunchViewModel;
import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.android.sagot.go4lunch.R;
import com.google.android.gms.tasks.OnFailureListener;

import java.util.Map;

import static com.android.sagot.go4lunch.Utils.Toolbox.isNetworkAvailable;


public abstract class BaseFragment extends Fragment {

    // Force developer implement those methods
    //protected abstract int getActivity(); // Layout of the Child Activity

    // For Debugging Mode
    private static final String TAG = BaseFragment.class.getSimpleName();


    // ---------------------------------------------------------------------------------------------
    //                                        VIEW MODEL ACCESS
    // ---------------------------------------------------------------------------------------------
    // Get current restaurant list of the Model
    public Map<String,Restaurant> getRestaurantMapOfTheModel(){
        Log.d(TAG, "getRestaurantMapOfTheModel: ");

        Go4LunchViewModel model = ViewModelProviders.of(getActivity()).get(Go4LunchViewModel.class);

        return model.getMapRestaurant();
    }
}
