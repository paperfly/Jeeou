package com.paperfly.instantjio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.paperfly.instantjio.event.Event;

public class NotificationService extends Service {
    public static final String TAG = NotificationService.class.getCanonicalName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                final Firebase ref = new Firebase(getString(R.string.firebase_url));

                final String uid = ref.getAuth().getUid();

                final ChildEventListener mChildHandler = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (!dataSnapshot.exists()) {
                            return;
                        }

                        final Query queryRef = ref.child("events").child(dataSnapshot.getKey());
                        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    return;
                                }

                                final Event event = dataSnapshot.getValue(Event.class);
                                postNotification("New event!", event.getTitle());
                                startInvitedActivity();

                                // Add the un-notified event to the user's list of notified events
                                ref.child("users").child(uid).child("events").child(dataSnapshot.getKey()).setValue(true);
                                // Remove the un-notified event from the list of new events
                                ref.child("users").child(uid).child("newEvents").child(dataSnapshot.getKey()).removeValue();
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                };

                final Query queryRef = ref.child("users").child(uid).child("newEvents");
                queryRef.addChildEventListener(mChildHandler);
            }
        }).start();
    }

    private void postNotification(final String contentTitle, final String contentText) {
        Intent resultIntent = new Intent(this, MainActivity.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
//        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
        // Gets a PendingIntent containing the entire back stack
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(contentTitle)
                        .setContentText(contentText)
                        .setAutoCancel(true)
//                        .setSound(alarmSound)
//                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
//                        .setLights(Color.YELLOW, 3000, 3000)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 1;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private void startInvitedActivity() {
//        startActivity(new Intent(this, EventInvitedActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
