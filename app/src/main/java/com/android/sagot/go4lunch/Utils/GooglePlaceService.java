package com.android.sagot.go4lunch.Utils;

import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceDetails.PlaceDetails;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceNearBySearch.PlaceNearBySearch;
import com.google.gson.GsonBuilder;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface GooglePlaceService {

    // Place NearBySearch
    @GET("nearbysearch/json")
    Observable<PlaceNearBySearch> getPlaceNearBySearch(@QueryMap Map<String,String> query);

    // Place Details
    @GET("details/json")
    Observable<PlaceDetails> getPlaceDetails(@QueryMap Map<String,String> query);

    // Place Photo
    //@GET("/photo")
    //Observable<PlacePhoto> getPlacePhoto(@Query("api-key") String apiKey,
    //                                           @QueryMap Map<String, String> filters);


    // Use excludeFieldsWithoutExposeAnnotation() for ignore some fields
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create(
                    new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

}


