package com.paperfly.instantjio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.paperfly.instantjio.event.Event;
import com.paperfly.instantjio.event.EventParcelable;
import com.paperfly.instantjio.event.EventScrollingActivity;
import com.paperfly.instantjio.group.Group;
import com.paperfly.instantjio.group.GroupParcelable;
import com.paperfly.instantjio.group.GroupScrollingActivity;
import com.paperfly.instantjio.util.Constants;

public class NotificationService extends Service {
    public static final String TAG = NotificationService.class.getCanonicalName();
    public static final int sEventNotificationId = 0;
    public static final int sGroupNotificationId = 1;

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
                final Query newEventsRef = ref.child("users").child(uid).child("newEvents");
                final ChildEventListener listener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Query eventsRef = ref.child("events").child(dataSnapshot.getKey());
                        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final Event event = dataSnapshot.getValue(Event.class);
                                postNotification(event, dataSnapshot.getKey());
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

                newEventsRef.addChildEventListener(listener);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                final Firebase ref = new Firebase(getString(R.string.firebase_url));
                final String uid = ref.getAuth().getUid();
                final Query newGroupsRef = ref.child("users").child(uid).child("newGroups");
                final ChildEventListener listener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Query groupsRef = ref.child("groups").child(dataSnapshot.getKey());
                        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final Group group = dataSnapshot.getValue(Group.class);
                                postNotification(group, dataSnapshot.getKey());
                                startInvitedActivity();

                                // Add the new group to the user's groups' list
                                ref.child("users").child(uid).child("groups").child(dataSnapshot.getKey()).setValue(true);
                                // Remove the newly added group from the user's "newGroups" tree
                                ref.child("users").child(uid).child("newGroups").child(dataSnapshot.getKey()).removeValue();
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

                newGroupsRef.addChildEventListener(listener);
            }
        }).start();
    }

    private void postNotification(Event event, String key) {
        Intent resultIntent = new Intent(this, EventScrollingActivity.class);
        EventParcelable eventParcelable = new EventParcelable(event);
        resultIntent.putExtra(Constants.EVENT_OBJECT, eventParcelable);
        resultIntent.putExtra(Constants.EVENT_KEY, key);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(EventScrollingActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("New event!")
                        .setContentText(event.getTitle())
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(resultPendingIntent);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(sEventNotificationId, mBuilder.build());
    }

    private void postNotification(Group group, String key) {
        Intent resultIntent = new Intent(this, GroupScrollingActivity.class);
        GroupParcelable groupParcelable = new GroupParcelable(group);
        resultIntent.putExtra(Constants.GROUP_OBJECT, groupParcelable);
        resultIntent.putExtra(Constants.GROUP_KEY, key);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(GroupScrollingActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("You have been invited to join a group!")
                        .setContentText(group.getName())
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(resultPendingIntent);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(sGroupNotificationId, mBuilder.build());
    }

    private void startInvitedActivity() {
//        startActivity(new Intent(this, EventInvitedActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}