package com.paperfly.instantjio.events;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.contacts.ContactsChooserActivity;
import com.paperfly.instantjio.groups.GroupsChooserActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class EventCreateActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = EventCreateActivity.class.getCanonicalName();
    private static final String CHOSEN_GROUPS = "CHOSEN_GROUPS";
    private static final String CHOSEN_CONTACTS = "CHOSEN_CONTACTS";
    private static final int GROUPS_CHOOSER_RC = 0;
    private static final int CONTACTS_CHOOSER_RC = 1;
    private ArrayList<String> mChosenGroups;
    private HashMap<String, String> mChosenContacts;

    private EditText startDate;
    private EditText endDate;
    private EditText startTime;
    private EditText endTime;
    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog endDatePickerDialog;
    private TimePickerDialog startTimePickerDialog;
    private TimePickerDialog endTimePickerDialog;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mChosenGroups = new ArrayList<>();
        mChosenContacts = new HashMap<>();

        initEventListeners();

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        timeFormatter = new SimpleDateFormat("HH:mm ZZZZ", Locale.getDefault());

        startDate = (EditText) findViewById(R.id.inputStartDate);
        startDate.setInputType(InputType.TYPE_NULL);
        startTime = (EditText) findViewById(R.id.inputStartTime);
        startTime.setInputType(InputType.TYPE_NULL);
        endDate = (EditText) findViewById(R.id.inputEndDate);
        endDate.setInputType(InputType.TYPE_NULL);
        endTime = (EditText) findViewById(R.id.inputEndTime);
        endTime.setInputType(InputType.TYPE_NULL);

        setDateField();
        setTimeField();

        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAutocompleteView.setAdapter(mAdapter);
    }

    void initEventListeners() {
        findViewById(R.id.add_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGroupsChooser();
            }
        });

        findViewById(R.id.add_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startContactsChooser();
            }
        });

        findViewById(R.id.add_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventCreateActivity.this, "Event created!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void startGroupsChooser() {
        final Intent intent = new Intent(this, GroupsChooserActivity.class);

        if (mChosenGroups != null) {
            intent.putExtra(CHOSEN_GROUPS, mChosenGroups);
        }

        startActivityForResult(intent, GROUPS_CHOOSER_RC);
    }

    public void startContactsChooser() {
        final Intent intent = new Intent(this, ContactsChooserActivity.class);

        if (mChosenContacts != null)
            intent.putExtra(CHOSEN_CONTACTS, mChosenContacts);

        startActivityForResult(intent, CONTACTS_CHOOSER_RC);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setDateField() {
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        startDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                startDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        endDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                endDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setTimeField() {
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();

        startTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar newTime = Calendar.getInstance();

                newTime.set(0, 0, 0, hourOfDay, minute, 0);
                startTime.setText(timeFormatter.format(newTime.getTime()));
            }
        },newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE), true);

        endTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar newTime = Calendar.getInstance();

                newTime.set(0, 0, 0, hourOfDay, minute, 0);
                endTime.setText(timeFormatter.format(newTime.getTime()));
            }
        },newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE), true);
    }

    @Override
    public void onClick(View view) {
        if(view == startDate) {
            startDatePickerDialog.show();
        }
        else if(view == endDate) {
            endDatePickerDialog.show();
        }
        else if(view == startTime) {
            startTimePickerDialog.show();
        }
        else if (view == endTime) {
            endTimePickerDialog.show();
        }
        view.clearFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GROUPS_CHOOSER_RC) {
            if (resultCode == RESULT_OK) {
                mChosenGroups = MergeList(mChosenGroups, (ArrayList<String>) data.getSerializableExtra(CHOSEN_GROUPS));

                for (int i = 0; i < mChosenGroups.size(); ++i) {
                    Log.i(TAG, mChosenGroups.get(i));
                }
            }
        } else if (requestCode == CONTACTS_CHOOSER_RC) {
            if (resultCode == RESULT_OK) {
                mChosenContacts = MergeMap(mChosenContacts, (HashMap<String, String>) data.getSerializableExtra(CHOSEN_CONTACTS));

                for (HashMap.Entry<String, String> entry : mChosenContacts.entrySet()) {
                    Log.i(TAG, entry.getKey() + " " + entry.getValue());
                }
            }
        }
    }

    // Merge two maps from right to left
    private HashMap<String, String> MergeMap(HashMap<String, String> left, HashMap<String, String> right) {
        final HashMap<String, String> temp = new HashMap<>();

        // Remove deselected chosenContacts here
        for (HashMap.Entry<String, String> entry : left.entrySet()) {
            if (right.containsKey(entry.getKey())) {
                temp.put(entry.getKey(), entry.getValue());
            }
        }

        // Merge them
        for (HashMap.Entry<String, String> entry : right.entrySet()) {
            temp.put(entry.getKey(), entry.getValue());
        }

        return temp;
    }

    private ArrayList<String> MergeList(ArrayList<String> left, ArrayList<String> right) {
        final ArrayList<String> temp = new ArrayList<>();

        // Remove deselected chosenContacts here

        for (int i = 0; i < left.size(); ++i) {
            if (right.contains(left.get(i))) {
                temp.add(left.get(i));
            }
        }

        for (int i = 0; i < right.size(); ++i) {
            if (!temp.contains(right.get(i))) {
                temp.add(right.get(i));
            }
        }

        return temp;
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }
}
