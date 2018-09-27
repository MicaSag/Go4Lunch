package com.android.sagot.go4lunch.Utils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GooglePlaceStreams {

    // Google Place NearBySearch STREAM
    /*public static Observable<GooglePlaceNearBySearch> streamFetchPlaceNearBySearch(String apiKey, Map<String,String> filters){
        GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);
        return googlePlaceService.getPlaceNearBySearch(apiKey,filters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }*/

    // Google Place Details STREAM
    /*public static Observable<GooglePlaceDetails> streamFetchPlaceDetails(String apiKey, Map<String,String> filters){
        GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);
        return googlePlaceService.getPlaceDetails(apiKey,filters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }*/

    // Google Place Photo STREAM
    /*public static Observable<GooglePlacePhoto> streamFetchPlacePhoto(String apiKey, Map<String,String> filters){
        GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);
        return googlePlaceService.getPlacePhoto(apiKey,filters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }*/
}
