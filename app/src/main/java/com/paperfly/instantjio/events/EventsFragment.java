package com.paperfly.instantjio.events;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Firebase;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.groups.GroupsFragment;

public class EventsFragment extends Fragment {
    private static final String TAG = GroupsFragment.class.getCanonicalName();
    private EventsAdapter mAdapter;

    public EventsFragment() {

    }

//    public static EventsFragment newInstance() {
//        EventsFragment fragment = new EventsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Firebase ref = new Firebase(getString(R.string.firebase_url));
        mAdapter = new EventsAdapter(ref);

//        Event event = new Event();
//        event.setTitle("Basketball!");
//        event.setStartTime("08:00 A.M.");
//        event.setEndTime("10:00 A.M.");
//        event.setStartDate("11 October 2015");
//        event.setEndDate("11 October 2015");
//        event.setDescription("It's been a long time since we last played, let's have some fun");
//        event.addInvited("+60 10-208 0089");
//        event.addInvited("+60 16-341 7325");

//        final Firebase newRef = ref.child("events").push();
//        final Map<String, Object> eventIndexMap = new HashMap<>();
//        eventIndexMap.put(newRef.getKey(), true);

//        newRef.setValue(event);
//        ref.child("users").child(ref.getAuth().getUid()).child("events").updateChildren(eventIndexMap);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.destroy();
    }
}
