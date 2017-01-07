package com.paperfly.instantjio;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paperfly.instantjio.event.EventInvitedNotifyActivity;
import com.paperfly.instantjio.event.EventParcelable;
import com.paperfly.instantjio.event.EventScheduler;
import com.paperfly.instantjio.event.EventScrollingActivity;
import com.paperfly.instantjio.group.GroupParcelable;
import com.paperfly.instantjio.group.GroupScrollingActivity;
import com.paperfly.instantjio.util.Constants;
import com.paperfly.instantjiocore.Model.Event;
import com.paperfly.instantjiocore.Model.Group;

import java.text.ParseException;
import java.util.Calendar;

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

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user != null ? user.getUid() : "";
//        Firebase ref = new Firebase(getString(R.string.firebase_url));
        if (user == null) {
            stopSelf();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

//                final Firebase ref = new Firebase(getString(R.string.firebase_url));
                final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                final DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events");
//                final String uid = ref.getAuth().getUid();
                final Query newEventsRef = userRef.child(userId).child("newEvents");
                final ChildEventListener listener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Query eventsRef = eventRef.child(dataSnapshot.getKey());
                        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final Event event = dataSnapshot.getValue(Event.class);
                                readyNotification(event, dataSnapshot.getKey());
                                startEventInvitedActivity(event, dataSnapshot.getKey());

                                // Add the un-notified event to the user's list of notified events
                                userRef.child(userId).child("events").child(dataSnapshot.getKey()).setValue(true);
                                // Remove the un-notified event from the list of new events
                                userRef.child(userId).child("newEvents").child(dataSnapshot.getKey()).removeValue();

                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                Calendar reminder = Calendar.getInstance();
                                Calendar expiry = Calendar.getInstance();

                                try {
                                    reminder.setTime(Constants.DATE_TIME_FORMATTER.parse(
                                            event.getStartDate() + " " + event.getStartTime()));
                                    expiry.setTime(Constants.DATE_TIME_FORMATTER.parse(
                                            event.getEndDate() + " " + event.getEndTime()));

                                    EventScheduler.setStartAlarm(getApplicationContext(), dataSnapshot.getKey(), alarmManager, reminder);
                                    EventScheduler.setStopAlarm(getApplicationContext(), dataSnapshot.getKey(), alarmManager, expiry);
                                } catch (ParseException e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError firebaseError) {

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
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                };

                newEventsRef.addChildEventListener(listener);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

//                final Firebase ref = new Firebase(getString(R.string.firebase_url));
                final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                final DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups");
//                final String uid = ref.getAuth().getUid();
                final Query newGroupsRef = userRef.child(userId).child("newGroups");
                final ChildEventListener listener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Query groupsRef = groupRef.child(dataSnapshot.getKey());
                        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final Group group = dataSnapshot.getValue(Group.class);
                                readyNotification(group, dataSnapshot.getKey());

                                // Add the new group to the user's groups' list
                                userRef.child(userId).child("groups").child(dataSnapshot.getKey()).setValue(true);
                                // Remove the newly added group from the user's "newGroups" tree
                                userRef.child(userId).child("newGroups").child(dataSnapshot.getKey()).removeValue();
                            }

                            @Override
                            public void onCancelled(DatabaseError firebaseError) {

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
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                };

                newGroupsRef.addChildEventListener(listener);
            }
        }).start();
    }

    private void readyNotification(Event event, String key) {
        Intent resultIntent = new Intent(this, EventScrollingActivity.class);
        EventParcelable eventParcelable = new EventParcelable(event);
        resultIntent.putExtra(Constants.EVENT_OBJECT, eventParcelable);
        resultIntent.putExtra(Constants.EVENT_KEY, key);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(EventScrollingActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        postNotification(stackBuilder, "New event!", event.getTitle(), sEventNotificationId);
    }


    private void readyNotification(Group group, String key) {
        Intent resultIntent = new Intent(this, GroupScrollingActivity.class);
        GroupParcelable groupParcelable = new GroupParcelable(group);
        resultIntent.putExtra(Constants.GROUP_OBJECT, groupParcelable);
        resultIntent.putExtra(Constants.GROUP_KEY, key);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(GroupScrollingActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        postNotification(stackBuilder, "You have been invited to join a group!", group.getName(), sGroupNotificationId);
    }

    private void postNotification(TaskStackBuilder stackBuilder, String title, String text, int id) {
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_paperfly_instantjio_white_nobg)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(resultPendingIntent);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(id, mBuilder.build());
    }

    private void startEventInvitedActivity(Event event, String key) {
        Intent intent = new Intent(this, EventInvitedNotifyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        EventParcelable eventParcelable = new EventParcelable(event);
        intent.putExtra(Constants.EVENT_OBJECT, eventParcelable);
        intent.putExtra(Constants.EVENT_KEY, key);
        startActivity(intent);
    }
}
