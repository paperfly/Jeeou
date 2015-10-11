package com.paperfly.instantjio.groups;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.paperfly.instantjio.R;
import com.paperfly.instantjio.common.ChooserEventListener;

import java.util.ArrayList;

public class GroupsChooserActivity extends AppCompatActivity implements ChooserEventListener.ItemInteraction {
    private static final String TAG = GroupsChooserActivity.class.getCanonicalName();
    private static final String CHOSEN_GROUPS = "CHOSEN_GROUPS";
    private ArrayList<String> mChosenGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_chooser);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            return;
        }

        mChosenGroups = new ArrayList<>();

        if (getIntent().getExtras() != null) {
            mChosenGroups = (ArrayList<String>) getIntent().getSerializableExtra(CHOSEN_GROUPS);
        }

        GroupsFragment groupsFragment = GroupsFragment.newInstance(true, mChosenGroups); // true = choosing mode
        getSupportFragmentManager().beginTransaction().add(R.id.groups_chooser_container, groupsFragment).commit();
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

    @Override
    public void onItemClick(String s) {
        if (mChosenGroups.contains(s)) {
            mChosenGroups.remove(s);
        } else {
            mChosenGroups.add(s);
        }
    }

    @Override
    public void onConfirmSelection() {
        if (mChosenGroups == null)
            finish();

        final Intent intent = new Intent();
        intent.putExtra(CHOSEN_GROUPS, mChosenGroups);
        setResult(RESULT_OK, intent);
        finish();
    }
}
