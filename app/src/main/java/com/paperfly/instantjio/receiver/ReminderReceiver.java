package com.paperfly.instantjio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.paperfly.instantjio.util.Constants;

import java.util.Calendar;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String TAG = ReminderReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar updateTime = Calendar.getInstance();
        Log.i(TAG, Constants.DATE_TIME_FORMATTER.format(updateTime.getTime()));
        Log.i(TAG, "There's an event coming soon in XX time!");
    }
}
