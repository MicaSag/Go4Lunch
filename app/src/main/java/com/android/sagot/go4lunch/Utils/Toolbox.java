package com.android.sagot.go4lunch.Utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

import com.android.sagot.go4lunch.R;

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
     * Start an Activity
     *
     * @param  context   : context of the application
     *         className : className of the activity called
     *         key       : receiving key of the activity called
     *         keyValue  : sent key content
     * @return none
     */
    public static void startActivity(Context context, Class className, String key, String keyValue){

        // Create a intent for call RestaurantCardActivity
        Intent intent = new Intent(context, className);

        // ==> Sends the Restaurant details
        if (!(key == null)) intent.putExtra(key,keyValue);

        // Call RestaurantCardActivity with 3 parameters
        context.startActivity(intent);
    }

    /**
     * Checking whether network is connected
     * @param context Context to get {@link ConnectivityManager}
     * @return true if Network is connected, else false
     */
    public static boolean isNetworkAvailable(Context context) {

        try {
            // Get ConnectivityManager
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                // Get ActiveNetworkInfo
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                // see if a network connection is established and Wifi or Mobile connection
                if (activeNetworkInfo != null &&
                        activeNetworkInfo.isConnected())
                    return true;

        } catch (Exception e) {
            Log.d(TAG, "isNetworkAvailable: KO");
            return false;
        }
        Log.d(TAG, "isNetworkAvailable: KO");
        return false;
    }
    /**
     * CLASS for set min/max value for EditText
     */
    public static class MinMaxFilter implements InputFilter {

        private int mIntMin, mIntMax;

        public MinMaxFilter(int minValue, int maxValue) {
            this.mIntMin = minValue;
            this.mIntMax = maxValue;
        }

        public MinMaxFilter(String minValue, String maxValue) {
            this.mIntMin = Integer.parseInt(minValue);
            this.mIntMax = Integer.parseInt(maxValue);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(mIntMin, mIntMax, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

    /**
     * Formatting time
     * @param
     * @return time in format am or pm
     */
    public static String formatTime (Context context, Integer time) {

        return ((time > 999) ? (String.valueOf(time)).substring(0, 2)
                : (String.valueOf(time)).substring(0, 1))
                + "."
                + ((time > 999) ? (String.valueOf(time)).substring(2, 4)
                : (String.valueOf(time)).substring(1, 2))
                + ((time > 1200) ? context.getString(R.string.list_restaurant_text_4)
                : context.getString(R.string.list_restaurant_text_5));
    }

    /**
     * Formatting Place Photo URL
     * @param
     * @return Url Place Photo
     */
    public static String formatPlacePhotoUrl(String maxWidth, String photoReference, String key){

    return "https://maps.googleapis.com/maps/api/place/photo?"
                + "maxwidth=" + maxWidth
                + "&photoreference=" + photoReference
                + "&key=" + key;
    }

    /**
     * @since 1.0
     * Return date in format string JJ/MM/AA
     * @param date
     *          String starting with a date in the format SSAAMMJJ
     * @return String Date in format JJ/MM/AA
     */
    public static String dateReformat(String date){

        String JJ = date.substring(6,8);            // Day
        String MM = date.substring(4,6);            // Month
        String AA = date.substring(2,4);            // Year

        return JJ+"/"+MM+"/"+AA;                    // JJ/MM/AA
    }

    /**
     * @since 1.0
     * Return date in format string SSAAMMJJ
     * @param date
     *          String starting with a date in the format begin SSAA-MM-JJ...
     * @return String Date in format SSAAMMJJ
     */
    public static String dateReformatSSAAMMJJ(String date){

        String SSAA = date.substring(0,4);           // Year
        String MM = date.substring(5,7);             // Month
        String JJ = date.substring(8,10);            // Day

        return SSAA+MM+JJ;
    }
}
