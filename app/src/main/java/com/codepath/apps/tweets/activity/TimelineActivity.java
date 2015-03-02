package com.codepath.apps.tweets.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.tweets.R;
import com.codepath.apps.tweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.tweets.fragments.ComposeDialog;
import com.codepath.apps.tweets.fragments.TweetListFragment;

public class TimelineActivity extends ActionBarActivity implements ComposeDialog.onTweetListener{

    private static final String TAG = TimelineActivity.class.getSimpleName();
    private TweetListFragment fragmentTweetList;
    private TweetsArrayAdapter aTweets;
    private SearchView searchView;
    private String searchString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip tabsTrip = (PagerSlidingTabStrip)findViewById(R.id.tabs);
        tabsTrip.setTextColor(getResources().getColor(R.color.simple_blue));
        tabsTrip.setIndicatorColor(Color.parseColor("#ffbbddf5"));
        tabsTrip.setUnderlineHeight(2);
        tabsTrip.setUnderlineColor(Color.parseColor("#FFEDEDED"));
        tabsTrip.setIndicatorHeight(20);
        tabsTrip.setBackgroundColor(Color.WHITE);
        tabsTrip.setViewPager(viewPager);




//        if(savedInstanceState==null)
//            fragmentTweetList = (TweetListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
//            aTweets = fragmentTweetList.getArrayAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        MenuItem searchItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchString = query;
                Intent intent = new Intent(TimelineActivity.this, SearchActivity.class);
                intent.putExtra("search_string", searchString);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.compose) {
            showComposeDialog();
            return true;
        }
        else if (id == R.id.notifications) {
            Toast.makeText(this,"Notification button is not implemented yet!!",Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.messages) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("showCurrentUser",true);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showComposeDialog() {
        ComposeDialog compose = ComposeDialog.getInstance(getResources().getString(R.string.Compose_Tweet_hint),getSupportFragmentManager());
        compose.show(getSupportFragmentManager(),"");
    }
    @Override
    public void onTweetSubmit() {
//        populateTimeline(null);
    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter{

        public TweetsPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        public String tabTitles[] = {"Home","Mentions","Tweets"};

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0://TODO, enum or something instead of numbers.
                    return TweetListFragment.newInstance(tabTitles[position]);
                case 1:
                    return TweetListFragment.newInstance(tabTitles[position]);
                default:
                    return TweetListFragment.newInstance(tabTitles[position]);
            }
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
