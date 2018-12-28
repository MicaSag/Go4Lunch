package com.android.sagot.go4lunch.Views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.sagot.go4lunch.Controllers.Activities.WelcomeActivity;
import com.android.sagot.go4lunch.Models.AdapterRestaurant;
import com.android.sagot.go4lunch.Models.firestore.Restaurant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class AutoCompleteSearchAdapter extends ArrayAdapter<AdapterRestaurant> implements Filterable {

    private Context mContext;
    private ArrayList<AdapterRestaurant> mRestaurantList;
    private ArrayList<AdapterRestaurant> mRestaurantListFilter = new ArrayList<>();
    private ValueFilter valueFilter;

    private static final String TAG = "AutoCompleteSearchAdapt";

    public AutoCompleteSearchAdapter(Context context,
                                     ArrayList<AdapterRestaurant> restaurants) {
        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
        mContext = context;
        mRestaurantList = restaurants;

        // By default, no restaurant Marker will be visible on the Map
        for (AdapterRestaurant adapterRestaurant : restaurants){
            mRestaurantListFilter.add(adapterRestaurant);
        }
    }

    /**
     * Returns the number of restaurants
     *
     * NOT ACTIVATE = NO FILTER LIST RETURNED
     */
    /*@Override public int getCount() {
        return mRestaurantList.size();
    }*/

    /**
     * Returns a restaurant item
     */
    @Override public  AdapterRestaurant getItem(int position) {
        return mRestaurantList.get(position);
    }

    /**
     * Build one row
     */
    @Override public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = super.getView(position, convertView, parent);

        TextView textName    = row.findViewById(android.R.id.text1);
        TextView textAddress = row.findViewById(android.R.id.text2);

        AdapterRestaurant item = getItem(position);
        textName.setText(item.getRestaurant().getName());
        textAddress.setText(item.getRestaurant().getAddress());

        // Return the completed view to render on screen
        return row;
    }

    /**
     * Returns the filter for the restaurant choice
     */
    @Override public Filter getFilter() {

        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    /**
     * Perform filter
     */
    private class ValueFilter extends  Filter {
        @Override protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<AdapterRestaurant> filterList = new ArrayList<>();

                for (AdapterRestaurant adapterRestaurant : mRestaurantListFilter){

                    if ((adapterRestaurant.getRestaurant().getName().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        AdapterRestaurant restaurantItem = adapterRestaurant;
                        filterList.add(restaurantItem);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mRestaurantListFilter.size();
                results.values = mRestaurantListFilter;
            }
            return results;
        }

        /**
         * Publish Results
         */
        @Override protected void publishResults(CharSequence constraint, FilterResults results) {
            Log.d(TAG, "publishResults: ");

            // Reference of the complete list of restaurants
            LinkedHashMap<String,AdapterRestaurant> completeRestaurantList
                    = ((WelcomeActivity) mContext).getCompleteMapAdapterRestaurantOfTheModel();

            // By default, no restaurant Marker will be visible on the Map
            Set<Map.Entry<String, AdapterRestaurant>> setMapAdapterRestaurant
                    = completeRestaurantList.entrySet();
            Iterator<Map.Entry<String, AdapterRestaurant>> it = setMapAdapterRestaurant.iterator();
            while (it.hasNext()) {
                Map.Entry<String, AdapterRestaurant> adapterRestaurant = it.next();
                adapterRestaurant.getValue().getMarker().setVisible(false);
                Log.d(TAG, "publishResults: adapterRestaurant  NAME      = "
                        +adapterRestaurant.getValue().getRestaurant().getName());
                Log.d(TAG, "publishResults: adapterRestaurant identifier = "
                        +adapterRestaurant.getValue().getRestaurant().getIdentifier());
                Log.d(TAG, "publishResults: adapterRestaurant is visible = "
                        +adapterRestaurant.getValue().isVisible());
            }

            // Reference of the list of filtered restaurants
            LinkedHashMap<String,AdapterRestaurant> filteredRestaurantList
                    = ((WelcomeActivity) mContext).getFilteredMapAdapterRestaurantOfTheModel();

            // purge the list of filtered restaurants
            filteredRestaurantList.clear();

            // Reference of the filtered AutoCompletion result
            mRestaurantList = (ArrayList<AdapterRestaurant>) results.values;

            String identifier;
            for (AdapterRestaurant adapterRestaurant : mRestaurantList){
                identifier = adapterRestaurant.getRestaurant().getIdentifier();
                Log.d(TAG, "------------------------------------ FILTERED ------------------------------------------");
                Log.d(TAG, "publishResults: adapterRestaurant            = "+adapterRestaurant.getRestaurant());
                Log.d(TAG, "publishResults: adapterRestaurant identifier = "+identifier);
                Log.d(TAG, "publishResults: adapterRestaurant marker     = "+adapterRestaurant.getMarker().isVisible());
                Log.d(TAG, "publishResults: adapterRestaurant nbrLikes   = "+adapterRestaurant.getRestaurant().getNbrLikes());
                Log.d(TAG, "publishResults: adapterRestaurant nbrParti   = "+adapterRestaurant.getRestaurant().getNbrParticipants());


                // Only the markers of filtered AutoCompletion result will be visible
                adapterRestaurant.getMarker().setVisible(true);

                // Add restaurant at the new list
                filteredRestaurantList
                        .put(   completeRestaurantList
                                            .get(adapterRestaurant.getRestaurant().getIdentifier())
                                            .getRestaurant().getIdentifier(),
                                completeRestaurantList
                                            .get(adapterRestaurant.getRestaurant().getIdentifier()));
            }

            // Refresh the list of restaurants
            (((WelcomeActivity) mContext).getListRestaurantsViewFragment()).updateUI();
        }

        /**
         * This method allows you to correctly format a selected answer in the list
         * so that it is displayed correctly in the main search area
         */
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Log.d(TAG, "convertResultToString: ");
            
            // Override this method to display a readable result in the AutocompleteTextView
            // when clicked.
            if (resultValue instanceof Restaurant) {
                return ((Restaurant) resultValue).getName();
            } else {
                return super.convertResultToString(resultValue);
            }
        }
    }
}
