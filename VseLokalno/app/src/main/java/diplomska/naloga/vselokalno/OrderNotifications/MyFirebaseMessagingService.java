package diplomska.naloga.vselokalno.OrderNotifications;

import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

import diplomska.naloga.vselokalno.MainActivity;
import diplomska.naloga.vselokalno.R;

/**
 * Status of notification: (Eg: 0#4 -> new status 0 AND new status 4 with extra parameter ($articleID))
 * 0 == New order,
 * 1 == Stock running low,
 * 2 == Order has been approved,
 * 3 == Order has been canceled,
 * 4 == Ran out of stock on ordered items
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String TAG = "MyFirebaseMessagingService";
    String CHANNEL_ID = "Main channel VseLokalno app";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) { // Got message from topic but not the user:
            makeLogW(TAG, "(onMessageReceived) received message but user is null!");
        } else { // Got user and message from topic:
            makeLogD(TAG, "(onMessageReceived) Message: " + remoteMessage.getData());
            String userID_temp = firebaseUser.getUid();
            Map<String, String> myData = remoteMessage.getData();
            if (Objects.equals(myData.get("ID_to"), userID_temp)) { // Targeted at this user.
                String idTo = myData.get("ID_to");
                String idOrder = myData.get("ID_order");
                String[] statusNumbers = Objects.requireNonNull(myData.get("status_number")).split("#");
                // Handle different situations:
                for (int i = 0; i < statusNumbers.length; i++) {
                    String tempStatusNumber = statusNumbers[i];
                    String title = "";
                    String textMessage = "";
                    int iconDrawable = -1;
                    switch (tempStatusNumber) {
                        case "0": // New order:
                            title = "Imate novo naročilo!";
                            textMessage = "Prejeli ste novo naročilo, odprite aplikacijo in se nanj odzovite.";
                            iconDrawable = getResources().getIdentifier("ic_info", "drawable", this.getPackageName());
                            break;
                        case "1": // Running out of stock:
                            title = "Pomanjkanje zaloge!";
                            textMessage = "Zaloga vaših artiklov je nizka in bo kmalu pošla! Preverite zalogo in jih založite.";
                            iconDrawable = getResources().getIdentifier("ic_warning", "drawable", this.getPackageName());
                            break;
                        case "2": // Order approved:
                            title = "Potrjeno naročilo!";
                            textMessage = "Naročilo, ki ste ga poslali je bilo potrjeno.";
                            iconDrawable = getResources().getIdentifier("ic_info", "drawable", this.getPackageName());
                            break;
                        case "3": // Order canceled:
                            title = "Preklic naročila!";
                            textMessage = "Naročilo je bilo preklicano in prestavljeno v vašo zgodovino.";
                            iconDrawable = getResources().getIdentifier("ic_error", "drawable", this.getPackageName());
                            break;
                        case "4": // Out of stock on articles:
                            title = "Pomanjkanje zaloge!";
                            textMessage = "Zaloga vaših artiklov je pošla! Dokler jih ne založite so odvzeti iz prodaje.";
                            iconDrawable = getResources().getIdentifier("ic_error", "drawable", this.getPackageName());
                            break;
                    }
                    if (iconDrawable != -1) {
                        sendNotification(
                                iconDrawable,
                                title,
                                textMessage,
                                i
                        );
                    } else {
                        makeLogW(TAG, "Error sending message (message is empty).");
                    }
                }
            }
        }
    }

    private void sendNotification(int iconDrawable, String title, String message, int id) {
        // Create a notification:
        Intent toMainIntent = new Intent(this, MainActivity.class);
//                toMainIntent.putExtra("OpenFragment", "UserFunctionsFragment");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(toMainIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(iconDrawable)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(id, builder.build());
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "VseLokalno Notification channel";
            String description = "This channel is for notifications regarding VseLokalno app.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    } // createNotificationChannel
}
