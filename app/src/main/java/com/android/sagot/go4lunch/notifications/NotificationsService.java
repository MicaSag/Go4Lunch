package com.android.sagot.go4lunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.sagot.go4lunch.Controllers.Activities.RestaurantCardActivity;
import com.android.sagot.go4lunch.Models.firestore.Restaurant;
import com.android.sagot.go4lunch.Models.firestore.User;
import com.android.sagot.go4lunch.R;
import com.android.sagot.go4lunch.Utils.Toolbox;
import com.android.sagot.go4lunch.api.RestaurantHelper;
import com.android.sagot.go4lunch.api.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;

public class NotificationsService extends FirebaseMessagingService {

    // For Debug
    private static final String TAG = "NotificationsService";

    private final int NOTIFICATION_ID = 004;
    private final String NOTIFICATION_TAG = "GO4LUNCH";

    private FirebaseUser firebaseUser;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: ");

        Log.d(TAG, "onMessageReceived: remoteMessage.getNotification() = "+remoteMessage.getNotification());

        if (Toolbox.isNetworkAvailable(this)) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser != null) {
                if (remoteMessage.getNotification() != null) {
                    // Get message sent by FireBase
                    String message = remoteMessage.getNotification().getBody();
                    // Show message in console
                    Log.e(TAG, "onMessageReceived: " + message);
                    getWorkmatesList();
                }
            }
        }
    }

    private void getWorkmatesList(){
        Log.d(TAG, "getWorkmatesList: ");

        // Get additional data from FireStore : restaurantIdentifier of the User choice
        UserHelper.getUser(firebaseUser.getUid()).addOnCompleteListener(documentSnapshot -> {
            if (documentSnapshot.isSuccessful()) {
                User currentUser = documentSnapshot.getResult().toObject(User.class);
                String restaurantIdentifier = currentUser.getRestaurantIdentifier();
                Log.d(TAG, "onSuccess: restaurantIdentifier = " + restaurantIdentifier);
                if (restaurantIdentifier != null) {
                    RestaurantHelper.getRestaurant(restaurantIdentifier).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> documentSnapshot) {
                            if (documentSnapshot.isSuccessful()) {
                                Restaurant restaurant = documentSnapshot.getResult().toObject(Restaurant.class);

                                ArrayList<String> message = new ArrayList<>();
                                message.add("We remind you that you have decided to have lunch");
                                message.add("At : " + restaurant.getName());
                                message.add("To the following address : ");
                                message.add(restaurant.getAddress());
                                UserHelper.getAllUser()
                                        .whereEqualTo("restaurantIdentifier", restaurant.getIdentifier())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                                ArrayList<String> workmatesNameList = new ArrayList<>();
                                                for (DocumentSnapshot document : queryDocumentSnapshots) {
                                                    User user = document.toObject(User.class);
                                                    String userName = user.getUserName();
                                                    String userId = user.getUid();
                                                    Log.d(TAG, "onEvent: userId   = " + userId);
                                                    Log.d(TAG, "onEvent: userName = " + userName);

                                                    if (!userId.equals(firebaseUser.getUid()))
                                                        workmatesNameList.add(user.getUserName());
                                                }
                                                if (workmatesNameList.size() > 0) {
                                                    message.add("With :");
                                                    for (String workmatesName : workmatesNameList)
                                                        message.add(workmatesName);
                                                }
                                                sendVisualNotification(message,restaurant.getIdentifier());
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendVisualNotification(ArrayList<String> messageBody,String restaurantIdentifier) {

        // Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, RestaurantCardActivity.class);

        // ==> Sends the Restaurant details
        intent.putExtra(RestaurantCardActivity.KEY_DETAILS_RESTAURANT_CARD,restaurantIdentifier);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.notification_message_title));
        for (String messageLine : messageBody){
            inboxStyle.addLine(messageLine);
        }

        // Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);

        // Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.pic_logo_go4lunch_512x512)
                        .setBadgeIconType(R.drawable.pic_logo_go4lunch_512x512)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.notification_title))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        // Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = getString(R.string.default_notification_channel_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }
}
