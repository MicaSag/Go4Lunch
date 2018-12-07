package com.android.sagot.go4lunch.Models.sharedPreferences;

/**
 *  Activity settings data saved in shared preferences
 */
public class Preferences_SettingsActivity {

    // Status of notification ( UI Notification Switch )
    private boolean mNotificationStatus = false;

    // Search Radius ( UI Search Radius EditText )
    private String mSearchRadius;


    // GETTER
    public boolean isNotificationStatus() {
        return mNotificationStatus;
    }

    public String getSearchRadius() {
        return mSearchRadius;
    }


    // SETTER
    public void setNotificationStatus(boolean notificationStatus) {
        this.mNotificationStatus = notificationStatus;
    }


    public void setSearchRadius(String mSearchRadius) {
        this.mSearchRadius = mSearchRadius;
    }
}
