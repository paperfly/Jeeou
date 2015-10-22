package com.paperfly.instantjio.event;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.paperfly.instantjio.receiver.ExpiryReceiver;
import com.paperfly.instantjio.receiver.ReminderReceiver;
import com.paperfly.instantjio.util.Constants;

import java.util.Calendar;

public class EventScheduler {
    public static final String TAG = EventScheduler.class.getCanonicalName();

    private EventScheduler() {
    }

    public static void setStartAlarm(Context context, String key, AlarmManager alarmManager, Calendar startDate) {
        Log.i(TAG, "Start Date: " + Constants.DATE_TIME_FORMATTER.format(startDate.getTime()));

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(Constants.EVENT_KEY, key);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC,
                    startDate.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC,
                    startDate.getTimeInMillis(), pendingIntent);
        }
    }

    public static void setStopAlarm(Context context, String key, AlarmManager alarmManager, Calendar endDate) {
        Log.i(TAG, "End Date: " + Constants.DATE_TIME_FORMATTER.format(endDate.getTime()));

        Intent intent = new Intent(context, ExpiryReceiver.class);
        intent.putExtra(Constants.EVENT_KEY, key);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC,
                    endDate.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC,
                    endDate.getTimeInMillis(), pendingIntent);
        }
    }
}
