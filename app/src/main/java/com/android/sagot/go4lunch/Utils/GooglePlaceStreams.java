package com.android.sagot.go4lunch.Utils;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.sagot.go4lunch.Models.GooglePlaceStreams.Common.Period;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceDetails.PlaceDetails;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceDetails.PlaceDetailsResult;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceNearBySearch.PlaceNearBySearch;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceNearBySearch.PlaceNearBySearchResult;
import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.android.sagot.go4lunch.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class GooglePlaceStreams {

    private static final String TAG = GooglePlaceStreams.class.getSimpleName();

    // Google Place NearBySearch STREAM
    public static Observable<PlaceNearBySearch> streamFetchRestaurantsNearBySearch(String location, String radiusSearch){
        Log.d(TAG, "streamFetchRestaurantsNearBySearch() called with: location = [" + location + "], radiusSearch = [" + radiusSearch + "]");

        GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);

        return googlePlaceService.getPlaceNearBySearch(location,radiusSearch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    // Return a PlaceId List
    public static Observable<List<String>> streamFetchListRestaurantId(String location, String radiusSearch){
        Log.d(TAG, "streamFetchListRestaurantId: ");

        return streamFetchRestaurantsNearBySearch(location, radiusSearch)
                .concatMap((Function<PlaceNearBySearch, Observable<List<String>>>) placeNearBySearch -> {

                    //List of restaurants Id found
                    List<String> listPlaceIdNearBySearch = new ArrayList<>();

                    Observable<List<String>> listPlacesId = Observable.fromArray(listPlaceIdNearBySearch);

                    if (placeNearBySearch.getResults().size() != 0) {
                        for (PlaceNearBySearchResult result : placeNearBySearch.getResults()) {
                            Log.d(TAG, "streamFetchListRestaurantId: placeId = " + result.getPlaceId());
                            listPlaceIdNearBySearch.add(result.getPlaceId());
                        }
                    } else
                        Log.d(TAG, "streamFetchListRestaurantId: placeNearBySearch.getResults().size() = null");
                    //Observable from the restaurant Id list found
                    return listPlacesId;
                });
    }

    // Google Place Details STREAM
    public static Observable<PlaceDetails> streamFetchPlaceDetails(String placeId){
        Log.d(TAG, "streamFetchPlaceDetails: ");

        GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);
        return googlePlaceService.getPlaceDetails(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    // Return Restaurants Details List near by search
    public static Observable<List<Restaurant>> streamFetchListRestaurantDetails(Context context, Location location, String radiusSearch) {
        Log.d(TAG, "streamFetchListRestaurantDetails: ");

        return streamFetchListRestaurantId(Toolbox.locationStringFromLocation(location),radiusSearch)
                .concatMap((Function<List<String>, Observable<List<Restaurant>>>) restaurantId -> Observable.fromIterable(restaurantId)
                        .concatMap((Function<String, Observable<Restaurant>>) restaurantId1 -> {

                            Observable<PlaceDetails> observablePlaceDetails = streamFetchPlaceDetails(restaurantId1);

                            return observablePlaceDetails
                                    .concatMap((Function<PlaceDetails, Observable<Restaurant>>) placeDetails -> {

                                        // Creating a New RestaurantDetails Restaurant Variable
                                        Restaurant restaurant = new Restaurant();

                                        final PlaceDetailsResult result = placeDetails.getResult();

                                        Log.d(TAG, "streamFetchListRestaurantDetails:      Place Id    = " + result.getPlaceId());

                                        // Restaurant  ID
                                        restaurant.setIdentifier(result.getPlaceId());

                                        // Restaurant Name
                                        Log.d(TAG, "streamFetchListRestaurantDetails: STEP : Restaurant Name");
                                        restaurant.setName(result.getName());

                                        // OpeningHours
                                        Log.d(TAG, "streamFetchListRestaurantDetails: STEP : OpeningHours");
                                        Log.d(TAG, "streamFetchListRestaurantDetails: Current_date = " + Toolbox.getCurrentDay());
                                        // Restaurant is closed by default
                                        restaurant.setOpeningTime(context.getString(R.string.list_restaurant_text_1));
                                        Integer time = 0;
                                        if (result.getOpeningHours() != null) {
                                            for (Period period : result.getOpeningHours().getPeriods()) {
                                                Log.d(TAG, "streamFetchListRestaurantDetails: Period found");
                                                if (period.getClose() != null) {
                                                    Log.d(TAG, "streamFetchListRestaurantDetails: period.close Found");
                                                    if (period.getClose().getDay() > Toolbox.getCurrentDay()) {
                                                        Log.d(TAG, "streamFetchListRestaurantDetails: Day > current_date");
                                                        break;
                                                    } else if (period.getClose().getDay().equals(Toolbox.getCurrentDay())) {
                                                        Log.d(TAG, "streamFetchListRestaurantDetails: Day = current_day");
                                                        if (Integer.parseInt(period.getClose().getTime()) > time) {
                                                            Log.d(TAG, "streamFetchListRestaurantDetails: " + period.getClose().getTime() + " > " + time);
                                                            time = Integer.parseInt(period.getClose().getTime());
                                                        }
                                                    }
                                                } else {
                                                    restaurant.setOpeningTime(context.getString(R.string.notification_message_6));
                                                    break;
                                                }
                                            }
                                            Log.d(TAG, "streamFetchListRestaurantDetails: setOpeningTime = " + restaurant.getOpeningTime());
                                            Log.d(TAG, "streamFetchListRestaurantDetails: time = " + time);
                                            if (!restaurant.getOpeningTime().equals(context.getString(R.string.notification_message_6))) {
                                                Log.d(TAG, "streamFetchListRestaurantDetails: Not 24/24");
                                                if (!time.equals(0)) {
                                                    Log.d(TAG, "streamFetchListRestaurantDetails: ");
                                                    restaurant.setOpeningTime(context.getString(R.string.list_restaurant_text_3)
                                                            + Toolbox.formatTime(context,time));
                                                }
                                            }
                                        }

                                        // Restaurant Address
                                        Log.d(TAG, "streamFetchListRestaurantDetails: STEP : Address");
                                        restaurant.setAddress(result.getFormattedAddress());

                                        // Lat position
                                        Log.d(TAG, "streamFetchListRestaurantDetails: STEP : Lat");
                                        restaurant.setLat(result.getGeometry().getLocation().getLat().toString());

                                        // Lng position
                                        Log.d(TAG, "streamFetchListRestaurantDetails: STEP : Lng");
                                        restaurant.setLng(result.getGeometry().getLocation().getLng().toString());

                                        // Zero Star by default
                                        restaurant.setNbrLikes(0);

                                        // Zero participant by default
                                        restaurant.setNbrParticipants(0);

                                        // Photo URL
                                        Log.d(TAG, "streamFetchListRestaurantDetails: STEP : Photo URL");
                                        if (result.getPhotos() != null) {
                                            restaurant.setPhotoUrl(Toolbox.formatPlacePhotoUrl(GooglePlaceService.maxWidth,
                                                    result.getPhotos().get(0).getPhotoReference(),GooglePlaceService.key));
                                        } else
                                            restaurant.setPhotoUrl("http://www.bsmc.net.au/wp-content/uploads/No-image-available.jpg");

                                        // Web Site URL
                                        Log.d(TAG, "streamFetchListRestaurantDetails: STEP : Web Site");
                                        if (result.getWebsite() != null) {
                                            restaurant.setWebSiteUrl(result.getWebsite());
                                        }

                                        // Phone Number
                                        Log.d(TAG, "streamFetchListRestaurantDetails: STEP : Phone Number");
                                        if (result.getFormattedPhoneNumber() != null)
                                            restaurant.setPhone(result.getFormattedPhoneNumber());

                                        Log.d(TAG, "streamFetchListRestaurantDetails: ***************************************");
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      Place Id    = " + restaurant.getIdentifier());
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      Name        = " + restaurant.getName());
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      Address     = " + restaurant.getAddress());
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      OpeningTime = " + restaurant.getOpeningTime());
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      Lat         = " + restaurant.getLat());
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      Lng         = " + restaurant.getLng());
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      Distance    = " + restaurant.getDistance());
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      web site    = " + restaurant.getWebSiteUrl());

                                        return Observable.just(restaurant);
                                    });
                        })
                        .toList()
                        .toObservable());
    }
}
