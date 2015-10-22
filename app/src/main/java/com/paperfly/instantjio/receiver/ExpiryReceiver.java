package com.paperfly.instantjio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class ExpiryReceiver extends BroadcastReceiver {
    public static final String TAG = ExpiryReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar updateTime = Calendar.getInstance();
        Log.i(TAG, String.valueOf(updateTime.get(Calendar.HOUR_OF_DAY)) + ":" +
                String.valueOf(updateTime.get(Calendar.MINUTE)) + ":" +
                String.valueOf(updateTime.get(Calendar.SECOND)));
        Log.i(TAG, "Event ended!");
    }
}
