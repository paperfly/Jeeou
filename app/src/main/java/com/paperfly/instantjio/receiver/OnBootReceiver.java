package com.paperfly.instantjio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.firebase.client.Firebase;
import com.paperfly.instantjio.NotificationService;
import com.paperfly.instantjio.R;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        Firebase ref = new Firebase(context.getString(R.string.firebase_url));
        if (ref.getAuth() != null) {
            context.startService(new Intent(context, NotificationService.class));
        }
    }
}
