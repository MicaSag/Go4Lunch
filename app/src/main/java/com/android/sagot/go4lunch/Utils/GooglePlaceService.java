package com.android.sagot.go4lunch.Utils;

import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceDetails.PlaceDetails;
import com.android.sagot.go4lunch.Models.GooglePlaceStreams.PlaceNearBySearch.PlaceNearBySearch;
import com.google.gson.GsonBuilder;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlaceService {

    // Constant parameters
    String key      = "AIzaSyC8l-LPDTEqpJxWbJ-VbUgdUoj8TdXlcK4";
    String type     = "restaurant";
    String radius   = "70";
    String maxWidth = "2304";

    // Place NearBySearch
    @GET("nearbysearch/json?key="+key+"&radius="+radius+"&type="+type)
    Observable<PlaceNearBySearch> getPlaceNearBySearch(@Query("location") String location);

    // Place Details
    @GET("details/json?key="+key)
    Observable<PlaceDetails> getPlaceDetails(@Query("placeid") String placeId);

    // Use excludeFieldsWithoutExposeAnnotation() for ignore some fields
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create(
                    new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}


