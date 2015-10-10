package com.paperfly.instantjio.groups;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Firebase;
import com.paperfly.instantjio.R;

public class GroupsFragment extends Fragment {
    final static String TAG = GroupsFragment.class.getCanonicalName();

    Firebase ref;
    GroupsAdapter mAdapter;

//    public static GroupsFragment newInstance() {
//        GroupsFragment fragment = new GroupsFragment();
//        Bundle args = new Bundle();
//        args.put(ARG_PARAM1, param1);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref = new Firebase(getResources().getString(R.string.firebase_url));
        mAdapter = new GroupsAdapter(ref);
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
    public void onDestroy() {
        super.onDestroy();
        mAdapter.destroy();
    }
}
