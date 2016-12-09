package com.paperfly.instantjio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paperfly.instantjio.util.Constants;

import java.util.Calendar;

public class ExpiryReceiver extends BroadcastReceiver {
    public static final String TAG = ExpiryReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar updateTime = Calendar.getInstance();
        Log.i(TAG, Constants.DATE_TIME_FORMATTER.format(updateTime.getTime()));
        Log.i(TAG, "Event ended!");

//        final Firebase ref = new Firebase(context.getString(R.string.firebase_url));
        final DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events");
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user != null ? user.getUid() : "";
        final String key = intent.getStringExtra(Constants.EVENT_KEY);

        eventRef.child(key).child("type").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().equals("quick")) {
                    eventRef.child(key).removeValue();
                }

                userRef.child(userId).child("oldEvents").child(key).setValue(true);
                userRef.child(userId).child("events").child(key).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e(TAG, firebaseError.getMessage());
            }
        });
    }
}
