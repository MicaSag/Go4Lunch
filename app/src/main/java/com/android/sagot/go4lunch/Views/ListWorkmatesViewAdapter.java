package com.android.sagot.go4lunch.Views;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.sagot.go4lunch.Models.firestore.User;
import com.android.sagot.go4lunch.R;
import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import io.reactivex.annotations.NonNull;

public class ListWorkmatesViewAdapter extends FirestoreRecyclerAdapter<User, ListWorkmatesViewHolder> {

    // Declaring a Glide object
    private RequestManager mGlide;

    // Declare Options<User>
    FirestoreRecyclerOptions<User> mOptions;

    // Declare restaurant Identifier
    String mRestaurantIdentifier;

    // CONSTRUCTOR
    public ListWorkmatesViewAdapter(@NonNull FirestoreRecyclerOptions<User> options
                                        , RequestManager glide
                                        , String restaurantIdentifier) {
        super(options);
        mOptions = options;
        mGlide = glide;
        mRestaurantIdentifier = restaurantIdentifier;
    }

    @Override
    public ListWorkmatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        return new ListWorkmatesViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_workmates_view_item, parent, false));
    }

    // UPDATE VIEW HOLDER WITH A PARTICIPANT INFORMATION
    @Override
    public void onBindViewHolder(@NonNull ListWorkmatesViewHolder viewHolder, int position, @NonNull User user) {
        viewHolder.updateWithParticipantDetails(user, mGlide, mRestaurantIdentifier);
    }

    // Returns the Restaurant Identifier of the current position
    public String getRestaurantIdentifier(int position){
        return mOptions.getSnapshots().get(position).getRestaurantIdentifier();
    }
}
