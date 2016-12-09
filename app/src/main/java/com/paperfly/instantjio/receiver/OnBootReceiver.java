package com.paperfly.instantjio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.paperfly.instantjio.NotificationService;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        Firebase ref = new Firebase(context.getString(R.string.firebase_url));
        if (user != null) {
            context.startService(new Intent(context, NotificationService.class));
        }
    }
}
