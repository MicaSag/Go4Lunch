package com.android.sagot.go4lunch.Models;

import com.google.android.gms.maps.model.LatLng;

public class RestaurantDetails {
    private String mId;             // Place Identifier
    private String mName;           // Name of the place
    private String mAddress;        // Address of the Place
    private String mOpeningTime;    // Opening time of the place
    private String mDistance;       // Distance where the restaurant is from the current position
    private int mNbrParticipants;   // Number of participants
    private int mNbrStars;          // Number of stars that the restaurant got
    private String mPhotoUrl;       // URL of the place's photo
    private String mWebSiteUrl;     // URL of the Web site
    private String mType;           // Type od the place
    private String mLat;            // Latitude  of the place on the Map
    private String mLng;            // Longitude of the place on the Map

    public String getId() {
        return mId;
    }

    public void setId(String idt) {
        mId = idt;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String photoURL) {
        mPhotoUrl = photoURL;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getOpeningTime() {
        return mOpeningTime;
    }

    public void setOpeningTime(String openingTime) {
        mOpeningTime = openingTime;
    }

    public String getLat() {
        return mLat;
    }

    public void setLat(String lat) {
        mLat = lat;
    }

    public String getLng() {
        return mLng;
    }

    public void setLng(String lng) {
        mLng = lng;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public int getNbrParticipants() {
        return mNbrParticipants;
    }

    public void setNbrParticipants(int nbrParticipants) {
        mNbrParticipants = nbrParticipants;
    }

    public int getNbrStars() {
        return mNbrStars;
    }

    public void setNbrStars(int nbrStars) {
        mNbrStars = nbrStars;
    }

    public String getWebSiteUrl() {
        return mWebSiteUrl;
    }

    public void setWebSiteUrl(String webSiteUrl) {
        mWebSiteUrl = webSiteUrl;
    }
}