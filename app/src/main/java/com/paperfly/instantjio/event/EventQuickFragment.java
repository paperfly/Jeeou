package com.paperfly.instantjio.event;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.common.firebase.DirectReferenceAdapter;

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

    private QuickEventAdapter mAdapter;

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

        final Firebase ref = new Firebase(getString(R.string.firebase_url));
        Query newRef = ref.child("users").child(ref.getAuth().getUid()).child("templates").limitToLast(3);
        mAdapter = new QuickEventAdapter(newRef, Event.class, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_quick, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.event_quick_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        return view;
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
        final Firebase ref = new Firebase(getString(R.string.firebase_url));
        ref.child("users").child(ref.getAuth().getUid()).child("templates").child(templateId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event event = dataSnapshot.getValue(Event.class);
                        Firebase eventRef = ref.child("events").push();
                        eventRef.setValue(event);
                        ref.child("users").child(event.getHost()).child("events").child(eventRef.getKey()).setValue(true);
                        for (HashMap.Entry<String, Boolean> entry : event.getInvited().entrySet()) {
                            ref.child("users").child(entry.getKey()).child("events").child(eventRef.getKey()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
    }

    protected class QuickEventAdapter extends DirectReferenceAdapter<QuickEventAdapter.QuickEventViewHolder, Event> {
        public QuickEventAdapter(Query ref, Class<Event> itemClass, ItemEventListener<Event> itemListener) {
            super(ref, itemClass, itemListener);
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
                        createEvent(mKeys.get(getAdapterPosition()));
                        getActivity().finish();
                    }
                });
            }

            public void setView(Event event) {
                title.setText(event.getTitle());
            }
        }
    }
}
