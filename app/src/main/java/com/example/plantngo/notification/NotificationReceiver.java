package com.example.plantngo.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.plantngo.R;

import java.util.HashSet;
import java.util.Set;

/**
 * The NotificationReceiver class extends BroadcastReceiver and handles scheduled notifications.
 */
public class NotificationReceiver extends BroadcastReceiver {

    // Constants for notification management
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public static String NOTIFICATION_CHANNEL_ID = "1001 ";
    public static String DEFAULT_NOTIFICATION_ID = "default";

    /**
     * Overrides the onReceive method to display a notification when triggered by the AlarmManager.
     *
     * @param context The context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        if (Build.VERSION.SDK_INT >= 26) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANEL_NAME", importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        assert notificationManager != null;
        notificationManager.notify(id, notification);
    }

    /**
     * Schedules a notification for a specific plant with the provided delay.
     *
     * @param context   The application context.
     * @param plantName The name of the plant associated with the notification.
     * @param notification The Notification object to be displayed.
     * @param delay     The delay in milliseconds before the notification is triggered.
     */
    public void scheduleNotification(Context context, String plantName, Notification notification, long delay) {
        int notificationId = plantName.hashCode();

        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        notificationIntent.putExtra("plantName", plantName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureMillis, pendingIntent);

        saveNotificationToSharedPreferences(context, notificationId, delay);

        // Log information about the scheduled notification
        Log.d("ScheduledNotification", "ID: " + plantName.hashCode());
        Log.d("ScheduledNotification", "Plant: " + plantName);
        Log.d("ScheduledNotification", "Scheduled Time: " + futureMillis);
        Log.d("SchedulingStatus", "Notification scheduled");

        Toast.makeText(context, "Watering reminder notification scheduled for " + plantName, Toast.LENGTH_SHORT).show();
    }

    /**
     * Saves the information about a scheduled notification to SharedPreferences.
     *
     * @param context         The application context.
     * @param notificationId The unique ID of the scheduled notification.
     * @param delay           The delay in milliseconds before the notification is triggered.
     */
    private void saveNotificationToSharedPreferences(Context context, int notificationId, long delay) {
        // Retrieve the existing set of scheduled notifications
        Set<String> notifications = PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet("scheduled_notifications", new HashSet<>());

        // Add the new notification entry to the set
        String notificationEntry = notificationId + ":" + delay;
        notifications.add(notificationEntry);

        // Save the updated set to SharedPreferences
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putStringSet("scheduled_notifications", notifications)
                .apply();
    }

    /**
     * Displays a list of all scheduled notifications for debugging purposes.
     *
     * @param context The application context.
     */
    public void listScheduledNotifications(Context context) {
        Log.d("ScheduledNotifications", "List of scheduled notifications:");

        // Retrieve the set of scheduled notifications from SharedPreferences
        Set<String> notifications = PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet("scheduled_notifications", new HashSet<>());

        for (String notificationEntry : notifications) {
            String[] parts = notificationEntry.split(":");
            int notificationId = Integer.parseInt(parts[0]);
            long delay = Long.parseLong(parts[1]);

            Log.d("ScheduledNotification", "ID: " + notificationId);
            Log.d("ScheduledNotification", "Delay: " + delay);
        }
    }

    /**
     * Cancels a scheduled notification for a specific plant.
     *
     * @param context   The application context.
     * @param plantName The name of the plant associated with the notification.
     */
    public void cancelScheduledNotification(Context context, String plantName) {
        int notificationId = plantName.hashCode();

        // Create the PendingIntent with the same details
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra("plantName", plantName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Cancel the PendingIntent
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.cancel(pendingIntent);

        // Remove the canceled notification from SharedPreferences
        removeNotificationFromSharedPreferences(context, notificationId);

        // Log information about the canceled notification
        Log.d("CanceledNotification", "ID: " + notificationId);
        Log.d("CanceledNotification", "Plant: " + plantName);
        Log.d("CancellationStatus", "Notification canceled");

        Toast.makeText(context, "Watering reminder notification for " + plantName + " is cancelled", Toast.LENGTH_SHORT).show();
    }

    /**
     * Removes information about a canceled notification from SharedPreferences.
     *
     * @param context         The application context.
     * @param notificationId The unique ID of the canceled notification.
     */
    private void removeNotificationFromSharedPreferences(Context context, int notificationId) {
        // Retrieve the existing set of scheduled notifications
        Set<String> notifications = new HashSet<>(
                PreferenceManager.getDefaultSharedPreferences(context)
                        .getStringSet("scheduled_notifications", new HashSet<>())
        );

        // Remove the canceled notification entry from the set
        notifications.removeIf(entry -> entry.startsWith(notificationId + ":"));

        // Save the updated set to SharedPreferences
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putStringSet("scheduled_notifications", notifications)
                .apply();
    }

    /**
     * Saves the state of a notification (active or inactive) to SharedPreferences.
     *
     * @param context   The application context.
     * @param plantName The name of the plant associated with the notification.
     * @param isActive  The state of the notification (true for active, false for inactive).
     */
    public void saveNotificationState(Context context, String plantName, boolean isActive) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(plantName + "_notification_state", isActive).apply();
    }

    /**
     * Retrieves the state of a notification from SharedPreferences.
     *
     * @param context   The application context.
     * @param plantName The name of the plant associated with the notification.
     * @return The state of the notification (true if active, false if inactive).
     */
    public boolean getNotificationState(Context context, String plantName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(plantName + "_notification_state", false);
    }

    /**
     * Creates and returns a notification with the specified content.
     *
     * @param context The application context.
     * @param content The content text of the notification.
     * @return The Notification object.
     */
    public Notification getNotification(Context context, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_ID);

        builder.setContentTitle("Watering Reminder");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);

        return builder.build();
    }
}