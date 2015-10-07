package com.paperfly.instantjio.contacts;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.paperfly.instantjio.R;
import com.paperfly.instantjio.util.Utils;
import com.paperfly.instantjio.widget.decorator.DividerItemDecoration;

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // Defines a tag for identifying log entries
    private static final String TAG = "ContactsFragment";

    // Bundle key for saving previously selected search result item
//    private static final String STATE_PREVIOUSLY_SELECTED_KEY =
//            "com.paperfly.instantjio.SELECTED_ITEM";

//    private ContactsAdapter mAdapter; // The main query adapter
//    private ImageLoader mImageLoader; // Handles loading the contact image in a background thread
    private String mSearchTerm; // Stores the current search query term

    // Contact selected listener that allows the activity holding this fragment to be notified of
    // a contact being selected
//    private OnContactsInteractionListener mOnContactSelectedListener;

    // Stores the previously selected search item so that on a configuration change the same item
    // can be reselected again
//    private int mPreviouslySelectedSearchItem = 0;

    // Whether or not the search query has changed since the last time the loader was refreshed
//    private boolean mSearchQueryChanged;

    // Whether or not this fragment is showing in a two-pane layout
//    private boolean mIsTwoPaneLayout;

    // Whether or not this is a search result view of this fragment, only used on pre-honeycomb
    // OS versions as search results are shown in-line via Action Bar search from honeycomb onward
//    private boolean mIsSearchResultView = false;

    private RecyclerView mContactListView;

    public static ContactsFragment newInstance() {
//        ContactsFragment fragment = new ContactsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return new ContactsFragment();
    }

    /**
     * Fragments require an empty constructor.
     */
    public ContactsFragment() {}

    /**
     * In platform versions prior to Android 3.0, the ActionBar and SearchView are not supported,
     * and the UI gets the search string from an EditText. However, the fragment doesn't allow
     * another search when search results are already showing. This would confuse the user, because
     * the resulting search would re-query the Contacts Provider instead of searching the listed
     * results. This method sets the search query and also a boolean that tracks if this Fragment
     * should be displayed as a search result view or not.
     *
//     * @param query The contacts search query.
     */
//    public void setSearchQuery(String query) {
//        if (TextUtils.isEmpty(query)) {
//            mIsSearchResultView = false;
//        } else {
//            mSearchTerm = query;
//            mIsSearchResultView = true;
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if this fragment is part of a two-pane set up or a single pane by reading a
        // boolean from the application resource directories. This lets allows us to easily specify
        // which screen sizes should use a two-pane layout by setting this boolean in the
        // corresponding resource size-qualified directory.
//        mIsTwoPaneLayout = getResources().getBoolean(R.bool.has_two_panes);

        // Let this fragment contribute menu items
        setHasOptionsMenu(true);
//
//        // Create the main contacts adapter
//        mAdapter = new ContactsAdapter(getActivity());
//
        if (savedInstanceState != null) {
            // If we're restoring state after this fragment was recreated then
            // retrieve previous search term and previously selected search
            // result.
            mSearchTerm = savedInstanceState.getString(SearchManager.QUERY);
//            mPreviouslySelectedSearchItem =
//                    savedInstanceState.getInt(STATE_PREVIOUSLY_SELECTED_KEY, 0);
        }

        /*
         * An ImageLoader object loads and resizes an image in the background and binds it to the
         * QuickContactBadge in each item layout of the ListView. ImageLoader implements memory
         * caching for each image, which substantially improves refreshes of the ListView as the
         * user scrolls through it.
         *
         * To learn more about downloading images asynchronously and caching the results, read the
         * Android training class Displaying Bitmaps Efficiently.
         *
         * http://developer.android.com/training/displaying-bitmaps/
         */
//        mImageLoader = new ImageLoader(getActivity(), getListPreferredItemHeight()) {
//            @Override
//            protected Bitmap processBitmap(Object data) {
//                // This gets called in a background thread and passed the data from
//                // ImageLoader.loadImage().
//                return loadContactPhotoThumbnail((String) data, getImageSize());
//            }
//        };

        // Set a placeholder loading image for the image loader
//        mImageLoader.setLoadingImage(R.drawable.ic_action_person);

        // Add a cache to the image loader
//        mImageLoader.addImageCache(getActivity().getSupportFragmentManager(), 0.1f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContactListView = (RecyclerView) getActivity().findViewById(android.R.id.list);
        mContactListView.setHasFixedSize(true);
        mContactListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mContactListView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mContactListView.setItemAnimator(new DefaultItemAnimator());
        // Set up ListView, assign adapter and set some listeners. The adapter was previously
        // created in onCreate().
//        setListAdapter(mAdapter);
//        getListView().setOnItemClickListener(this);
//        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
//                // Pause image loader to ensure smoother scrolling when flinging
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
//                    mImageLoader.setPauseWork(true);
//                } else {
//                    mImageLoader.setPauseWork(false);
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//            }
//        });

//        if (mIsTwoPaneLayout) {
//            // In a two-pane layout, set choice mode to single as there will be two panes
//            // when an item in the ListView is selected it should remain highlighted while
//            // the content shows in the second pane.
//            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        }

        // If there's a previously selected search item from a saved state then don't bother
        // initializing the loader as it will be restarted later when the query is populated into
        // the action bar search view (see onQueryTextChange() in onCreateOptionsMenu()).
//        if (mPreviouslySelectedSearchItem == 0) {
            // Initialize the loader, and create a loader identified by ContactsQuery.QUERY_ID
            getLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
//        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        try {
//            // Assign callback listener which the holding activity must implement. This is used
//            // so that when a contact item is interacted with (selected by the user) the holding
//            // activity will be notified and can take further action such as populating the contact
//            // detail pane (if in multi-pane layout) or starting a new activity with the contact
//            // details (single pane layout).
//            mOnContactSelectedListener = (OnContactsInteractionListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString()
//                    + " must implement OnContactsInteractionListener");
//        }
//    }

//    @Override
//    public void onPause() {
//        super.onPause();
//
//        // In the case onPause() is called during a fling the image loader is
//        // un-paused to let any remaining background work complete.
//        mImageLoader.setPauseWork(false);
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//        // Gets the Cursor object currently bound to the ListView
//        final Cursor cursor = mAdapter.getCursor();
//
//        // Moves to the Cursor row corresponding to the ListView item that was clicked
//        cursor.moveToPosition(position);
//
//        // Creates a contact lookup Uri from contact ID and lookup_key
//        final Uri uri = ContactsContract.Contacts.getLookupUri(
//                cursor.getLong(ContactsQuery.ID),
//                cursor.getString(ContactsQuery.LOOKUP_KEY));
//
//        // Notifies the parent activity that the user selected a contact. In a two-pane layout, the
//        // parent activity loads a ContactDetailFragment that displays the details for the selected
//        // contact. In a single-pane layout, the parent activity starts a new activity that
//        // displays contact details in its own Fragment.
//        mOnContactSelectedListener.onContactSelected(uri);
//
//        // If two-pane layout sets the selected item to checked so it remains highlighted. In a
//        // single-pane layout a new activity is started so this is not needed.
//        if (mIsTwoPaneLayout) {
//            getListView().setItemChecked(position, true);
//        }
//    }

    /**
     * Called when ListView selection is cleared, for example
     * when search mode is finished and the currently selected
     * contact should no longer be selected.
     */
//    private void onSelectionCleared() {
//        // Uses callback to notify activity this contains this fragment
//        mOnContactSelectedListener.onSelectionCleared();
//
//        // Clears currently checked item
//        getListView().clearChoices();
//    }

    // This method uses APIs from newer OS versions than the minimum that this app supports. This
    // annotation tells Android lint that they are properly guarded so they won't run on older OS
    // versions and can be ignored by lint.
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu items
        inflater.inflate(R.menu.contact_list_menu, menu);
        // Locate the search item
        MenuItem searchItem = menu.findItem(R.id.menu_search);

        // In versions prior to Android 3.0, hides the search item to prevent additional
        // searches. In Android 3.0 and later, searching is done via a SearchView in the ActionBar.
        // Since the search doesn't create a new Activity to do the searching, the menu item
        // doesn't need to be turned off.
//        if (mIsSearchResultView) {
//            searchItem.setVisible(false);
//        }

        // In version 3.0 and later, sets up and configures the ActionBar SearchView
        if (Utils.hasHoneycomb()) {

            // Retrieves the system search manager service
            final SearchManager searchManager =
                    (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

            // Retrieves the SearchView from the search menu item
            final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
//            final SearchView searchView = (SearchView) searchItem.getActionView();

            // Assign searchable info to SearchView
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getActivity().getComponentName()));

            // Set listeners for SearchView
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String queryText) {
                    // Nothing needs to happen when the user submits the search string
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // Called when the action bar search text has changed.  Updates
                    // the search filter, and restarts the loader to do a new query
                    // using the new search string.
                    String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

                    // Don't do anything if the filter is empty
                    if (mSearchTerm == null && newFilter == null) {
                        return true;
                    }

                    // Don't do anything if the new filter is the same as the current filter
                    if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
                        return true;
                    }

                    // Updates current filter to new filter
                    mSearchTerm = newFilter;

                    // Restarts the loader. This triggers onCreateLoader(), which builds the
                    // necessary content Uri from mSearchTerm.
//                    mSearchQueryChanged = true;
                    getLoaderManager().restartLoader(
                            ContactsQuery.QUERY_ID, null, ContactsFragment.this);
                    return true;
                }
            });

            if (Utils.hasICS()) {
                // This listener added in ICS
                MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        // Nothing to do when the action item is expanded
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        // When the user collapses the SearchView the current search string is
                        // cleared and the loader restarted.
//                        if (!TextUtils.isEmpty(mSearchTerm)) {
//                            onSelectionCleared();
//                        }
                        mSearchTerm = null;
                        getLoaderManager().restartLoader(
                                ContactsQuery.QUERY_ID, null, ContactsFragment.this);
                        return true;
                    }
                });
            }

            if (mSearchTerm != null) {
                // If search term is already set here then this fragment is
                // being restored from a saved state and the search menu item
                // needs to be expanded and populated again.

                // Stores the search term (as it will be wiped out by
                // onQueryTextChange() when the menu item is expanded).
                final String savedSearchTerm = mSearchTerm;

                // Expands the search menu item
                if (Utils.hasICS()) {
                    searchItem.expandActionView();
                }

                // Sets the SearchView to the previous search string
                searchView.setQuery(savedSearchTerm, false);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mSearchTerm)) {
            // Saves the current search string
            outState.putString(SearchManager.QUERY, mSearchTerm);

            // Saves the currently selected contact
//            outState.putInt(STATE_PREVIOUSLY_SELECTED_KEY, getListView().getCheckedItemPosition());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Sends a request to the People app to display the create contact screen
            case R.id.menu_add_contact:
                final Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                startActivity(intent);
                break;
            // For platforms earlier than Android 3.0, triggers the search activity
            case R.id.menu_search:
                if (!Utils.hasHoneycomb()) {
                    getActivity().onSearchRequested();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // If this is the loader for finding contacts in the Contacts Provider
        // (the only one supported)
        if (id == ContactsQuery.QUERY_ID) {
            Uri contentUri;

            // There are two types of searches, one which displays all contacts and
            // one which filters contacts by a search query. If mSearchTerm is set
            // then a search query has been entered and the latter should be used.

            if (mSearchTerm == null) {
                // Since there's no search string, use the content URI that searches the entire
                // Contacts table
                contentUri = ContactsQuery.CONTENT_URI;
            } else {
                // Since there's a search string, use the special content Uri that searches the
                // Contacts table. The URI consists of a base Uri and the search string.
                contentUri =
                        Uri.withAppendedPath(ContactsQuery.FILTER_URI, Uri.encode(mSearchTerm));
            }

            // Returns a new CursorLoader for querying the Contacts table. No arguments are used
            // for the selection clause. The search string is either encoded onto the content URI,
            // or no contacts search string is used. The other search criteria are constants. See
            // the ContactsQuery interface.
            return new CursorLoader(getContext(),
                    contentUri,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    null,
                    ContactsQuery.SORT_ORDER);
        }

        Log.e(TAG, "onCreateLoader - incorrect ID provided (" + id + ")");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Put the result Cursor in the adapter for the ListView
        if (loader.getId() == ContactsQuery.QUERY_ID) {
            mContactListView.setAdapter(new ContactsAdapter(data));

            // If this is a two-pane layout and there is a search query then
            // there is some additional work to do around default selected
            // search item.
//            if (mIsTwoPaneLayout && !TextUtils.isEmpty(mSearchTerm) && mSearchQueryChanged) {
//                // Selects the first item in results, unless this fragment has
//                // been restored from a saved state (like orientation change)
//                // in which case it selects the previously selected search item.
//                if (data != null && data.moveToPosition(mPreviouslySelectedSearchItem)) {
//                    // Creates the content Uri for the previously selected contact by appending the
//                    // contact's ID to the Contacts table content Uri
//                    final Uri uri = Uri.withAppendedPath(
//                            ContactsContract.Contacts.CONTENT_URI, String.valueOf(data.getLong(ContactsQuery.ID)));
//                    mOnContactSelectedListener.onContactSelected(uri);
//                    getListView().setItemChecked(mPreviouslySelectedSearchItem, true);
//                } else {
//                    // No results, clear selection.
//                    onSelectionCleared();
//                }
//                // Only restore from saved state one time. Next time fall back
//                // to selecting first item. If the fragment state is saved again
//                // then the currently selected item will once again be saved.
//                mPreviouslySelectedSearchItem = 0;
//                mSearchQueryChanged = false;
//            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        if (loader.getId() == ContactsQuery.QUERY_ID) {
//            // When the loader is being reset, clear the cursor from the adapter. This allows the
//            // cursor resources to be freed.
//            mAdapter.swapCursor(null);
//        }
    }
}
