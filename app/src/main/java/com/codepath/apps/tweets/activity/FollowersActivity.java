package com.codepath.apps.tweets.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.codepath.apps.tweets.R;
import com.codepath.apps.tweets.fragments.PeopleListFragment;

public class FollowersActivity extends PeopleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Followers");
        mUid = getIntent().getLongExtra("uid",-1);
        if(savedInstanceState==null) {
            PeopleListFragment followerListFragment = PeopleListFragment.newInstance("followers",mUid);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer,followerListFragment);
            ft.commit();
        }
    }

}
