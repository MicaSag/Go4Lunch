package com.android.sagot.go4lunch.Models;

public class WorkmateDetails {

    private String mParticipantPhotoUrl;    // Url of the participant's photo
    private String mName;                   // Participant Name
    private String mRestaurantName;         // Name of the restaurant chosen
    private String mRestaurantIdentifier;   // Restaurant Identifier

    public String getParticipantPhotoUrl() {
        return mParticipantPhotoUrl;
    }

    public void setParticipantPhotoUrl(String participantPhotoUrl) {
        mParticipantPhotoUrl = participantPhotoUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getRestaurantName() {
        return mRestaurantName;
    }

    public void setRestaurantName(String restaurantChoice) {
        mRestaurantName = restaurantChoice;
    }

    public String getRestaurantIdentifier() {
        return mRestaurantIdentifier;
    }

    public void setRestaurantIdentifier(String restaurantIdentifier) {
        mRestaurantIdentifier = restaurantIdentifier;
    }
}
