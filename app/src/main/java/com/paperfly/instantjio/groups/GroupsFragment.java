package com.paperfly.instantjio.groups;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Firebase;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.common.ChooserEventListener;

import java.util.ArrayList;

public class GroupsFragment extends Fragment {
    private static final String TAG = GroupsFragment.class.getCanonicalName();
    private static final String CHOOSING_MODE = "CHOOSING_MODE";
    private static final String CHOSEN_GROUPS = "CHOSEN_GROUPS";
    private boolean mChoosingMode;
    private ArrayList<String> mChosenGroups;
    private GroupsAdapter mAdapter;
    private ChooserEventListener.ItemInteraction mItemInteraction;

    public GroupsFragment() {

    }

    public static GroupsFragment newInstance(boolean choosingMode, ArrayList<String> chosenGroups) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putBoolean(CHOOSING_MODE, choosingMode);
        args.putSerializable(CHOSEN_GROUPS, chosenGroups);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Firebase ref = new Firebase(getString(R.string.firebase_url));
        mAdapter = new GroupsAdapter(ref, mChosenGroups);
        mAdapter.setChooserListener(mItemInteraction);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getArguments() != null) {
            mChoosingMode = getArguments().getBoolean(CHOOSING_MODE);
            mChosenGroups = (ArrayList<String>) getArguments().getSerializable(CHOSEN_GROUPS);
        }

        if (mChoosingMode) {
            try {
                mItemInteraction = (ChooserEventListener.ItemInteraction) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement mOnItemClickListener");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.group_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.groups_menu, menu);
        menu.findItem(R.id.menu_choose).setEnabled(mChoosingMode);
        menu.findItem(R.id.menu_choose).setVisible(mChoosingMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // For platforms earlier than Android 3.0, triggers the search activity
            case R.id.menu_search:
//                if (!Utils.hasHoneycomb()) {
//                    getActivity().onSearchRequested();
//                }
                break;
            case R.id.menu_choose:
                mItemInteraction.onConfirmSelection();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.destroy();
    }
}
