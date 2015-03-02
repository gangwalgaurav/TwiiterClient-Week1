package com.codepath.apps.tweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.tweets.R;
import com.codepath.apps.tweets.TwitterApplication;
import com.codepath.apps.tweets.TwitterClient;
import com.codepath.apps.tweets.adapters.PeopleArrayAdapter;
import com.codepath.apps.tweets.helpers.EndlessScrollListener;
import com.codepath.apps.tweets.helpers.Utilities;
import com.codepath.apps.tweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gangwal on 3/2/15.
 */
public class PeopleListFragment extends Fragment{

    private static final String TAG = PeopleListFragment.class.getSimpleName();
    private ImageView ivProfilePicture;
    private TextView tvUserName;
    private TextView tvScreenName;
    private TextView description;
    private ImageView ivFollowBtn;
    private ListView lvUsers;

    private SwipeRefreshLayout swipeContainer;
    private PeopleArrayAdapter aUsers;
    private ArrayList<User> users;


    private TwitterClient client;
    private String max;
    private final static String UID_HEADER="user_id";
    private final static String PEOPLE_TYPE="people_type";

    public static PeopleListFragment newInstance(String listType,long user_id) {
        PeopleListFragment fragment = new PeopleListFragment();
        Bundle args = new Bundle();
        args.putLong(UID_HEADER, user_id);
        args.putString(PEOPLE_TYPE,listType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        users = new ArrayList<User>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_user_list,parent,false);
        lvUsers = (ListView) view.findViewById(R.id.lvUsers);
        lvUsers.setAdapter(aUsers);

        aUsers = new PeopleArrayAdapter(getActivity(),users);
        lvUsers.setAdapter(aUsers);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateUserList(null);
                swipeContainer.setRefreshing(false);

            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvUsers.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if(users.isEmpty())
                    populateUserList(null);//If this is called, that means we are scrolling on blank lv
                else {
                    long maxId = users.get(users.size() - 1).getUid();
                    populateUserList(Long.toString(maxId - 1));
                }
            }
        });

        populateUserList(null);
        return view;
    }

    private void populateUserList(final String maxId) {

        boolean isNetworkAvailable = Utilities.isNetworkAvailable(getActivity());
        max = maxId;
        if(getArguments()!=null) {
            long user_id = getArguments().getLong(UID_HEADER);
            String userListType = getArguments().getString(PEOPLE_TYPE);
            if (isNetworkAvailable) {
                if (userListType.equals("friends"))//TODO make enums
                    client.getFriends(user_id, "100", userListJSONHandler);
                else if (userListType.equals("followers"))
                    client.getFollowers(user_id, "100", userListJSONHandler);
            }
            else{
                //GET DATA OFFLINE
                Toast.makeText(getActivity(), R.string.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }


    public JsonHttpResponseHandler userListJSONHandler = new JsonHttpResponseHandler(){

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            {
                try {
                    aUsers.addAll(User.fromJSONArray(response.getJSONArray("users")));
                    aUsers.notifyDataSetChanged();
//                    writeToFile(response.toString(2));//TODO Need to move it to a helper method
//                    Log.i(TAG, "Got the result");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
            {
                try {
//                    writeToFile(errorResponse.toString(2));
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


}
