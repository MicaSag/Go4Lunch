package com.android.sagot.go4lunch.Utils;

import android.content.Context;
import android.util.Log;

import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceDetails.PlaceDetails;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceDetails.PlaceDetailsResult;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceNearBySearch.PlaceNearBySearch;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceNearBySearch.PlaceNearBySearchResult;
import com.android.sagot.go4lunch.Models.RestaurantDetails;

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
    public static Observable<PlaceNearBySearch> streamFetchRestaurantsNearBySearch(String location){
        Log.d(TAG, "streamFetchRestaurantsNearBySearch: ");

        GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);

        return googlePlaceService.getPlaceNearBySearch(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    // Return a PlaceId List
    public static Observable<List<String>> streamFetchListRestaurantId(String location){
        Log.d(TAG, "streamFetchListRestaurantId: ");

        return streamFetchRestaurantsNearBySearch(location)
                .concatMap((Function<PlaceNearBySearch, Observable<List<String>>>) placeNearBySearch -> {

                    //List of restaurants Id found
                    List<String> listPlaceIdNearBySearch = new ArrayList<>();

                    Observable<List<String>> listPlacesId = Observable.fromArray(listPlaceIdNearBySearch);

                    if (placeNearBySearch.getResults().size() != 0) {
                        for (PlaceNearBySearchResult result : placeNearBySearch.getResults()) {
                            Log.d(TAG, "streamFetchListRestaurantId: placeId = "+result.getPlaceId());
                            listPlaceIdNearBySearch.add(result.getPlaceId());
                        }
                    } else Log.d(TAG, "streamFetchListRestaurantId: placeNearBySearch.getResults().size() = null");
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
    public static Observable<List<RestaurantDetails>> streamFetchListRestaurantDetails(String location, Context context) {
        Log.d(TAG, "streamFetchListRestaurantDetails: ");

        return streamFetchListRestaurantId(location)
                .concatMap((Function<List<String>, Observable<List<RestaurantDetails>>>) restaurantId -> Observable.fromIterable(restaurantId)
                        .concatMap((Function<String, Observable<RestaurantDetails>>) restaurantId1 -> {

                            Observable<PlaceDetails> observablePlaceDetails = streamFetchPlaceDetails(restaurantId1);

                            return observablePlaceDetails
                                    .concatMap((Function<PlaceDetails, Observable<RestaurantDetails>>) placeDetails -> {

                                        // Creating a New RestaurantDetails Restaurant Variable
                                        RestaurantDetails restaurantDetails = new RestaurantDetails();

                                        final PlaceDetailsResult result = placeDetails.getResult();
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      Place Id    = " + result.getPlaceId());

                                        // Restaurant  ID
                                        restaurantDetails.setId(result.getPlaceId());

                                        // Restaurant Name
                                        restaurantDetails.setName(result.getName());

                                        // OpeningHours
                                        if (result.getOpeningHours() != null) {
                                            if (result.getOpeningHours().getPeriods().get(0).getClose() != null) {
                                                restaurantDetails.setOpeningTime("Open until " + result.getOpeningHours().getPeriods().get(0).getClose().getTime() + "pm");
                                            }
                                        }
                                        // Restaurant Address
                                        restaurantDetails.setAddress(result.getFormattedAddress());

                                        // Lat position
                                        restaurantDetails.setLat(result.getGeometry().getLocation().getLat().toString());

                                        // Lng position
                                        restaurantDetails.setLng(result.getGeometry().getLocation().getLng().toString());

                                        // Zero Star by default
                                        restaurantDetails.setNbrStars(2);

                                        // Zero participant by default
                                        restaurantDetails.setNbrParticipants(22);

                                        // Photo URL
                                        if (result.getPhotos() != null) {
                                            restaurantDetails.setPhotoUrl("https://maps.googleapis.com/maps/api/place/photo?"
                                                    + "maxwidth=" + GooglePlaceService.maxWidth
                                                    + "&photoreference=" + result.getPhotos().get(0).getPhotoReference()
                                                    + "&key=" + GooglePlaceService.key);
                                        }

                                        // Web Site URL
                                        if (result.getWebsite() != null) {
                                            restaurantDetails.setWebSiteUrl(result.getWebsite());
                                        }

                                        // Phone Number
                                        if (result.getFormattedPhoneNumber() != null)
                                            restaurantDetails.setPhone(result.getFormattedPhoneNumber());

                                        Log.d(TAG, "streamFetchListRestaurantDetails: ***************************************");
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      Place Id    = " + restaurantDetails.getId());
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      Name        = " + restaurantDetails.getName());
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      Address     = " + restaurantDetails.getAddress());
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      OpeningTime = " + restaurantDetails.getOpeningTime());
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      Lat         = " + restaurantDetails.getLat());
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      Lng         = " + restaurantDetails.getLng());
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      web site    = " + restaurantDetails.getWebSiteUrl());

                                        return Observable.just(restaurantDetails);
                                    });
                        })
                        .toList()
                        .toObservable());
    }
}
