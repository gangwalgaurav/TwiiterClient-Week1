package com.codepath.apps.tweets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.tweets.R;
import com.codepath.apps.tweets.TwitterApplication;
import com.codepath.apps.tweets.TwitterClient;
import com.codepath.apps.tweets.activity.FollowersActivity;
import com.codepath.apps.tweets.activity.FriendsListActivity;
import com.codepath.apps.tweets.activity.TweetListActivity;
import com.codepath.apps.tweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by gangwal on 3/1/15.
 */
public class UserHeaderFragment extends Fragment{

    TwitterClient client ;
    User user;
    ImageView ivProfileImage;
    ImageView ivBannerImage;
    TextView tvFullName;
    TextView tvTagLine;
    private Button mBtnTweetsCount;
    private Button mBtnFollowingCount;
    private Button mBtnFollowersCount;
    private Button mBtnEditProfile;
    private ImageButton mIBtnSettings;
    private ImageButton mIBtnNotifications;
    private Button mBtnFollowing;
    private final static String UID_HEADER="user_id";

    public static UserHeaderFragment newInstance(long user_id) {
        UserHeaderFragment fragment = new UserHeaderFragment();
        Bundle args = new Bundle();
        args.putLong(UID_HEADER, user_id);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_user_header,parent,false);
        setupViews(view);
        return view;
    }

    public void setupViews(View view){
        ivProfileImage = (ImageView)view.findViewById(R.id.ivProfileImage);
        ivBannerImage = (ImageView)view.findViewById(R.id.ivProfileBanner);
        tvFullName = (TextView)view.findViewById(R.id.tvName);
        tvTagLine = (TextView)view.findViewById(R.id.tvTagline);
        mBtnTweetsCount = (Button) view.findViewById(R.id.btnTweets);
        mBtnFollowingCount = (Button)view.findViewById(R.id.btnFollowing);
        mBtnFollowersCount = (Button) view.findViewById(R.id.btnFollowers);
        mBtnEditProfile = (Button) view.findViewById(R.id.btnEditProfile);
        mIBtnSettings = (ImageButton) view.findViewById(R.id.btnSettings);
        mIBtnNotifications = (ImageButton) view.findViewById(R.id.btnNotificationsIcon);
        mBtnFollowing = (Button) view.findViewById(R.id.btnFollowingIcon);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        if(getArguments()!=null){
            long user_id = getArguments().getLong(UID_HEADER);
            if(user_id==-1)
                client.getCurrentUserCredentials(userDetailsJSONHandler);
            else
                client.getUserInfo(user_id,userDetailsJSONHandler);
        }
    }

    private JsonHttpResponseHandler userDetailsJSONHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            user = User.fromJSON(response);
//                getSupportActionBar().setTitle("@"+user.getScreenName()); //TODO Probably need to create a method in interface
            populateProfileHeader(user);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
        }
    };



    private void populateProfileHeader(User user) {
        Picasso.with(getActivity()).load(user.getBannerImageUrl()).into(ivBannerImage);
        Picasso.with(getActivity()).load(user.getProfileImageUrl()).into(ivProfileImage);
        tvFullName.setText(user.getName());
        if(user.isVerified()){
            tvFullName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_profile_verified, 0);
        }else{
            tvFullName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        tvTagLine.setText(user.getTagline());

        mBtnTweetsCount.setText(Html.fromHtml("<b>" + user.getTweetsCount() + "</b><br>TWEETS"));
        mBtnTweetsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewTweetList(v);
            }
        });
        mBtnFollowingCount.setText(Html.fromHtml("<b>" + user.getFollowingCount() + "</b><br>FOLLOWING"));
        mBtnFollowingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewUserFriends(v);
            }
        });
        mBtnFollowersCount.setText(Html.fromHtml("<b>" + user.getFollowersCount() + "</b><br>FOLLOWERS"));
        mBtnFollowersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewUserFollowers(v);
            }
        });

        if (user.isCurrentUser()) {
            mBtnEditProfile.setVisibility(View.VISIBLE);
            mIBtnSettings.setVisibility(View.GONE);
            mIBtnNotifications.setVisibility(View.GONE);
            mBtnFollowing.setVisibility(View.GONE);
        } else {
            mBtnEditProfile.setVisibility(View.GONE);
            mIBtnSettings.setVisibility(View.VISIBLE);
            mIBtnNotifications.setVisibility(View.VISIBLE);
            if (user.isNotifications()) {
                mIBtnNotifications.setImageResource(R.drawable.ic_tweet_action_inline_favorite_on);
            } else {
                mIBtnNotifications.setImageResource(R.drawable.ic_tweet_action_inline_favorite_off);
            }

            mBtnFollowing.setVisibility(View.VISIBLE);
            if (user.isFollowing()) {
                mBtnFollowing.setText("Following");
                mBtnFollowing.setBackgroundResource((R.drawable.rounded_tweet_button));
                mBtnFollowing.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tweet_action_inline_follow_off, 0, 0, 0);
                mBtnFollowing.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                mBtnFollowing.setText("Follow");
                mBtnFollowing.setBackgroundResource((R.drawable.rounded_blue_edge_button));
                mBtnFollowing.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_action_default, 0, 0, 0);
                mBtnFollowing.setTextColor(getResources().getColor(R.color.simple_blue));
            }
        }
    }

    public void onViewUserFriends(View view) {
        Intent intent = new Intent(getActivity(), FriendsListActivity.class);
        intent.putExtra("user_id", user.getUid());
        startActivity(intent);
    }

    public void onViewUserFollowers(View view) {
        Intent intent = new Intent(getActivity(), FollowersActivity.class);
        intent.putExtra("user_id", user.getUid());
        startActivity(intent);
    }

    public void onViewTweetList(View view) {
        Intent intent = new Intent(getActivity(), TweetListActivity.class);
        intent.putExtra("user_id", user.getUid());
        startActivity(intent);
    }
}


