package com.android.sagot.go4lunch.Models.firestore;

import android.support.annotation.Nullable;

public class Restaurant {

    private String mIdentifier;             // Restaurant Identifier
    private String mName;                   // Name of the Restaurant
    @Nullable private String mAddress;      // Address of Restaurant
    @Nullable private String mOpeningTime;  // Opening time of Restaurant
    @Nullable private String mDistance;     // Distance where the restaurant is from the current position
    @Nullable private int mNbrParticipants; // Number of participants
    @Nullable private int mNbrStars;        // Number of stars that the restaurant got
    @Nullable private String mPhotoUrl;     // URL of the Restaurant photo
    @Nullable private String mWebSiteUrl;   // URL of the Web site
    @Nullable private String mType;         // Type of the Restaurant
    @Nullable private String mLat;          // Latitude  of the Restaurant on the Map
    @Nullable private String mLng;          // Longitude of the Restaurant on the Map
    @Nullable private String mPhone;        // Phone number to call the Restaurant

    // Blank constructor necessary for use with FireBase
    public Restaurant() { }

    //
    public Restaurant(String identifier, String name, @Nullable String address, @Nullable String openingTime,
                      @Nullable String distance, @Nullable int nbrParticipants, @Nullable int nbrStars,
                      @Nullable String photoUrl, @Nullable String webSiteUrl, @Nullable String type,
                      @Nullable String lat, @Nullable String lng, @Nullable String phone) {
        mIdentifier = identifier;
        mName = name;
        mAddress = address;
        mOpeningTime = openingTime;
        mDistance = distance;
        mNbrParticipants = nbrParticipants;
        mNbrStars = nbrStars;
        mPhotoUrl = photoUrl;
        mWebSiteUrl = webSiteUrl;
        mType = type;
        mLat = lat;
        mLng = lng;
        mPhone = phone;
    }

    // --- GETTERS ---

    public String getIdentifier() {
        return mIdentifier;
    }

    public String getName() {
        return mName;
    }

    @Nullable
    public String getAddress() {
        return mAddress;
    }

    @Nullable
    public String getOpeningTime() {
        return mOpeningTime;
    }

    @Nullable
    public String getDistance() {
        return mDistance;
    }

    @Nullable
    public int getNbrParticipants() {
        return mNbrParticipants;
    }

    @Nullable
    public int getNbrStars() {
        return mNbrStars;
    }

    @Nullable
    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    @Nullable
    public String getWebSiteUrl() {
        return mWebSiteUrl;
    }

    @Nullable
    public String getType() {
        return mType;
    }

    @Nullable
    public String getLat() {
        return mLat;
    }

    @Nullable
    public String getLng() {
        return mLng;
    }

    @Nullable
    public String getPhone() {
        return mPhone;
    }

    // --- SETTERS ---

    public void setIdentifier(String identifier) {
        mIdentifier = identifier;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setAddress(@Nullable String address) {
        mAddress = address;
    }

    public void setOpeningTime(@Nullable String openingTime) {
        mOpeningTime = openingTime;
    }

    public void setDistance(@Nullable String distance) {
        mDistance = distance;
    }

    public void setNbrParticipants(@Nullable int nbrParticipants) {
        mNbrParticipants = nbrParticipants;
    }

    public void setNbrStars(@Nullable int nbrStars) {
        mNbrStars = nbrStars;
    }

    public void setPhotoUrl(@Nullable String photoUrl) {
        mPhotoUrl = photoUrl;
    }

    public void setWebSiteUrl(@Nullable String webSiteUrl) {
        mWebSiteUrl = webSiteUrl;
    }

    public void setType(@Nullable String type) {
        mType = type;
    }

    public void setLat(@Nullable String lat) {
        mLat = lat;
    }

    public void setLng(@Nullable String lng) {
        mLng = lng;
    }

    public void setPhone(@Nullable String phone) {
        mPhone = phone;
    }
}
