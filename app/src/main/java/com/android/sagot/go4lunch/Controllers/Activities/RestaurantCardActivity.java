package com.android.sagot.go4lunch.Controllers.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sagot.go4lunch.Controllers.Base.BaseActivity;
import com.android.sagot.go4lunch.Controllers.Fragments.ListWorkmatesViewFragment;
import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.android.sagot.go4lunch.Models.firestore.User;
import com.android.sagot.go4lunch.R;
import com.android.sagot.go4lunch.api.RestaurantHelper;
import com.android.sagot.go4lunch.api.UserHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**************************************************************************************************
 *
 *  ACTIVITY that displays the Restaurant
 *  -----------------------------------------
 *  IN = Restaurant Details     : RestaurantDetails
 *
 **************************************************************************************************/

public class RestaurantCardActivity extends BaseActivity {

    // For debugging Mode
    private static final String TAG = RestaurantCardActivity.class.getSimpleName();

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.activity_restaurant_coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.activity_restaurant_card_name) TextView mName;
    @BindView(R.id.activity_restaurant_card_address) TextView mAddress;
    @BindView(R.id.activity_restaurant_card_star_one) ImageView mStarOne;
    @BindView(R.id.activity_restaurant_card_star_two) ImageView mStarTwo;
    @BindView(R.id.activity_restaurant_card_star_three) ImageView mStarThree;
    @BindView(R.id.activity_restaurant_card_image) ImageView mImage;
    @BindView(R.id.activity_restaurant_web_site_button) ImageButton mWebSiteButton;
    @BindView(R.id.activity_restaurant_web_site_text) TextView mWebSiteText;
    @BindView(R.id.activity_restaurant_call_button) ImageButton mPhoneButton;
    @BindView(R.id.activity_restaurant_call_text) TextView mPhoneText;
    @BindView(R.id.activity_restaurant_like_button) ImageButton mLikeButton;
    @BindView(R.id.activity_restaurant_like_text) TextView mLikeText;
    @BindView(R.id.activity_restaurant_floating_action_button) FloatingActionButton mFloatingActionButton;

    // Create the key details restaurant
    public static final String KEY_DETAILS_RESTAURANT_CARD = "KEY_DETAILS_RESTAURANT_CARD";

    // Data creation returned to the caller
    public static final String RETURN_SELECTED_RESTAURANT_CARD = "RETURN_SELECTED_RESTAURANT_CARD";

    // --> Data retrieved from the caller
    // Declare a RestaurantDetails
    private Restaurant mRestaurant;
    // Declare Current User data
    private User mUser;

    // Declaring a Glide object
    private RequestManager mGlide;

    // ---------------------------------------------------------------------------------------------
    //                                DECLARATION BASE METHODS
    // ---------------------------------------------------------------------------------------------
    // BASE METHOD Implementation
    // Get the activity layout
    // CALLED BY BASE METHOD 'onCreate(...)'
    @Override
    protected int getActivityLayout() {
        return R.layout.activity_restaurant_card;
    }

    // BASE METHOD Implementation
    // Get the coordinator layout
    // CALLED BY BASE METHOD
    @Override
    protected View getCoordinatorLayout() {
        return mCoordinatorLayout;
    }
    // ---------------------------------------------------------------------------------------------
    //                                    ENTRY POINT
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // update Floating Action Button
        updateFloatingActionButton();

        // Obtain RestaurantDetails
        getRestaurantDetails();

        // Enables listening of the number of stars
        listenNbrLikes();

        // Enables listening of the user Like
        listenUserLike();

        // Update UI
        updateUI();
    }
    // ---------------------------------------------------------------------------------------------
    //                                       METHODS
    // ---------------------------------------------------------------------------------------------
    /**
     * Method for recovering data sent by the caller
     */
    private void getRestaurantDetails(){
        Log.d(TAG, "getRestaurantDetails: ");

        // Recover intent of the Caller
        Intent intent = getIntent();

        // ==> Retrieves the details of the restaurant sent by Caller
        String restaurant = intent.getStringExtra(KEY_DETAILS_RESTAURANT_CARD);
        mRestaurant = new Gson().fromJson(restaurant,Restaurant.class);
        Log.d(TAG, "getRestaurantDetails: mRestaurant nb Like = "+mRestaurant.getNbrLikes());
    }
    /**
     * Enables listening of the number of stars
     */
    public void listenNbrLikes() {
        Log.d(TAG, "listenNbrLikes: ");
        RestaurantHelper
                .getRestaurantsCollection()
                .document(mRestaurant.getIdentifier())
                .addSnapshotListener((restaurant, e) -> {
                    if (e != null) {
                        Log.d(TAG, "fireStoreListener.onEvent: Listen failed: " + e);
                        return;
                    }
                    Log.d(TAG, "listenNbrLikes.onEvent: nbrLikes = " + restaurant.get("nbrLikes").toString());
                    mRestaurant.setNbrLikes((long) restaurant.get("nbrLikes"));
                    RestaurantCardActivity.this.updateUI_Stars();
                });
    }
    /**
     * Enables listening of the user like
     */
    public void listenUserLike() {
        Log.d(TAG, "listenUserLike: ");

        UserHelper
                .getUsersCollection()
                .document(getCurrentUser().getUid())
                .addSnapshotListener((user, e) -> {
                    if (e != null) {
                        Log.d(TAG, "fireStoreListener.onEvent: Listen failed: " + e);
                        return;
                    }
                    mLikeButton.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    User currentUser = user.toObject(User.class);
                    Map<String, String> restaurantLiked = currentUser.getListRestaurantLiked();
                    if (restaurantLiked != null) {
                        if (restaurantLiked.containsKey(mRestaurant.getIdentifier())) {
                            // Remove the like from the restaurant on the user
                            restaurantLiked.remove(mRestaurant.getIdentifier());
                            mLikeButton.setImageResource(R.drawable.baseline_star_black_24);
                        } else {
                            mLikeButton.setImageResource(R.drawable.baseline_star_border_black_24);
                        }
                    }else{
                        mLikeButton.setImageResource(R.drawable.baseline_star_border_black_24);
                    }
                });
    }
    // ---------------------------------------------------------------------------------------------
    //                                       ACTIONS
    // ---------------------------------------------------------------------------------------------
    //*****************************
    // PUSH Floating Action Button
    //*****************************
    @OnClick(R.id.activity_restaurant_floating_action_button)
    public void submitFloatingActionButton(View view){
        Log.d(TAG, "submitFloatingActionButton: ");

        // Disabling the button until the data is updated in FireBase
        mFloatingActionButton.setEnabled(false);

        // Get additional data from FireStore : restaurantIdentifier of the User choice
        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                String restaurantIdentifier = currentUser.getRestaurantIdentifier();
                Log.d(TAG, "onSuccess: 1 restaurantIdentifier = "+restaurantIdentifier);

                int nbrParticipants;
                // If the user has already chosen restaurant to eat
                if (restaurantIdentifier != null) {

                    // if the restaurant already chosen by the user is the same as the restaurant listing
                    if (restaurantIdentifier.equals(mRestaurant.getIdentifier())) {

                        // We cancel the user's choice
                        UserHelper.updateRestaurantIdentifier(RestaurantCardActivity.this.getCurrentUser().getUid(), null);
                        UserHelper.updateRestaurantName(RestaurantCardActivity.this.getCurrentUser().getUid(), null);
                        mFloatingActionButton.setImageResource(R.drawable.no_verified_x96);
                        nbrParticipants = -1;
                    } else {

                        // We accept the choice of the user
                        UserHelper.updateRestaurantIdentifier(RestaurantCardActivity.this.getCurrentUser().getUid(), mRestaurant.getIdentifier());
                        UserHelper.updateRestaurantName(RestaurantCardActivity.this.getCurrentUser().getUid(), mRestaurant.getName());
                        mFloatingActionButton.setImageResource(R.drawable.verified_x96);
                        nbrParticipants = +1;
                    }
                } else {

                    // We accept the choice of the user
                    UserHelper.updateRestaurantIdentifier(RestaurantCardActivity.this.getCurrentUser().getUid(), mRestaurant.getIdentifier());
                    UserHelper.updateRestaurantName(RestaurantCardActivity.this.getCurrentUser().getUid(), mRestaurant.getName());
                    mFloatingActionButton.setImageResource(R.drawable.verified_x96);
                    nbrParticipants = +1;
                }
                Log.d(TAG, "onSuccess: 2 restaurantIdentifier = "+restaurantIdentifier);
                if (nbrParticipants > 0) {
                    if (restaurantIdentifier != null ) {
                        RestaurantHelper.getRestaurant(restaurantIdentifier).addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {
                                Log.d(TAG, "getRestaurant.onComplete: ");
                                Restaurant restaurant = task.getResult().toObject(Restaurant.class);
                                RestaurantHelper.updateRestaurantNbrParticipants(restaurantIdentifier
                                        , restaurant.getNbrParticipants() - 1).addOnCompleteListener(task1 -> {

                                            if (task1.isSuccessful()) {
                                                RestaurantHelper.getRestaurant(mRestaurant.getIdentifier()).addOnCompleteListener(task11 -> {

                                                    if (task11.isSuccessful()) {
                                                        Log.d(TAG, "getRestaurant.onComplete: ");
                                                        Restaurant restaurant1 = task11.getResult().toObject(Restaurant.class);
                                                        RestaurantHelper.updateRestaurantNbrParticipants(mRestaurant.getIdentifier()
                                                                , restaurant1.getNbrParticipants() + nbrParticipants)
                                                                .addOnCompleteListener(task111 -> {

                                                                    if (task111.isSuccessful()) {
                                                                        Log.d(TAG, "updateRestaurant.onComplete: ");

                                                                        // Display New Workmates List
                                                                        RestaurantCardActivity.this.displayWorkmatesList();

                                                                        // Reactivate the button once the updates in FireBase are complete
                                                                        mFloatingActionButton.setEnabled(true);

                                                                    } else {
                                                                        Exception e = task111.getException();
                                                                    }
                                                                });
                                                    } else {
                                                        Exception e = task11.getException();
                                                    }
                                                });
                                            } else {
                                                Exception e = task1.getException();
                                            }
                                        });
                            } else {
                                Exception e = task.getException();
                            }
                        });
                    }else {
                        RestaurantHelper.getRestaurant(mRestaurant.getIdentifier()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()) {
                                    Log.d(TAG, "getRestaurant.onComplete: ");
                                    Restaurant restaurant = task.getResult().toObject(Restaurant.class);
                                    RestaurantHelper.updateRestaurantNbrParticipants(mRestaurant.getIdentifier()
                                            , restaurant.getNbrParticipants() + nbrParticipants)
                                            .addOnCompleteListener(task12 -> {

                                                if (task12.isSuccessful()) {
                                                    Log.d(TAG, "updateRestaurant.onComplete: ");

                                                    // Display New Workmates List
                                                    RestaurantCardActivity.this.displayWorkmatesList();

                                                    // Reactivate the button once the updates in FireBase are complete
                                                    mFloatingActionButton.setEnabled(true);

                                                } else { Exception e = task12.getException(); }
                                            });
                                } else { Exception e = task.getException(); }
                            }
                        });
                    }
                }else {
                    RestaurantHelper.getRestaurant(mRestaurant.getIdentifier()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                Log.d(TAG, "getRestaurant.onComplete: ");
                                Restaurant restaurant = task.getResult().toObject(Restaurant.class);
                                RestaurantHelper.updateRestaurantNbrParticipants(mRestaurant.getIdentifier()
                                        , restaurant.getNbrParticipants() + nbrParticipants)
                                        .addOnCompleteListener(task13 -> {

                                            if (task13.isSuccessful()) {
                                                Log.d(TAG, "updateRestaurant.onComplete: ");

                                                // Display New Workmates List
                                                RestaurantCardActivity.this.displayWorkmatesList();

                                                // Reactivate the button once the updates in FireBase are complete
                                                mFloatingActionButton.setEnabled(true);

                                            } else { Exception e = task13.getException(); }
                                        });
                            } else { Exception e = task.getException(); }
                        }
                    });
                }
            }
        });
    }
    //**************
    // CALL Button
    //**************
    @OnClick(R.id.activity_restaurant_call_button)
    // Ask permission when accessing to this listener
    @AfterPermissionGranted(RC_CALL_PHONE_PERMISSION)
    public void submitCallButton(View view){
        Log.d(TAG, "submitCallButton: ");

        if (!EasyPermissions.hasPermissions(this, PERMISSION_CALL_PHONE)) {
            EasyPermissions.requestPermissions(this, "Permission CALL_PHONE", RC_CALL_PHONE_PERMISSION, PERMISSION_CALL_PHONE);
            return;
        }
        try {
            Log.d(TAG, "submitCallButton: Permission GRANTED :-)");
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            String phoneNumber = "tel:"+mRestaurant.getPhone().replaceAll(" ","");
            Log.d(TAG, "submitCallButton: phoneNumber = "+phoneNumber);
            callIntent.setData(Uri.parse(phoneNumber));
            startActivity(callIntent);
        }catch (SecurityException e){
            Log.d(TAG, "submitCallButton: EXCEPTION ALERTE ALERTE ALERTE");
        }
    }
    //**************
    // LIKE Button
    //**************
    @OnClick(R.id.activity_restaurant_like_button)
    protected void submitLikeButton(View view){
        Log.d(TAG, "submitLikeButton: ");
        Log.d(TAG, "submitLikeButton: nbrLikes = "+mRestaurant.getNbrLikes());

        // Disabling the button until the data is updated in FireBase
        mLikeButton.setEnabled(false);
        // Get additional data from FireStore : restaurantIdentifier of the User choice
        UserHelper.getUser(this.getCurrentUser().getUid())
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        User currentUser = task.getResult().toObject(User.class);
                        Map<String, String> restaurantLiked;
                        int like;
                        if (currentUser.getListRestaurantLiked() != null) {
                            restaurantLiked = currentUser.getListRestaurantLiked();
                            if (restaurantLiked.containsKey(mRestaurant.getIdentifier())) {
                                // Decreasing the number of likes by 1
                                like = -1;
                                // Remove the like from the restaurant on the user
                                restaurantLiked.remove(mRestaurant.getIdentifier());

                            } else {
                                // Increasing the number of likes by 1
                                like = 1;
                                // Add the like from the restaurant on the user
                                restaurantLiked.put(mRestaurant.getIdentifier(), "Liked");
                            }
                        }else{
                            restaurantLiked = new HashMap<>();
                            // Increasing the number of likes by 1
                            like = 1;
                            // Add the like from the restaurant on the user
                            restaurantLiked.put(mRestaurant.getIdentifier(), "Liked");
                        }
                        // Update the like of the restaurant on the user
                        UserHelper.updateListRestaurantLiked(getCurrentUser().getUid(), restaurantLiked)
                                .addOnCompleteListener(task1 -> {

                                    if (task1.isSuccessful()) {
                                        // Update of the restaurant's number of likes only
                                        // after the restaurant's like has been registered on the user
                                        mRestaurant.setNbrLikes(mRestaurant.getNbrLikes() + like);
                                        RestaurantHelper.updateRestaurantNbrLikes(mRestaurant.getIdentifier(), mRestaurant.getNbrLikes())
                                                .addOnCompleteListener(task11 -> {

                                                    if (task11.isSuccessful()) {
                                                        // Reactivate the button once the updates in FireBase are complete
                                                        mLikeButton.setEnabled(true);
                                                    }
                                                }).addOnFailureListener(this.onFailureListener());
                                    }
                                }).addOnFailureListener(this.onFailureListener());
                    }
                }).addOnFailureListener(this.onFailureListener());
    }

    //*****************
    // WEB SITE Button
    //*****************
    @OnClick(R.id.activity_restaurant_web_site_button)
    protected void submitWebSiteButton(View view){
        Log.d(TAG, "submitWebSiteButton: ");

        // Launch WebViewActivity
        // Param : Url to display
        Intent myIntent = new Intent(this, WebViewActivity.class);
        myIntent.putExtra(WebViewActivity.KEY_RESTAURANT_WEB_SITE_URL,mRestaurant.getWebSiteUrl());
        this.startActivity(myIntent);
    }
    // ---------------------------------------------------------------------------------------------
    //                                   REQUEST PERMISSION
    // ---------------------------------------------------------------------------------------------
    /**
     * Method to ask the user for permission to make calls
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    // ---------------------------------------------------------------------------------------------
    //                                      UPDATE UI
    // ---------------------------------------------------------------------------------------------
    /**
     * Method that changes the learning of the FAB according to the user's current choice
     */
    private void updateFloatingActionButton(){
        Log.d(TAG, "updateFloatingActionButton: ");

        // Disabling the button until the data is updated in FireBase
        mFloatingActionButton.setEnabled(false);

        // Get additional data from FireStore : restaurantIdentifier of the current user
        UserHelper.getUser(this.getCurrentUser().getUid())
                .addOnSuccessListener(documentSnapshot -> {
                    this.mUser = documentSnapshot.toObject(User.class);

                    if (mUser.getRestaurantIdentifier() != null) {
                        if (mUser.getRestaurantIdentifier().equals(mRestaurant.getIdentifier()))
                            mFloatingActionButton.setImageResource(R.drawable.verified_x96);
                        else mFloatingActionButton.setImageResource(R.drawable.no_verified_x96);
                    }
                    // Disabling the button until the data is updated in FireBase
                    mFloatingActionButton.setEnabled(true);
                });
    }
    /**
     * Method for updating the UI
     */
    private void updateUI(){
        Log.d(TAG, "updateUI: ");

        // Display Name of the restaurant
        this.mName.setText(mRestaurant.getName());

        // Display Address of the Restaurant
        this.mAddress.setText(mRestaurant.getAddress());

        // Display WebSite Button
        if (mRestaurant.getWebSiteUrl() == null) {
            mWebSiteButton.setVisibility(View.INVISIBLE);
            mWebSiteText.setVisibility(View.INVISIBLE);
        }

        // Display Phone Button
        if (mRestaurant.getPhone() == null) {
            mPhoneButton.setVisibility(View.INVISIBLE);
            mPhoneText.setVisibility(View.INVISIBLE);
        }

        // Display Photo of the restaurant
        if (mRestaurant.getPhotoUrl() != null) {
            mGlide = Glide.with(this);
            mGlide.load(mRestaurant.getPhotoUrl()).into(this.mImage);
        }

        // Display Workmates List
        displayWorkmatesList();
    }

    private void updateUI_Stars(){
        Log.d(TAG, "updateUI_Stars: ");
        // Display Stars
        Log.d(TAG, "updateUI_Stars: mRestaurant.getNbrLikes() = "+mRestaurant.getNbrLikes());
        if (mRestaurant.getNbrLikes() < 3) mStarThree.setVisibility(View.INVISIBLE);
        else mStarThree.setVisibility(View.VISIBLE);
        if (mRestaurant.getNbrLikes() < 2) mStarTwo.setVisibility(View.INVISIBLE);
        else mStarTwo.setVisibility(View.VISIBLE);
        if (mRestaurant.getNbrLikes() < 1) mStarOne.setVisibility(View.INVISIBLE);
        else mStarOne.setVisibility(View.VISIBLE);
    }

    /**
     * Method who uses The FragmentManager(Support) for displaying Workmates List
     */
    private void displayWorkmatesList(){
        Log.d(TAG, "displayWorkmatesList: ");

        // 1_Get the current fragment
        ListWorkmatesViewFragment currentListWorkmatesViewFragment
                = (ListWorkmatesViewFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.activity_restaurant_workmates_list);

        // 2_Create new ListWorkmatesViewFragment and send the restaurant identifier in parameter
        String restaurantIdentifier = mRestaurant.getIdentifier();
        ListWorkmatesViewFragment listWorkmatesViewFragment 
                = ListWorkmatesViewFragment.newInstance(restaurantIdentifier);

        // 3_Put the new fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_restaurant_workmates_list,listWorkmatesViewFragment)
                .commit();

        // If the fragment exist
        if (currentListWorkmatesViewFragment != null) {
            Log.d(TAG, "displayWorkmatesList: Fragment Exist");

            // 4_Destroys the old fragment
            getSupportFragmentManager().beginTransaction()
                    .remove(currentListWorkmatesViewFragment)
                    .commit();
        }
    }
    @Override
    protected void onDestroy() {
        Intent intent = new Intent();
        intent.putExtra(RETURN_SELECTED_RESTAURANT_CARD, true);
        setResult(RESULT_OK, intent);

        super.onDestroy();
    }
}
