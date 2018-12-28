package com.android.sagot.go4lunch.Models;

import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.google.android.gms.maps.model.Marker;

import java.util.Comparator;

public class AdapterRestaurant {

    private Restaurant mRestaurant;
    private boolean mVisible = true;
    private Marker mMarker;


    // CONSTRUCTOR
    public AdapterRestaurant(Restaurant mRestaurant, Marker marker) {
        this.mRestaurant = mRestaurant;
        this.mMarker = marker;
    }

    // GET Methods
    public Restaurant getRestaurant() {
        return mRestaurant;
    }

    public boolean isVisible() {
        return mVisible;
    }

    public Marker getMarker() {
        return mMarker;
    }


    // PUT Methods
    public void setRestaurant(Restaurant mRestaurant) {
        this.mRestaurant = mRestaurant;
    }

    public void setVisible(boolean visible) {
        this.mVisible = visible;
    }

    public void setMarker(Marker mMarker) {
        this.mMarker = mMarker;
    }

    // SORT METHODS
    /*Comparator for sorting the list by Restaurant Name*/
    public static Comparator<AdapterRestaurant> RestaurantNameComparator = new Comparator<AdapterRestaurant>() {

        public int compare(AdapterRestaurant r1, AdapterRestaurant r2) {
            String RestaurantName1 = r1.getRestaurant().getName().toUpperCase();
            String RestaurantName2 = r2.getRestaurant().getName().toUpperCase();

            //ascending order
            return RestaurantName1.compareTo(RestaurantName2);

            //descending order
            //return RestaurantName2.compareTo(RestaurantName1);
        }};

    /*Comparator for sorting the list by Restaurant distance*/
    public static Comparator<AdapterRestaurant> RestaurantDistanceComparator = new Comparator<AdapterRestaurant>() {

        public int compare(AdapterRestaurant r1, AdapterRestaurant r2) {
            Integer RestaurantDistance1 = r1.getRestaurant().getDistance();
            Integer RestaurantDistance2 = r2.getRestaurant().getDistance();

            //ascending order
            return RestaurantDistance1.compareTo(RestaurantDistance2);

            //descending order
            //return RestaurantName2.compareTo(RestaurantName1);
        }};

    /*Comparator for sorting the list by Restaurant NbrLikes*/
    public static Comparator<AdapterRestaurant> RestaurantNbrLikesComparator = new Comparator<AdapterRestaurant>() {

        public int compare(AdapterRestaurant r1, AdapterRestaurant r2) {
            Integer RestaurantNbrLikes1 = r1.getRestaurant().getNbrLikes();
            Integer RestaurantNbrLikes2 = r2.getRestaurant().getNbrLikes();

            //ascending order
            //return RestaurantNbrLikes1.compareTo(RestaurantNbrLikes2);

            //descending order
            return RestaurantNbrLikes2.compareTo(RestaurantNbrLikes1);
        }};

    /*Comparator for sorting the list by Restaurant NbrParticipants*/
    public static Comparator<AdapterRestaurant> RestaurantNbrParticipantsComparator = new Comparator<AdapterRestaurant>() {

        public int compare(AdapterRestaurant r1, AdapterRestaurant r2) {
            Integer RestaurantNbrParticipants1 = r1.getRestaurant().getNbrParticipants();
            Integer RestaurantNbrParticipants2 = r2.getRestaurant().getNbrParticipants();

            //ascending order
            //return RestaurantNbrParticipants1.compareTo(RestaurantNbrParticipants2);

            //descending order
            return RestaurantNbrParticipants2.compareTo(RestaurantNbrParticipants1);
        }};
}
