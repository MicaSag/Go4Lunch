package com.android.sagot.go4lunch;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.android.sagot.go4lunch.Controllers.Activities.WelcomeActivity;
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
public class GooglePlaceStreams_Instrumented_Test {

    private static final String TAG = "GooglePlaceStreams_Inst";

    @Test
    public void fetchRestaurantsNearBySearch_I_Test() throws Exception {
        Log.d(TAG, "fetchRestaurantsNearBySearch_I_Test: ");

        Location location = new Location("");
        location.setLatitude(50.4830);
        location.setLongitude(2.48099);

        String key = BuildConfig.google_maps_api;
        String radius = "500";

        // Get the stream
        Observable<PlaceNearBySearch> restaurantsNearBySearch
                = GooglePlaceStreams.streamFetchRestaurantsNearBySearch(Toolbox.locationStringFromLocation(location), radius);
        // Create a restaurant TestObserver
        TestObserver<PlaceNearBySearch> testObserver = new TestObserver<>();
        // Launch observable
        restaurantsNearBySearch.subscribeWith(testObserver)
                .assertNoErrors()       // Check if no errors
                .assertNoTimeout()      // Check if no Timeout
                .awaitTerminalEvent();  // Await the stream terminated before continue

        // Get list of restaurants fetched
        PlaceNearBySearch newsFetched = testObserver.values().get(0);

        // Verify if status: "OK"
        assertThat("The status of the Stream was read correctly", newsFetched.getStatus().equals("OK"));

        // Verify the name of the Place
        assertThat("The name of the Restaurant was read correctly", newsFetched.getResults().get(0).getName().equals("Friterie du bateau"));

        // Verify Place Id Value
        assertThat("The Place Id of the Restaurant was read correctly", newsFetched.getResults().get(0).getPlaceId().equals("ChIJ9xxXZ5wQ3UcR58lEiarVqlY"));

        // Verify scope Value
        assertThat("The Scope of the Restaurant was read correctly", newsFetched.getResults().get(0).getScope().equals("GOOGLE"));
    }

    @Test
    public void fetchListRestaurantId_I_Test() throws Exception {
        Log.d(TAG, "fetchListRestaurantId_I_Test: ");

        Location location = new Location("");
        location.setLatitude(50.4830);
        location.setLongitude(2.48099);

        String key = BuildConfig.google_maps_api;
        String radius = "600";

        // Get the stream
        Observable<List<String>> listRestaurantsId
                = GooglePlaceStreams.streamFetchListRestaurantId(Toolbox.locationStringFromLocation(location), radius);
        // Create a restaurant TestObserver
        TestObserver<List<String>> testObserver = new TestObserver<>();
        // Launch observable
        listRestaurantsId.subscribeWith(testObserver)
                .assertNoErrors()       // Check if no errors
                .assertNoTimeout()      // Check if no Timeout
                .awaitTerminalEvent();  // Await the stream terminated before continue

        // Get list of restaurants fetched
        List<String> newsFetched = testObserver.values().get(0);

        // Verify Places Id
        assertThat("The First Place ID of the List of the Restaurants was read correctly", newsFetched.get(0).equals("ChIJ28LrjZwQ3UcRf_UQkipLNyc"));
        assertThat("The Second Place ID of the List of the restaurants was read correctly", newsFetched.get(1).equals("ChIJZ7eTV3Ua3UcRU8Fzi4HfXG0"));
        assertThat("The  Fifth Place ID of the List of the restaurants was read correctly", newsFetched.get(4).equals("ChIJZer74J8Q3UcR_A7s76dPR3A"));
    }

    @Test
    public void fetchPlaceDetails_I_Test() throws Exception {
        Log.d(TAG, "fetchPlaceDetails_I_Test: ");

        String key = BuildConfig.google_maps_api;
        String placeId = "ChIJ28LrjZwQ3UcRf_UQkipLNyc";

        // Get the stream
        Observable<PlaceDetails> placeDetails
                = GooglePlaceStreams.streamFetchPlaceDetails(placeId);
        // Create a place Details TestObserver
        TestObserver<PlaceDetails> testObserver = new TestObserver<>();
        // Launch observable
        placeDetails.subscribeWith(testObserver)
                .assertNoErrors()       // Check if no errors
                .assertNoTimeout()      // Check if no Timeout
                .awaitTerminalEvent();  // Await the stream terminated before continue

        // Get list of restaurants fetched
        PlaceDetails newsFetched = testObserver.values().get(0);

        // Verify if status: "OK"
        assertThat("The status of the Stream was read correctly", newsFetched.getStatus().equals("OK"));

        // Verify the name of the Place
        assertThat("The name of the Restaurant was read correctly", newsFetched.getResult().getName().equals("Royal Pizza"));

        // Verify Place Id Value
        assertThat("The Place Id of the Restaurant was read correctly", newsFetched.getResult().getPlaceId().equals("ChIJ28LrjZwQ3UcRf_UQkipLNyc"));

        // Verify scope Value
        assertThat("The Scope of the Restaurant was read correctly", newsFetched.getResult().getScope().equals("GOOGLE"));
    }

    @Test
    public void fetchListRestaurantDetails_I_Test() throws Exception {
        Log.d(TAG, "fetchListRestaurantDetails_I_Test: ");

        Context context = InstrumentationRegistry.getTargetContext();

        Location location = new Location("");
        location.setLatitude(50.4830);
        location.setLongitude(2.48099);

        String key = BuildConfig.google_maps_api;
        String radius = "600";

        // Get the stream
        Observable<List<Restaurant>> listRestaurantDetails
                = GooglePlaceStreams.streamFetchListRestaurantDetails(context,location, radius);
        // Create a restaurant TestObserver
        TestObserver<List<Restaurant>> testObserver = new TestObserver<>();
        // Launch observable
        listRestaurantDetails.subscribeWith(testObserver)
                .assertNoErrors()       // Check if no errors
                .assertNoTimeout()      // Check if no Timeout
                .awaitTerminalEvent();  // Await the stream terminated before continue

        // Get list of restaurants fetched
        List<Restaurant> newsFetched = testObserver.values().get(0);

        // Verify Places Id
        assertThat("The First Place ID of the List of the Restaurants was read correctly", newsFetched.get(0).getIdentifier().equals("ChIJ28LrjZwQ3UcRf_UQkipLNyc"));
        assertThat("The Second Place ID of the List of the restaurants was read correctly", newsFetched.get(1).getIdentifier().equals("ChIJZ7eTV3Ua3UcRU8Fzi4HfXG0"));
        assertThat("The  Fifth Place ID of the List of the restaurants was read correctly", newsFetched.get(4).getIdentifier().equals("ChIJZer74J8Q3UcR_A7s76dPR3A"));

        // Verify the name of the Place
        assertThat("The name of the Restaurant was read correctly", newsFetched.get(0).getName().equals("Royal Pizza"));
        assertThat("The name of the Restaurant was read correctly", newsFetched.get(1).getName().equals("La Mag Pizz"));
        assertThat("The name of the Restaurant was read correctly", newsFetched.get(4).getName().equals("Friterie Chantal"));
    }
}
