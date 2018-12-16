package com.android.sagot.go4lunch;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceDetails.PlaceDetails;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceNearBySearch.PlaceNearBySearch;
import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.android.sagot.go4lunch.Utils.GooglePlaceStreams;
import com.android.sagot.go4lunch.Utils.Toolbox;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Toolbox_Instrumented_Test {

    private static final String TAG = "Toolbox_Instrumented_Test";

    @Test
    public void locationStringFromLocation_I_Test() {

        Location location = new Location("");
        location.setLatitude(50.4830);
        location.setLongitude(2.48099);

        assertEquals("50.483,2.48099", Toolbox.locationStringFromLocation(location));
    }

    @Test
    public void isNetworkAvailable_I_Test() {

        Context context = InstrumentationRegistry.getTargetContext();

        assertEquals(true,Toolbox.isNetworkAvailable(context));
    }

    @Test
    public void formatTime_I_Test() {

        Context context = InstrumentationRegistry.getTargetContext();
        String language = Locale.getDefault().getLanguage();

        Log.d(TAG, "formatTime: language = "+language);

        if (language.equals("en")) {
            assertEquals("22.30pm", Toolbox.formatTime(context, 2230));
            assertEquals("10.30am", Toolbox.formatTime(context, 1030));
        }else{
            assertEquals("22.30 du soir", Toolbox.formatTime(context, 2230));
            assertEquals("10.30 du matin", Toolbox.formatTime(context, 1030));
        }
    }
}
