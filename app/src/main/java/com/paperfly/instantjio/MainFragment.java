package com.paperfly.instantjio;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paperfly.instantjio.common.SectionsPagerAdapter;
import com.paperfly.instantjio.contact.ContactsFragment;
import com.paperfly.instantjio.event.EventCreateActivity;
import com.paperfly.instantjio.event.EventsFragment;
import com.paperfly.instantjio.group.GroupCreateActivity;
import com.paperfly.instantjio.group.GroupsFragment;

public class MainFragment extends Fragment implements ViewPager.OnPageChangeListener {
    public static final String TAG = MainFragment.class.getCanonicalName();
    private FloatingActionButton vFAB;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Set the action bar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        sectionsPagerAdapter.addFragment(new GroupsFragment(), "Groups");
        sectionsPagerAdapter.addFragment(new EventsFragment(), "Jio");
        sectionsPagerAdapter.addFragment(new ContactsFragment(), "Contacts");

        vFAB = (FloatingActionButton) view.findViewById(R.id.fab);

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(this);

        // Set up the TabLayout with the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);

        return view;
    }

    private void startGroupCreateActivity() {
        Intent intent = new Intent(getContext(), GroupCreateActivity.class);
        startActivity(intent);
    }

    private void startEventCreateActivity() {
        Intent intent = new Intent(getContext(), EventCreateActivity.class);
        startActivity(intent);
    }

    private void setFABForGroups() {
        vFAB.show();
        vFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGroupCreateActivity();
            }
        });
    }

    private void setFABForEvents() {
        vFAB.show();
        vFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEventCreateActivity();
            }
        });
    }

    private void setFABForContacts() {
        vFAB.hide();
    }

    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                setFABForGroups();
                break;
            case 1:
                setFABForEvents();
                break;
            case 2:
                setFABForContacts();
                break;
            default:
                vFAB.hide();
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    public void onPageScrollStateChanged(int state) {

    }
}
