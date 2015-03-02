package com.codepath.apps.tweets.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.tweets.R;
import com.codepath.apps.tweets.fragments.ComposeDialog;
import com.codepath.apps.tweets.fragments.TweetListFragment;
import com.codepath.apps.tweets.fragments.UserHeaderFragment;


public class ProfileActivity extends ActionBarActivity implements ComposeDialog.onTweetListener {

    long mUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile");
//        String screenName = getIntent().getStringExtra("screen_name");
        Boolean showCurrentUser = getIntent().getBooleanExtra("showCurrentUser",false);
        mUid = getIntent().getLongExtra("uid",-1);
        //TODO check if need to show current user or other user. Also take care of error condition when uid is not valid.
        if(!showCurrentUser && mUid==-1)
            //Error Condition, need to handle this later.
            return;
        if(savedInstanceState==null) {
//            View header = findViewById(R.id.flUserHeader);
            UserHeaderFragment userHeaderFragment = UserHeaderFragment.newInstance(mUid);
            TweetListFragment tweetListFragment = TweetListFragment.newInstance("Tweets",mUid);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flUserHeader,userHeaderFragment);
            ft.replace(R.id.flContainer, tweetListFragment);
            ft.commit();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTweetSubmit() {
        //TODO
    }
}
