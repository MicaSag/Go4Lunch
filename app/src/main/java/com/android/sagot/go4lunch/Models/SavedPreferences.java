package com.android.sagot.go4lunch.Models;

import java.util.ArrayList;
import java.util.List;

/**
 *  Data group used in the application and saved in the SharedPreferences
 */

public class SavedPreferences {

    // Saves the status of the notification switch
    private boolean mNotificationStatus = false;

    public boolean isNotificationStatus() {
        return mNotificationStatus;
    }

    public void setNotificationStatus(boolean notificationStatus) {
        this.mNotificationStatus = notificationStatus;
    }
}
