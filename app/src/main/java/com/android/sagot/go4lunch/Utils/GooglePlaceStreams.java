package com.android.sagot.go4lunch.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceDetails.PlaceDetails;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceDetails.PlaceDetailsResult;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceNearBySearch.PlaceNearBySearch;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceNearBySearch.PlaceNearBySearchResult;
import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.android.sagot.go4lunch.api.RestaurantHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

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
    public static Observable<List<Restaurant>> streamFetchListRestaurantDetails(String location, Context context) {
        Log.d(TAG, "streamFetchListRestaurantDetails: ");

        return streamFetchListRestaurantId(location)
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
                                        /*if (result.getOpeningHours() != null) {
                                            if (result.getOpeningHours().getPeriods().get(0).getClose() != null) {
                                                restaurantDetails.setOpeningTime("Open until " + result.getOpeningHours().getPeriods().get(0).getClose().getTime() + "pm");
                                            }
                                        }*/
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
                                            restaurant.setPhotoUrl("https://maps.googleapis.com/maps/api/place/photo?"
                                                    + "maxwidth=" + GooglePlaceService.maxWidth
                                                    + "&photoreference=" + result.getPhotos().get(0).getPhotoReference()
                                                    + "&key=" + GooglePlaceService.key);
                                        } else restaurant.setPhotoUrl("http://www.bsmc.net.au/wp-content/uploads/No-image-available.jpg");

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
                                        Log.d(TAG, "streamFetchListRestaurantDetails:      web site    = " + restaurant.getWebSiteUrl());

                                        // Get additional data from FireStore : restaurantIdentifier
                                        RestaurantHelper.getRestaurant(restaurant.getIdentifier()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(Task<DocumentSnapshot> documentSnapshot) {

                                                if (documentSnapshot.isSuccessful()) {

                                                    Restaurant currentRestaurant = documentSnapshot.getResult().toObject(Restaurant.class);

                                                    if (currentRestaurant == null) {
                                                        Log.d(TAG, "setListRestaurantsInFireBase: currentRestaurant not exist in FireBase Database");

                                                        RestaurantHelper.createRestaurant(restaurant).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "createRestaurant failure.", e);
                                                            }
                                                        });
                                                    } else {
                                                        Log.d(TAG, "setListRestaurantsInFireBase: currentRestaurant exist in FireBase Database");
                                                    }
                                                } else {
                                                    Exception e = documentSnapshot.getException();
                                                }
                                            }
                                        });

                                        return Observable.just(restaurant);
                                    });
                        })
                        .toList()
                        .toObservable());
    }
}
