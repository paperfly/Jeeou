package com.paperfly.instantjio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.util.Constants;

import java.util.Calendar;

public class ExpiryReceiver extends BroadcastReceiver {
    public static final String TAG = ExpiryReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar updateTime = Calendar.getInstance();
        Log.i(TAG, Constants.DATE_TIME_FORMATTER.format(updateTime.getTime()));
        Log.i(TAG, "Event ended!");

        final Firebase ref = new Firebase(context.getString(R.string.firebase_url));
        final String key = intent.getStringExtra(Constants.EVENT_KEY);

        ref.child("events").child(key).child("type").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().equals("quick")) {
                    ref.child("events").child(key).removeValue();
                }

                ref.child("users").child(ref.getAuth().getUid()).child("oldEvents").child(key).setValue(true);
                ref.child("users").child(ref.getAuth().getUid()).child("events").child(key).removeValue();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
