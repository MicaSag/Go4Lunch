package com.android.sagot.go4lunch.Models.firestore;

import android.support.annotation.Nullable;

import java.util.List;

public class User {

    private String mUid;
    private String mUserName;
    @Nullable private String mRestaurantIdentifier;
    @Nullable private String mRestaurantName;
    @Nullable private List<String> mListRestaurantLiked;
    @Nullable private String mUrlPicture;

    public User() { }

    public User(String uid, String userName, @Nullable String restaurantIdentifier, @Nullable String restaurantName,
                @Nullable List<String> listRestaurantLiked, @Nullable String urlPicture) {
        this.mUid = uid;
        this.mUserName = userName;
        this.mRestaurantIdentifier = restaurantIdentifier;
        this.mRestaurantName = restaurantName;
        this.mListRestaurantLiked = listRestaurantLiked;
        this.mUrlPicture = urlPicture;
    }

    // --- GETTERS ---
    public String getUid() { return mUid; }
    public String getUserName() { return mUserName; }
    @Nullable public String getUrlPicture() { return mUrlPicture; }
    @Nullable public String getRestaurantIdentifier() { return mRestaurantIdentifier; }
    @Nullable public String getRestaurantName() { return mRestaurantName; }
    @Nullable public List<String> getListRestaurantLiked() { return mListRestaurantLiked; }

    // --- SETTERS ---
    public void setUserName(String userName) { this.mUserName = userName; }
    public void setUid(String uid) { this.mUid = uid; }
    public void setUrlPicture(@Nullable String urlPicture) { this.mUrlPicture = urlPicture; }
    public void setRestaurantIdentifier(@Nullable String restaurantName) { mRestaurantIdentifier = restaurantName; }
    public void setRestaurantName(@Nullable String restaurantName) { mRestaurantName = restaurantName; }
    public void setListRestaurantLiked(@Nullable List<String> listRestaurantLiked) { mListRestaurantLiked = listRestaurantLiked; }
}
