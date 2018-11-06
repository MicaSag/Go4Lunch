package com.android.sagot.go4lunch.api;

import com.android.sagot.go4lunch.Models.firestore.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String userName, String restaurantIdentifier, String restaurantName,
                                        List<String> listRestaurantLiked, String urlPicture) {
        User userToCreate = new User(uid, userName, restaurantIdentifier, restaurantName, listRestaurantLiked, urlPicture);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Task<QuerySnapshot> getAllUserInfos(){
        return UserHelper.getUsersCollection().get();
    }
    public static Query getAllUser(){
        return UserHelper.getUsersCollection().orderBy("userName").limit(10);
    }

    // --- UPDATE ---

    public static Task<Void> updateUserName(String userName, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("userName", userName);
    }

    public static Task<Void> updateRestaurantIdentifier(String uid, String restaurantIdentifier) {
        return UserHelper.getUsersCollection().document(uid).update("restaurantIdentifier", restaurantIdentifier);
    }

    public static Task<Void> updateRestaurantName(String uid, String restaurantName) {
        return UserHelper.getUsersCollection().document(uid).update("restaurantName", restaurantName);
    }

    public static Task<Void> updateListRestaurantLiked(String uid, List<String> listRestaurantLiked) {
        return UserHelper.getUsersCollection().document(uid).update("listRestaurantLiked", listRestaurantLiked);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}
