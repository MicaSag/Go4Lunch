package com.android.sagot.go4lunch.Utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Toolbox containing various functions used in the application
 */
public class Toolbox {

    // for Debug
    private static final String TAG = Toolbox.class.getSimpleName();

    /**
     * Function that returns the current day
     *
     * @return  int : 0 = Sunday
     *                1 = Monday
     *                2 = Tuesday
     *                3 = Wednesday
     *                4 = Thursday
     *                5 = Friday
     *                6 = Saturday
     */
    public static int getCurrentDay() {
        Log.d(TAG, "getCurrentDay: ");

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        return calendar.get(calendar.DAY_OF_WEEK);
    }

    /**
     * Formatting Location Coordinates in String
     *
     * @param location : location in format location
     * @return String  : location in format String
     */
    public static String locationStringFromLocation(final Location location) {
        Log.d(TAG, "locationStringFromLocation: ");

        String latitude = Location.convert(location.getLatitude(), Location.FORMAT_DEGREES);
        String lat = latitude.replaceAll(",",".");
        String longitude = Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
        String lon = longitude.replaceAll(",",".");

        Log.d(TAG, "locationStringFromLocation: "+lat+ "," + lon);

        return lat+ "," + lon;
    }

    /**
     * Formatting Location Coordinates in String
     *
     * @param  context   : context of the application
     *         className : className of the activity called
     *         key       : receiving key of the activity called
     *         keyValue  : sent key content
     * @return none
     */
    public static void startActivity(Context context, Class className, String key, Object keyValue){

        // Create a intent for call RestaurantCardActivity
        Intent intent = new Intent(context, className);

        // Create a Gson Object
        final Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
        String json;

        // ==> Sends the Restaurant details
        json = gson.toJson(keyValue);
        intent.putExtra(key, json);

        // Call RestaurantCardActivity with 3 parameters
        context.startActivity(intent);
    }
}
