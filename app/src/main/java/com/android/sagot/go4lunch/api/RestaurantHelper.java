package com.android.sagot.go4lunch.api;

import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class RestaurantHelper {

    private static final String COLLECTION_NAME = "restaurants";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createRestaurant(Restaurant restaurant) {

        Restaurant restaurantToCreate = new Restaurant( restaurant.getIdentifier(), restaurant.getName(),
                    restaurant.getAddress(), restaurant.getOpeningTime(), restaurant.getDistance(),
                    restaurant.getNbrParticipants(), restaurant.getNbrStars(), restaurant.getPhotoUrl(),
                    restaurant.getWebSiteUrl(), restaurant.getType(), restaurant.getLat(), restaurant.getLng(),
                    restaurant.getPhone());

        return RestaurantHelper.getRestaurantsCollection().document(restaurant.getIdentifier()).set(restaurantToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getRestaurant(String identifier){
        return RestaurantHelper.getRestaurantsCollection().document(identifier).get();
    }

    public static Task<QuerySnapshot> getAllRestaurantInfos(){
        return RestaurantHelper.getRestaurantsCollection().get();
    }
    public static Query getAllRestaurant(){
        return RestaurantHelper.getRestaurantsCollection().orderBy("name").limit(20);
    }

    // --- UPDATE ---

    public static Task<Void> updateRestaurantName(String identifier, String name) {
        return UserHelper.getUsersCollection().document(identifier).update("name", name);
    }

    public static Task<Void> updateRestaurantAddress(String identifier, String address) {
        return RestaurantHelper.getRestaurantsCollection().document(identifier).update("address", address);
    }

    public static Task<Void> updateRestaurantOpeningTime(String identifier, String openingTime) {
        return UserHelper.getUsersCollection().document(identifier).update("openingTime", openingTime);
    }

    public static Task<Void> updateRestaurantDistance(String identifier, String distance) {
        return UserHelper.getUsersCollection().document(identifier).update("distance", distance);
    }

    public static Task<Void> updateRestaurantNbrParticipants(String identifier, String nbrParticipants) {
        return UserHelper.getUsersCollection().document(identifier).update("nbrParticipants", nbrParticipants);
    }

    public static Task<Void> updateRestaurantNbrStars(String identifier, String nbrStars) {
        return UserHelper.getUsersCollection().document(identifier).update("nbrStars", nbrStars);
    }

    public static Task<Void> updateRestaurantPhotoUrl(String identifier, String photoUrl) {
        return UserHelper.getUsersCollection().document(identifier).update("photoUrl", photoUrl);
    }

    public static Task<Void> updateWebSiteUrl(String identifier, String webSiteUrl) {
        return UserHelper.getUsersCollection().document(identifier).update("webSiteUrl", webSiteUrl);
    }

    public static Task<Void> updateType(String identifier, String type) {
        return UserHelper.getUsersCollection().document(identifier).update("type", type);
    }

    public static Task<Void> updateLat(String identifier, String lat) {
        return UserHelper.getUsersCollection().document(identifier).update("lat", lat);
    }

    public static Task<Void> updateLng(String identifier, String lng) {
        return UserHelper.getUsersCollection().document(identifier).update("lng", lng);
    }

    public static Task<Void> updatePhone(String identifier, String phone) {
        return UserHelper.getUsersCollection().document(identifier).update("phone", phone);
    }

    // --- DELETE ---

    public static Task<Void> deleteRestaurant(String identifier) {
        return RestaurantHelper.getRestaurantsCollection().document(identifier).delete();
    }
}
