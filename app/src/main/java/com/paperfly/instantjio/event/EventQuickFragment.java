package com.paperfly.instantjio.event;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.common.firebase.DirectReferenceAdapter;
import com.paperfly.instantjio.common.firebase.ItemEventListener;
import com.paperfly.instantjio.util.Constants;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;

public class EventQuickFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String TAG = EventQuickFragment.class.getCanonicalName();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View vRoot;
    private QuickEventAdapter mAdapter;
    private ItemEventListener<Event> mListener = new ItemEventListener<Event>() {
        @Override
        public void itemAdded(Event item, String key, int position) {
            if (mAdapter.getItemCount() > 0) {
                vRoot.setBackgroundResource(0);
            }
        }

        @Override
        public void itemChanged(Event oldItem, Event newItem, String key, int position) {

        }

        @Override
        public void itemRemoved(Event item, String key, int position) {
            if (mAdapter.getItemCount() == 0) {
                if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT >= 22) {
                    vRoot.setBackground(getResources().getDrawable(R.drawable.empty_template_light_bg, null));
                } else if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
                    vRoot.setBackground(getResources().getDrawable(R.drawable.empty_template_light_bg));
                } else {
                    vRoot.setBackgroundDrawable(getResources().getDrawable(R.drawable.empty_template_light_bg));
                }
            }
        }

        @Override
        public void itemMoved(Event item, String key, int oldPosition, int newPosition) {

        }
    };

    public EventQuickFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventQuickFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventQuickFragment newInstance(String param1, String param2) {
        EventQuickFragment fragment = new EventQuickFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user != null ? user.getUid() : "";
//        final Firebase ref = new Firebase(getString(R.string.firebase_url));
        Query newRef = userRef.child(userId).child("templates").limitToLast(3);
        mAdapter = new QuickEventAdapter(newRef, mListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vRoot = inflater.inflate(R.layout.fragment_event_quick, container, false);
        RecyclerView recyclerView = (RecyclerView) vRoot.findViewById(R.id.event_quick_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        return vRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mAdapter.getItemCount() == 0) {
            if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT >= 22) {
                view.setBackground(getResources().getDrawable(R.drawable.empty_template_light_bg, null));
            } else if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
                view.setBackground(getResources().getDrawable(R.drawable.empty_template_light_bg));
            } else {
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.empty_template_light_bg));
            }
        } else {
            view.setBackgroundResource(0);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_quick_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.event_template_add) {
            startActivity(new Intent(getContext(), EventTemplateCreateActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createEvent(final String templateId) {
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        final DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events");
//        final Firebase ref = new Firebase(getString(R.string.firebase_url));
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user != null ? user.getUid() : "";
        userRef.child(userId).child("templates").child(templateId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event event = dataSnapshot.getValue(Event.class);
                        event.setType("quick");
                        event.setHost(userId);
                        DatabaseReference newEventRef = eventRef.push();
                        newEventRef.setValue(event);
                        userRef.child(event.getHost()).child("events").child(newEventRef.getKey()).setValue(true);
                        for (HashMap.Entry<String, Boolean> entry : event.getInvited().entrySet()) {
                            userRef.child(entry.getKey()).child("events").child(newEventRef.getKey()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });
    }

    private void createEvent(Event event) {
//        Firebase ref = new Firebase(getString(R.string.firebase_url));
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        final DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user != null ? user.getUid() : "";
        DatabaseReference newEventRef = eventRef.push();
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Calendar now = Calendar.getInstance();
        Calendar reminder = Calendar.getInstance();
        Calendar expiry = Calendar.getInstance();

        event.setType("quick");
        event.setHost(userId);

        Calendar startTime = Calendar.getInstance();
        try {
            startTime.setTime(Constants.DATE_TIME_FORMATTER.parse(
                            Constants.DATE_FORMATTER.format(now.getTime()) + " " + event.getStartTime())
            );

            if (now.after(startTime)) {
                now.add(Calendar.DAY_OF_WEEK, 1);
            }

            event.setStartDate(Constants.DATE_FORMATTER.format(now.getTime()));
            event.setEndDate(Constants.DATE_FORMATTER.format(now.getTime()));
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }

        newEventRef.setValue(event);
        userRef.child(event.getHost()).child("events").child(newEventRef.getKey()).setValue(true);
        for (HashMap.Entry<String, Boolean> entry : event.getInvited().entrySet()) {
            if (!entry.getKey().equals(userId)) {
                userRef.child(entry.getKey()).child("newEvents").child(newEventRef.getKey()).setValue(true);
            }
        }

        try {
            reminder.setTime(Constants.DATE_TIME_FORMATTER.parse(event.getStartDate() + " " + event.getStartTime()));
            expiry.setTime(Constants.DATE_TIME_FORMATTER.parse(event.getEndDate() + " " + event.getEndTime()));

            EventScheduler.setStartAlarm(getContext(), newEventRef.getKey(), alarmManager, reminder);
            EventScheduler.setStopAlarm(getContext(), newEventRef.getKey(), alarmManager, expiry);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    protected class QuickEventAdapter extends DirectReferenceAdapter<QuickEventAdapter.QuickEventViewHolder, Event> {
        public QuickEventAdapter(Query ref, ItemEventListener<Event> itemListener) {
            super(ref, Event.class, itemListener);
        }

        @Override
        public QuickEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listItemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_event_template, parent, false);
            return new QuickEventViewHolder(listItemView);
        }

        @Override
        public void onBindViewHolder(QuickEventViewHolder holder, int position) {
            holder.setView(getItem(position));
        }

        protected class QuickEventViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            Button eventCreate;

            public QuickEventViewHolder(View itemView) {
                super(itemView);

                title = (TextView) itemView.findViewById(R.id.event_template_title);
                eventCreate = (Button) itemView.findViewById(R.id.event_create);

                eventCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        createEvent(mKeys.get(getAdapterPosition()));
                        createEvent(getItem(getAdapterPosition()));
                        getActivity().finish();
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), EventTemplateScrollingActivity.class);
                        Event event = getItem(getAdapterPosition());
                        EventParcelable eventParcelable = new EventParcelable(event);
                        intent.putExtra(Constants.EVENT_OBJECT, eventParcelable);
                        intent.putExtra(Constants.EVENT_KEY, mKeys.get(getAdapterPosition()));
                        startActivity(intent);
                    }
                });
            }

            public void setView(Event event) {
                title.setText(event.getTitle());
            }
        }
    }
}
