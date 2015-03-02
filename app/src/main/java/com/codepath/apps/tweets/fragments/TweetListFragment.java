package com.codepath.apps.tweets.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.tweets.R;
import com.codepath.apps.tweets.TwitterApplication;
import com.codepath.apps.tweets.TwitterClient;
import com.codepath.apps.tweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.tweets.helpers.EndlessScrollListener;
import com.codepath.apps.tweets.helpers.Utilities;
import com.codepath.apps.tweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gangwal on 2/28/15.
 */
public class TweetListFragment extends Fragment {

    private static final String TAB_TYPE = "tabType";
    private static final String USER_ID = "user_id";
    private String mUserId;

    private TwitterClient client;
    private static final String TAG = TweetListFragment.class.getSimpleName();
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;
    private SwipeRefreshLayout swipeContainer;
    private String mTabName;
    private int mTabType;
    private static final int HOME_TWEET_TYPE = 0;//TODO Use enum
    private static final int MENTIONS_TWEET_TYPE = 1;
    private static final int USER_TWEET_TYPE = 2;
    private static final int SEARCH_TOP_TYPE = 3;
    private static final int SEARCH_ALL_TYPE = 4;

    private String max;
    private final static String UID_HEADER="user_id";
    static View header;
    private String searchString;

    public static TweetListFragment newInstance(String tabName){
        return newInstance(tabName,-1);
    }

    public static TweetListFragment newInstance(String tabName,long user_id){
        TweetListFragment fragment = new TweetListFragment();
//        header = view;
        Bundle args = new Bundle();
        args.putString(TAB_TYPE,tabName);
        args.putLong(UID_HEADER, user_id);
        fragment.setArguments(args);
        return fragment;
    }

    public JsonHttpResponseHandler tweetJSONHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            {
                try {
                    if (TextUtils.isEmpty(max)) {
//                        new Delete().from(Tweet.class).execute();
//                        new Delete().from(User.class).execute();
                        aTweets.clear();
                    }
                    aTweets.addAll(Tweet.fromJsonArray(response));
                    aTweets.notifyDataSetChanged();

//                    for (int i = 0; i < tweets.size(); i++) {
//                        tweets.get(i).getUser().save();
//                        tweets.get(i).save();
//                        Log.e("SAVED TWEET", tweets.get(i).toString());
//                    }
                    writeToFile(response.toString(2));
//                    Log.i(TAG, "Got the result");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            {
                try {
                    if (TextUtils.isEmpty(max)) {
//                        new Delete().from(Tweet.class).execute();
//                        new Delete().from(User.class).execute();
                        aTweets.clear();
                    }
                    aTweets.addAll(Tweet.fromJsonArray(response.getJSONArray("statuses")));
                    aTweets.notifyDataSetChanged();

//                    for (int i = 0; i < tweets.size(); i++) {
//                        tweets.get(i).getUser().save();
//                        tweets.get(i).save();
//                        Log.e("SAVED TWEET", tweets.get(i).toString());
//                    }
                    writeToFile(response.toString(2));
//                    Log.i(TAG, "Got the result");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
            {
                try {
                    writeToFile(errorResponse.toString(2));
                    Log.e(TAG, "Failure " + errorResponse.toString());
                    Toast.makeText(getActivity(), "Failure" + errorResponse.toString(), Toast.LENGTH_SHORT).show();
//                    List<Tweet> cachedTweets = new Select()
//                            .from(Tweet.class)
//                            .orderBy("uid DESC")
//                            .execute();
//                    aTweets.addAll(cachedTweets);
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            mTabName = getArguments().getString(TAB_TYPE);
            if(mTabName.equals("Home"))
                mTabType = HOME_TWEET_TYPE;
            else if(mTabName.equals("Mentions"))
                mTabType = MENTIONS_TWEET_TYPE;
            else if(mTabName.equals("Tweets"))
                mTabType = USER_TWEET_TYPE;
            else if(mTabName.equals("Top Tweets"))
                mTabType = SEARCH_TOP_TYPE;
            else if(mTabName.equals("All Tweets"))
                mTabType = SEARCH_ALL_TYPE;
            mUserId = Long.toString(getArguments().getLong(USER_ID));
            searchString = getArguments().getString("search_string");

        }
        else{/*Some Error*/}
        tweets = new ArrayList<Tweet>();
        client = TwitterApplication.getRestClient();
        populateTweets(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_tweets_list,parent,false);
        lvTweets = (ListView) view.findViewById(R.id.lvTweets);
        View v = inflater.inflate(R.layout.progress, null);
        lvTweets.addFooterView(v);

//        View header;//= getLayoutInflater().inflate(R.layout.fragment_user_header, null);
//        header = view.findViewById(R.id.flUserHeader);
//        if(header!=null)
//            lvTweets.addHeaderView(header);
//        else
//        Toast.makeText(getActivity(),"Header Null",Toast.LENGTH_SHORT).show();

        aTweets = new TweetsArrayAdapter(getActivity(),tweets);
        lvTweets.setAdapter(aTweets);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTweets(null);
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
                    populateTweets(null);//If this is called, that means we are scrolling on blank lv
                else {
                    long maxId = tweets.get(tweets.size() - 1).getUid();
                    populateTweets(Long.toString(maxId - 1));
                }
            }
        });

        return view;

    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    private void populateTweets(final String maxId) {

        boolean isNetworkAvailable = Utilities.isNetworkAvailable(getActivity());
        max = maxId;
        if(mTabType==HOME_TWEET_TYPE) {
            if (isNetworkAvailable)
                client.getHomeTimeline(maxId, tweetJSONHandler);
            else {
                //GET DATA OFFLINE
                Toast.makeText(getActivity(), R.string.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                return;
            }
        }else if(mTabType==MENTIONS_TWEET_TYPE) {
            if (isNetworkAvailable)
                client.getMetionsTimeline(maxId, tweetJSONHandler);
            else {
                //GET DATA OFFLINE
                Toast.makeText(getActivity(), R.string.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                return;
            }
        }else if(mTabType==USER_TWEET_TYPE) {
            if (isNetworkAvailable)
                client.getUserTimeline(maxId,mUserId, tweetJSONHandler);
            else {
                //GET DATA OFFLINE
                Toast.makeText(getActivity(), R.string.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                return;
            }
        }else if(mTabType==SEARCH_TOP_TYPE) {
            if (isNetworkAvailable) {
                client.getSearchTopTweets(searchString, max, tweetJSONHandler);
            } else {
                Toast.makeText(getActivity(), "No Internet connection. Please try again later.", Toast.LENGTH_LONG).show();
            }
        } else if(mTabType==SEARCH_ALL_TYPE) {
            if (isNetworkAvailable) {
                client.getSearchAllTweets(searchString, max, tweetJSONHandler);
            } else {
                Toast.makeText(getActivity(), "No Internet connection. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }

//        client.getHomeTimeline(maxId,new JsonHttpResponseHandler(){
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                try {
//                    if (TextUtils.isEmpty(maxId)) {
//                        new Delete().from(Tweet.class).execute();
//                        new Delete().from(User.class).execute();
//                        aTweets.clear();
//                    }
//                    aTweets.addAll(Tweet.fromJsonArray(response));
//                    for (int i = 0; i < tweets.size(); i++) {
//                        tweets.get(i).getUser().save();
//                        tweets.get(i).save();
//                        Log.e("SAVED TWEET", tweets.get(i).toString());
//                    }
//                    writeToFile(response.toString(2));
//                    Log.i(TAG, "Got the result");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                try {
//                    Log.e(TAG,"Failure "  + errorResponse.toString());
//                    Toast.makeText(getActivity(), "Failure" + errorResponse.toString(), Toast.LENGTH_SHORT).show();
//                    List<Tweet> cachedTweets = new Select()
//                            .from(Tweet.class)
//                            .orderBy("uid DESC")
//                            .execute();
//                    aTweets.addAll(cachedTweets);
//                    swipeContainer.setRefreshing(false);
//                    writeToFile(errorResponse.toString(2));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().openFileOutput(mTabName+"json.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Log.i(TAG, "File Written");
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void addAll(List<Tweet> tweets){aTweets.addAll(tweets);}
    public TweetsArrayAdapter getArrayAdapter(){return aTweets;}
}
