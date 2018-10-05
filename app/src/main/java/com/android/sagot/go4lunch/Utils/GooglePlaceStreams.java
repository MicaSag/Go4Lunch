package com.android.sagot.go4lunch.Utils;

import android.util.Log;

import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceDetails.PlaceDetails;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceNearBySearch.PlaceNearBySearch;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceNearBySearch.PlaceNearBySearchResult;
import com.android.sagot.go4lunch.Models.RestaurantDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class GooglePlaceStreams {

    private static final String TAG = GooglePlaceStreams.class.getSimpleName();


    // Google Place NearBySearch STREAM
    public static Observable<PlaceNearBySearch> streamFetchPlacesNearBySearch(Map<String,String> query){
        GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);
        return googlePlaceService.getPlaceNearBySearch(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }


    // Google Place Details STREAM
    public static Observable<PlaceDetails> streamFetchPlaceDetails(Map<String,String> query){
        GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);
        return googlePlaceService.getPlaceDetails(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<List<PlaceDetails>> streamFetchListPlacesNearBySearchId(Map<String,String> query){
        return streamFetchPlacesNearBySearch(query)
                .flatMap(new Function<PlaceNearBySearch, Observable<List<PlaceDetails>>>() {
                    @Override
                    public Observable<List<PlaceDetails>> apply(PlaceNearBySearch placeNearBySearch) throws Exception {
                        //List of restaurants Id found
                       // List<String> listPlacesNearBySearchId = new ArrayList<>();

                       //Observable<List<String>> aze = Observable.fromArray(listPlacesNearBySearchId);

                        Log.d(TAG, "apply: placeNearBySearch = "+placeNearBySearch);

                        List<PlaceDetails> listPlacesDetails = new ArrayList<>();

                        Observable<List<PlaceDetails>> observableListPlacesDetails = Observable.fromArray(listPlacesDetails);


                        Disposable dispo = null;

                        if (placeNearBySearch.getResults().size() != 0) {

                            // Create filters
                            Map<String, String> query2 = new HashMap<>();

                            // --> Add Criteria <--
                            String key = "AIzaSyC8l-LPDTEqpJxWbJ-VbUgdUoj8TdXlcK4";

                            //Here we recover only the elements of the query that interests us
                            for (PlaceNearBySearchResult results : placeNearBySearch.getResults()) {
                                Log.d(TAG, "apply: result = "+results);
                                query2.clear();
                                query2.put("key", key);
                                query2.put("placeid", results.getPlaceId());
                                for (Map.Entry entry : query2.entrySet()) {
                                    Log.d(TAG, "apply: Map query2 = "+entry.getKey() + ", " + entry.getValue());
                                }

                                // Execute the stream subscribing to Observable defined inside GooglePlaceStreams
                                // Declare Subscription
                                dispo = streamFetchPlaceDetails(query2)
                                        .subscribeWith(new DisposableObserver<PlaceDetails>() {
                                            @Override
                                            public void onNext(PlaceDetails placeDetails) {
                                                Log.d(TAG, "onNext: ");
                                                Log.d(TAG, "onNext: placeDetails.Name = "+placeDetails.getResult().getName());
                                                // Analyze the answer
                                                listPlacesDetails.add(placeDetails);

                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                // Display a toast message
                                                //updateUIWhenErrorHTTPRequest();
                                                Log.d(TAG, "onError: ");
                                            }

                                            @Override
                                            public void onComplete() {
                                                Log.d(TAG, "On Complete !!");
                                            }
                                        });
                            }
                        }
                        //if (dispo != null && !dispo.isDisposed()) dispo.dispose();
                        return observableListPlacesDetails;

                    }
                });


       /* public static Observable<List<PlaceDetails>> streamFetchListPlacesDeails(Map<String,String> query){
            return streamFetchListPlacesNearBySearchId(query)
                .flatMap(new Function<List<String>, Observable<List<PlaceDetails>>>() {
                    @Override
                    public Observable<List<PlaceDetails>> apply(List<String> strings) throws Exception {

                        List<PlaceDetails> listPlacesDetails = new ArrayList<>();

                        Observable<List<PlaceDetails>> observableListPlacesDetails = Observable.fromArray(listPlacesDetails);

                        // Create filters
                        Map<String, String> query2 = new HashMap<>();

                        // --> Add Criteria <--
                        String key = "AIzaSyC8l-LPDTEqpJxWbJ-VbUgdUoj8TdXlcK4";

                        //Here we recover only the elements of the query that interests us
                        for (String id : strings) {
                            query2.clear();
                            query2.put("key", key);
                            query2.put("placeid", id);

                            // Execute the stream subscribing to Observable defined inside GooglePlaceStreams
                            // Declare Subscription
                            Disposable dispo = streamFetchPlaceDetails(query2)
                                    .subscribeWith(new DisposableObserver<PlaceDetails>() {
                                        @Override
                                        public void onNext(PlaceDetails placeDetails) {
                                            Log.d(TAG, "onNext: ");
                                            // Analyze the answer
                                            listPlacesDetails.add(placeDetails);

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            // Display a toast message
                                            //updateUIWhenErrorHTTPRequest();
                                            Log.d(TAG, "onError: ");
                                        }

                                        @Override
                                        public void onComplete() {
                                            Log.d(TAG, "On Complete !!");
                                        }
                                    });
                        }
                        return observableListPlacesDetails;

                    }
                });*/
    }
    // Google Place Photo STREAM
    /*public static Observable<GooglePlacePhoto> streamFetchPlacePhoto(String apiKey, Map<String,String> filters){
        GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);
        return googlePlaceService.getPlacePhoto(apiKey,filters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }*/
}
