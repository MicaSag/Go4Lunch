package com.android.sagot.go4lunch.Controllers.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.sagot.go4lunch.Controllers.Base.BaseActivity;
import com.android.sagot.go4lunch.R;
import com.android.sagot.go4lunch.notifications.NotificationsAlarmReceiver;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnCheckedChanged;

public class SettingsActivity extends BaseActivity {

    // For Debug
    private static final String TAG = "SettingsActivity";

    // For Defined SharedPreferences of the application
    // -------------------------------------------------
    SharedPreferences mSharedPreferences;
    // Create the key saving of the preferences of the application
    public static final String SHARED_PREF_NOTIFICATION_STATUS = "SHARED_PREF_NOTIFICATION_STATUS";

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.activity_settings_coordinatorLayout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_settings_switch) Switch mSwitch;

    // Creating an intent to execute our broadcast
    private PendingIntent mPendingIntent;
    // Creating alarmManager
    private AlarmManager mAlarmManager;

    // Notification Status
    private boolean mNotificationChecked;

    // ---------------------------------------------------------------------------------------------
    //                                DECLARATION BASE METHODS
    // ---------------------------------------------------------------------------------------------
    // BASE METHOD Implementation
    // Get the activity layout
    // CALLED BY BASE METHOD 'onCreate(...)'
    @Override
    protected int getActivityLayout() {
        return R.layout.activity_settings;
    }

    // BASE METHOD Implementation
    // Get the coordinator layout
    // CALLED BY BASE METHOD
    @Override
    protected View getCoordinatorLayout() {
        return mCoordinatorLayout;
    }
    // ---------------------------------------------------------------------------------------------
    //                                      ENTRY POINT
    // ---------------------------------------------------------------------------------------------
    // ----------------------
    // OVERRIDE BASE METHODS
    // ----------------------
    // OVERRIDE BASE METHOD : onCreate(Bundle savedInstanceState)
    // To add the call to configureAlarmManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve SharedPreferences
        retrieveSharedPreferences();

        // Configure Toolbar
        this.configureToolBar();

        //Configuring The AlarmManager
        this.configureAlarmManager();
    }
    // ---------------------------------------------------------------------------------------------
    //                                   SHARED PREFERENCES
    // ---------------------------------------------------------------------------------------------
    // >> SHARED PREFERENCES RETRIEVE <-------
    private void retrieveSharedPreferences() {
        Log.d(TAG, "retrievesPreferences: ");
        // READ SharedPreferences
        mSharedPreferences = getPreferences(MODE_PRIVATE);

        // TEST == >>> Allows to erase all the preferences ( Useful for the test phase )
        //Log.i("MOOD","CLEAR COMMIT PREFERENCES");
        //mSharedPreferences.edit().clear().commit();

        // Retrieve Notification Status
        Log.d(TAG, "retrievesPreferences: notificationStatus Restoration");
        Boolean notificationChecked = mSharedPreferences
                .getBoolean(SHARED_PREF_NOTIFICATION_STATUS,false);
        Log.d(TAG, "retrievesPreferences: notificationChecked = "+notificationChecked);
        // Retrieve the notification status of the SharedPreferences
        // And check it in UI
        getAndCheckNotificationStatus(notificationChecked);
    }
    // Retrieve the notification status of the SharedPreferences
    // And check it in UI
    private void getAndCheckNotificationStatus(Boolean notificationChecked){
        Log.d(TAG, "getAndCheckNotificationStatus: ");

        mNotificationChecked = notificationChecked;
        mSwitch.setChecked(mNotificationChecked);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");

        // Add the Notification Status in shared Preferences
        mSharedPreferences.edit()
                .putBoolean(SHARED_PREF_NOTIFICATION_STATUS, mNotificationChecked).apply();
        super.onPause();
    }

    // ---------------------------------------------------------------------------------------------
    //                                     TOOLBAR
    // ---------------------------------------------------------------------------------------------
    protected void configureToolBar() {
        Log.d(TAG, "configureToolBar: ");

        // Change the toolbar Title
        setTitle(R.string.activity_welcome_title);
        // Sets the Toolbar
        setSupportActionBar(mToolbar);
    }
    // ---------------------------------------------------------------------------------------------
    //                                       ACTIONS
    // ---------------------------------------------------------------------------------------------
    // ---------------
    // ACTION SWITCH
    // ---------------
    @OnCheckedChanged(R.id.activity_settings_switch)
    public void OnCheckedChanged(CompoundButton cb, boolean isChecked){
        Log.d(TAG, "OnCheckedChanged: isChecked = "+isChecked);
        Log.d(TAG, "OnCheckedChanged: isChecked Before = "+mNotificationChecked);

        // If check switch and old state is not checked
        if (isChecked && !mNotificationChecked) this.startAlarm();
        if (!isChecked && mNotificationChecked) this.stopAlarm();

        mNotificationChecked = isChecked;

        Log.d(TAG, "OnCheckedChanged: isChecked After = " +mNotificationChecked);
    }
    // ---------------------------------------------------------------------------------------------
    //                                     NOTIFICATION
    // ---------------------------------------------------------------------------------------------
    // ----------------------------
    // CONFIGURATION ALARM MANAGER
    // ----------------------------
    private void configureAlarmManager(){
        Log.d(TAG, "configureAlarmManager: ");
        // Create the Intent to destination of the BroadcastReceiver
        Intent alarmIntent = new Intent(SettingsActivity.this,
                NotificationsAlarmReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    // ------------------------------
    // SCHEDULE TASK  : AlarmManager
    // ------------------------------
    // @Return The hour it milliseconds of release of the first alarm
    private long nextNotification() {
        Log.d(TAG, "nextNotification: ");

        Calendar cal = Calendar.getInstance();
        // If it is after noon then we add one day to the meter of release of the alarm
        if (cal.get(Calendar.HOUR_OF_DAY) > 12 ) cal.add(Calendar.DATE, 1);
        // The alarm next one will thus be at 12:00 am tomorrow
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 0);
        //The hour it milliseconds of release of the first alarm
        return cal.getTimeInMillis();
    }
    // Start Alarm
    private void startAlarm() {
        Log.d(TAG, "startAlarm: ");

        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setInexactRepeating( AlarmManager.RTC_WAKEUP,     // which will wake up the device when it goes off
                nextNotification(),          // First start at 12:00
                mAlarmManager.INTERVAL_DAY,  // Will trigger every day
                mPendingIntent);
        Snackbar.make(mCoordinatorLayout,"Notifications set !",Snackbar.LENGTH_LONG).show();
    }
    // Stop Alarm
    private void stopAlarm() {
        Log.d(TAG, "stopAlarm: ");

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(mPendingIntent);
        Snackbar.make(mCoordinatorLayout,"Notifications canceled !",Snackbar.LENGTH_LONG).show();
    }
}
