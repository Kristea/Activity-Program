package uk.ac.uea.activityprogram.Models;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import uk.ac.uea.activityprogram.Controllers.MainActivity;
import uk.ac.uea.activityprogram.R;
import uk.ac.uea.framework.implementation.Event;

/**
 * Created by Kristiana on 19/11/2015.
 * Manage notifications for the app, build a notification using the passed in parameters from the home page
 * Notification sound and vibration depends on user settings.
 *
 * Listens for alarm manager call to build a notification
 */
public class NotifyService extends BroadcastReceiver {

    private Event event;
    private AppUser user;

    /**
     * Method that creates a notification using the notification builder class
     *
     * @param context the context of the class that calls this method
     * @param msg the message title of the notification
     * @param msgText the message to be included in the notification
     * @param msgAlert the message alert that comes up in the notification bar
     * @param event the event the notification is for
     * @param user the user the notification was generated for
     */
    public void createNotification(Context context,String msg, String msgText, String msgAlert, Event event, AppUser user){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] pattern ={0,200,0};
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.calendar_logo);
        //int res = getResources().getIdentifier("img","drawable", getPackageName());

        PendingIntent notificationIntent = PendingIntent.getActivity(context,0, new Intent(context, MainActivity.class),0);
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_event_white_24dp)
                .setLargeIcon(icon)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setAutoCancel(true)
                .setLights(Color.MAGENTA, 3000, 3000)
                .setContentText(msgText)
                .setCategory("CATEGORY_EVENT");
        if (user.getSettings().getNotifVibration()){
            mBuilder.setVibrate(pattern); // only apply if user wants vibrate
        }
        if (user.getSettings().getNotifSound()){
            mBuilder.setSound(alarmSound); // only apply if parameter length > 0
        }


        mBuilder.setContentIntent(notificationIntent);
        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(1,mBuilder.build());

    }

    /**
     * Method that listens for a notification intent using the broadcast reciever
     *
     * @param context the context of the previous class
     * @param intent the intent passed from the previous class
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        event = (Event)intent.getSerializableExtra("Event");
        user = (AppUser)intent.getSerializableExtra("User");

        createNotification(context, event.getName(), event.getTime(), "UEA open day", event,user);
    }

}

