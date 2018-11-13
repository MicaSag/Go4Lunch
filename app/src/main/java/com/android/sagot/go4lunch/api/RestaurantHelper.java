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
                    restaurant.getNbrParticipants(), restaurant.getNbrLikes(), restaurant.getPhotoUrl(),
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
        return RestaurantHelper.getRestaurantsCollection().document(identifier).update("name", name);
    }

    public static Task<Void> updateRestaurantAddress(String identifier, String address) {
        return RestaurantHelper.getRestaurantsCollection().document(identifier).update("address", address);
    }

    public static Task<Void> updateRestaurantOpeningTime(String identifier, String openingTime) {
        return RestaurantHelper.getRestaurantsCollection().document(identifier).update("openingTime", openingTime);
    }

    public static Task<Void> updateRestaurantDistance(String identifier, String distance) {
        return RestaurantHelper.getRestaurantsCollection().document(identifier).update("distance", distance);
    }

    public static Task<Void> updateRestaurantNbrParticipants(String identifier, long nbrParticipants) {
        return RestaurantHelper.getRestaurantsCollection().document(identifier).update("nbrParticipants", nbrParticipants);
    }

    public static Task<Void> updateRestaurantNbrLikes(String identifier, long nbrLikes) {
        return RestaurantHelper.getRestaurantsCollection().document(identifier).update("nbrLikes", nbrLikes);
    }

    public static Task<Void> updateRestaurantPhotoUrl(String identifier, String photoUrl) {
        return RestaurantHelper.getRestaurantsCollection().document(identifier).update("photoUrl", photoUrl);
    }

    public static Task<Void> updateWebSiteUrl(String identifier, String webSiteUrl) {
        return RestaurantHelper.getRestaurantsCollection().document(identifier).update("webSiteUrl", webSiteUrl);
    }

    public static Task<Void> updateType(String identifier, String type) {
        return RestaurantHelper.getRestaurantsCollection().document(identifier).update("type", type);
    }

    public static Task<Void> updateLat(String identifier, String lat) {
        return RestaurantHelper.getRestaurantsCollection().document(identifier).update("lat", lat);
    }

    public static Task<Void> updateLng(String identifier, String lng) {
        return RestaurantHelper.getRestaurantsCollection().document(identifier).update("lng", lng);
    }

    public static Task<Void> updatePhone(String identifier, String phone) {
        return RestaurantHelper.getRestaurantsCollection().document(identifier).update("phone", phone);
    }

    // --- DELETE ---

    public static Task<Void> deleteRestaurant(String identifier) {
        return RestaurantHelper.getRestaurantsCollection().document(identifier).delete();
    }
}
