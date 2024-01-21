package com.example.bookit.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.bookit.R;
import com.example.bookit.model.Notification;

public class NotificationUtils {
    private static final String CHANNEL_ID = "BookIT_CHID"; // You can define your own channel ID

    public static void notifyPhone(Context context, Notification latestNotification, String notificationTitle) {
        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if the device is running Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel for Oreo and higher
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "BookITChannel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_bell)
                .setContentTitle(notificationTitle)
                .setContentText(latestNotification.getMessage())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        notificationManager.notify(latestNotification.getId(), builder.build());
    }
}
