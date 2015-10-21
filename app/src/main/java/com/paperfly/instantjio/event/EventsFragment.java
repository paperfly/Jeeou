package com.paperfly.instantjio.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.common.firebase.CrossReferenceAdapter;
import com.paperfly.instantjio.util.Constants;

public class EventsFragment extends Fragment {
    public static final String TAG = EventsFragment.class.getCanonicalName();
    private EventViewAdapter mAdapter;

    @SuppressWarnings("unused")
    public EventsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Firebase ref = new Firebase(getString(R.string.firebase_url));
        Query ref1 = ref.child("users").child(ref.getAuth().getUid()).child("events");
        Query ref2 = ref.child("events");
        mAdapter = new EventViewAdapter(ref1, ref2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.event_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.disable();
    }

    public class EventViewAdapter extends CrossReferenceAdapter<EventViewAdapter.EventViewViewHolder, Event> {
        public EventViewAdapter(Query firstRef, Query secondRef) {
            super(firstRef, secondRef, Event.class, null);
        }

        @Override
        public EventViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
            return new EventViewViewHolder(view);
        }

        @Override
        public void onBindViewHolder(EventViewViewHolder holder, int position) {
            final String title = getItem(position).getTitle();
            final String startDate = getItem(position).getStartDate();
            final String location = getItem(position).getLocation();
            final String type = getItem(position).getType();

            if (type.equals("quick")) {
                holder.setView(title, startDate, location, true);
            } else {
                holder.setView(title, startDate, location, false);
            }
        }

        public class EventViewViewHolder extends RecyclerView.ViewHolder {
            private final TextView mTitle;
            private final TextView mStartDate;
            private final TextView mLocation;

            public EventViewViewHolder(View itemView) {
                super(itemView);

                mTitle = (TextView) itemView.findViewById(R.id.event_title);
                mStartDate = (TextView) itemView.findViewById(R.id.event_start_date);
                mLocation = (TextView) itemView.findViewById(R.id.event_location);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), EventScrollingActivity.class);
                        Event event = getItem(getAdapterPosition());
                        EventParcelable eventParcelable = new EventParcelable(event);
                        intent.putExtra(Constants.EVENT_OBJECT, eventParcelable);
                        intent.putExtra(Constants.EVENT_KEY, mKeys.get(getAdapterPosition()));
                        startActivity(intent);
                    }
                });
            }

            public void setView(String title, String startDate, String location, boolean isQuickEvent) {
                mTitle.setText(title);
                mStartDate.setText(startDate);
                mLocation.setText(location);

                ImageView quickEvent = (ImageView) itemView.findViewById(R.id.event_quick);
                if (isQuickEvent) {
                    quickEvent.setVisibility(View.VISIBLE);
                } else {
                    quickEvent.setVisibility(View.GONE);
                }
            }
        }
    }
}
