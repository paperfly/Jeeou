package com.paperfly.instantjio.events;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.paperfly.instantjio.R;

public class EventViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = EventViewHolder.class.getCanonicalName();
    private TextView mEventTimeLeft;
    private TextView mEventTitle;
    private TextView mEventDescription;
    private ImageView mEventPhoto;
    private ImageView mEventHostPhoto;

    public EventViewHolder(View view) {
        super(view);

        mEventTimeLeft = (TextView) view.findViewById(R.id.event_time_left);
        mEventTitle = (TextView) view.findViewById(R.id.event_title);
        mEventPhoto = (ImageView) view.findViewById(R.id.event_photo);
        mEventDescription = (TextView) view.findViewById(R.id.event_description);
    }

    public void set(Event event) {
        mEventTitle.setText(event.getTitle());
//        mEventDescription.setText(event.getDescription());
    }
}
