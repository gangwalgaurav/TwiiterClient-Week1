package com.codepath.apps.tweets.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.codepath.apps.tweets.R;
import com.codepath.apps.tweets.TwitterApplication;
import com.codepath.apps.tweets.TwitterClient;
import com.codepath.apps.tweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.tweets.fragments.ComposeDialog;
import com.codepath.apps.tweets.helpers.EndlessScrollListener;
import com.codepath.apps.tweets.helpers.Utilities;
import com.codepath.apps.tweets.models.Tweet;
import com.codepath.apps.tweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends ActionBarActivity implements ComposeDialog.onTweetListener{

    private TwitterClient client;
    private static final String TAG = TimelineActivity.class.getSimpleName();
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;
    private SwipeRefreshLayout swipeContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<Tweet>();
        aTweets = new TweetsArrayAdapter(this,tweets);
        lvTweets.setAdapter(aTweets);
        client = TwitterApplication.getRestClient();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(null);
                swipeContainer.setRefreshing(false);

            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if(tweets.isEmpty())
                    populateTimeline(null);//If this is called, that means we are scrolling on blank lv
                else {
                    long maxId = tweets.get(tweets.size() - 1).getUid();
                    populateTimeline(Long.toString(maxId - 1));
                }
            }
        });
        populateTimeline(null);
    }

    private void populateTimeline(final String maxId) {
        if(!Utilities.isNetworkAvailable(this)){
            Toast.makeText(this,R.string.NO_INTERNET_CONNECTION,Toast.LENGTH_SHORT).show();
            return;
        }
        client.getHomeTimeline(maxId,new JsonHttpResponseHandler(){
            //Success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    if (TextUtils.isEmpty(maxId)) {
                        new Delete().from(Tweet.class).execute();
                        new Delete().from(User.class).execute();
                        aTweets.clear();
                    }
                    aTweets.addAll(Tweet.fromJsonArray(response));
                    for (int i = 0; i < tweets.size(); i++) {
                        tweets.get(i).getUser().save();
                        tweets.get(i).save();
                        Log.e("SAVED TWEET", tweets.get(i).toString());
                    }
                    writeToFile(response.toString(2));
                    Log.i(TAG,"Got the result");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    Log.e(TAG,"Failure "  + errorResponse.toString());
                    Toast.makeText(TimelineActivity.this,"Failure" + errorResponse.toString(),Toast.LENGTH_SHORT).show();
                    List<Tweet> cachedTweets = new Select()
                            .from(Tweet.class)
                            .orderBy("uid DESC")
                            .execute();
                    aTweets.addAll(cachedTweets);
                    swipeContainer.setRefreshing(false);
//                    writeToFile(errorResponse.toString(2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("json.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Log.i(TAG, "File Written");
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
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
            Toast.makeText(this,"Messages button is not implemented yet!!",Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.search) {
            Toast.makeText(this,"Search button is not implemented yet!!",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showComposeDialog() {
        ComposeDialog compose = ComposeDialog.getInstance(getSupportFragmentManager());
        compose.show(getSupportFragmentManager(),"");
    }

    @Override
    public void onTweetSubmit() {
        populateTimeline(null);
    }
}
