package com.codepath.apps.tweets.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.tweets.R;
import com.codepath.apps.tweets.TwitterApplication;
import com.codepath.apps.tweets.TwitterClient;
import com.codepath.apps.tweets.fragments.ComposeDialog;
import com.codepath.apps.tweets.fragments.TweetListFragment;


public class SearchActivity extends ActionBarActivity implements ComposeDialog.onTweetListener, ActionBar.TabListener {

    private static final String TAG = SearchActivity.class.getName().toString();
    private TwitterClient client;
    private SearchView searchView;
    private static String searchString;
    private TweetsPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Intent intent = getIntent();
        searchString = intent.getStringExtra("search_string");
        client = TwitterApplication.getRestClient();

        // Set up the action bar.
//        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new TweetsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        PagerSlidingTabStrip tabsTrip = (PagerSlidingTabStrip)findViewById(R.id.tabs);
        tabsTrip.setTextColor(getResources().getColor(R.color.simple_blue));
        tabsTrip.setIndicatorColor(Color.parseColor("#ffbbddf5"));
        tabsTrip.setUnderlineHeight(2);
        tabsTrip.setUnderlineColor(Color.parseColor("#FFEDEDED"));
        tabsTrip.setIndicatorHeight(20);
        tabsTrip.setBackgroundColor(Color.WHITE);
        viewPager.setOffscreenPageLimit(2);
        tabsTrip.setViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchString = query;
                adapter.notifyDataSetChanged();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        if (!TextUtils.isEmpty(searchString)) {
            searchItem.expandActionView();
            searchView.setQuery(searchString, false);
            searchView.requestFocus();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTweetSubmit() {

    }
    public class TweetsPagerAdapter extends FragmentPagerAdapter{

        public TweetsPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        public String tabTitles[] = {"Top Tweets","All Tweets"};

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                default:
                    Fragment fragment = TweetListFragment.newInstance(tabTitles[position]);
                    fragment.getArguments().putString("search_string", searchString);
                    return fragment;

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
