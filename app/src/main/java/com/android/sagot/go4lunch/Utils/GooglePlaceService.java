package com.android.sagot.go4lunch.Utils;

import com.google.gson.GsonBuilder;

import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface GooglePlaceService {

    // Place NearBySearch
    //@GET("nearbysearch/json")
    //Observable<GooglePlaceNearBySearch> getPlaceNearBySearch(@Query("api-key") String apiKey,
    //                                                         @QueryMap Map<String, String> filters);

    // Place Details
    //@GET("details/json")
    //Observable<GooglePlaceDetails> getPlaceDetails(@Query("api-key") String apiKey,
    //                                               @QueryMap Map<String, String> filters);
    // Place Photo
    //@GET("/photo")
    //Observable<GooglePlacePhoto> getPlacePhoto(@Query("api-key") String apiKey,
     //                                          @QueryMap Map<String, String> filters);


    // Use excludeFieldsWithoutExposeAnnotation() for ignore some fields
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create(
                    new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

}


