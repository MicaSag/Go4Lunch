package com.android.sagot.go4lunch.Views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.sagot.go4lunch.Models.WorkmateDetails;
import com.android.sagot.go4lunch.R;
import com.bumptech.glide.RequestManager;

import java.util.List;

public class ListWorkmatesViewAdapter extends RecyclerView.Adapter<ListWorkmatesViewHolder> {

    // FOR DATA
    private List<WorkmateDetails> mListWorkmatesDetails;

    // Declaring a Glide object
    private RequestManager mGlide;

    // CONSTRUCTOR
    public ListWorkmatesViewAdapter(List<WorkmateDetails> listParticipantsDetails, RequestManager glide) {
        mListWorkmatesDetails = listParticipantsDetails;
        mGlide = glide;
    }

    @Override
    public ListWorkmatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_list_workmates_view_item, parent, false);

        return new ListWorkmatesViewHolder(view);
    }

    // UPDATE VIEW HOLDER WITH A PARTICIPANT INFORMATION
    @Override
    public void onBindViewHolder(ListWorkmatesViewHolder viewHolder, int position) {
        viewHolder.updateWithParticipantDetails(mListWorkmatesDetails.get(position), this.mGlide);
    }

    // RETURN THE TOTAL COUNT OF ITEMS IN THE LIST
    @Override
    public int getItemCount() {
        return mListWorkmatesDetails.size();
    }

    // Returns the Restaurant Identifier of the current position
    public String getRestaurantIdentifier(int position){
        return this.mListWorkmatesDetails.get(position).getRestaurantIdentifier();
    }
}
