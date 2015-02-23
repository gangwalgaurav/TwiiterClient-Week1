package com.codepath.apps.tweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.tweets.R;
import com.codepath.apps.tweets.activity.ImageDisplayActivity;
import com.codepath.apps.tweets.activity.TweetActivity;
import com.codepath.apps.tweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by gangwal on 2/21/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{

    private class ViewHolder{
        ImageView ivProfilePicture;
        TextView tvUserName;
        TextView tvBody;
        TextView tvCreatedTime;
        TextView tvScreenName;
        ImageView ivMedia;
        ImageView ivRetweetedIcon;
        TextView tvRetweetedUserName;
        Button btRetweetedIcon;
        TextView tvRetweetedCount;
        Button btFavouriteIcon;
        TextView tvFavouriteCount;
        Button btFollowingIcon;

    }


    Tweet tweet;
    private final int REQUEST_CODE = 20;
    public long uuid;

    public TweetsArrayAdapter(Context context, List<Tweet> tweets){
        super(context, R.layout.item_tweet,tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        tweet = getItem(position);
        uuid = tweet.getUid();
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet,parent,false);
            viewHolder.ivProfilePicture = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvCreatedTime = (TextView)convertView.findViewById(R.id.tvCreatedAt);
            viewHolder.tvScreenName = (TextView)convertView.findViewById(R.id.tvScreenName);
            viewHolder.ivMedia = (ImageView)convertView.findViewById(R.id.ivMediaImage);
            viewHolder.ivRetweetedIcon = (ImageView)convertView.findViewById(R.id.ivRetweetUserIcon);
            viewHolder.tvRetweetedUserName = (TextView)convertView.findViewById(R.id.tvRetweetedUserName);

            viewHolder.btRetweetedIcon = (Button)convertView.findViewById(R.id.btRetweet);
            viewHolder.btFavouriteIcon = (Button)convertView.findViewById(R.id.btFavorite);
            viewHolder.btFollowingIcon = (Button)convertView.findViewById(R.id.btFollow);
            convertView.setTag(viewHolder);
        }  else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvUserName.setText(Html.fromHtml("<b>" +tweet.getUser().getName()+"</b>"));
        viewHolder.tvBody.requestLayout();

        viewHolder.tvBody.setText(Html.fromHtml(tweet.getBody()));
        viewHolder.tvBody.requestLayout();
        viewHolder.ivProfilePicture.setImageResource(android.R.color.transparent);
        viewHolder.tvCreatedTime.setText(getRelativeTimeAgo(tweet.getCreatedAtMillis()));
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.ivProfilePicture);
        viewHolder.tvScreenName.setText("@"+tweet.getUser().getScreenName());
        if(TextUtils.isEmpty(tweet.getMediaURL())){
            viewHolder.ivMedia.setVisibility(View.GONE);
        }
        else{
            viewHolder.ivMedia.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(tweet.getMediaURL()).into(viewHolder.ivMedia);
            viewHolder.ivMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ImageDisplayActivity.class);
                    String imageInfo = tweet.getMediaURL();
                    intent.putExtra("imageInfo", imageInfo);
                    getContext().startActivity(intent);
                }
            });
        }

        if(TextUtils.isEmpty(tweet.getRetweetedUserName()))
        {
            viewHolder.ivRetweetedIcon.setVisibility(View.GONE);
            viewHolder.tvRetweetedUserName.setVisibility(View.GONE);
        }
        else{
            viewHolder.ivRetweetedIcon.setVisibility(View.VISIBLE);
            viewHolder.tvRetweetedUserName.setVisibility(View.VISIBLE);
            viewHolder.tvRetweetedUserName.setText(tweet.getRetweetedUserName() + " retweeted");
        }

        //Starting a new Activity
        viewHolder.tvBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailedTweet(tweet.getIdStr());
            }
        });

        viewHolder.btFollowingIcon.setVisibility(tweet.getUser().isFollowing()? (View.GONE) : (View.VISIBLE));
        viewHolder.btRetweetedIcon.setText(NumberFormat.getNumberInstance(Locale.US).format(tweet.getRetweetCount()));
        Drawable retweeted = tweet.isRetweeted()?getContext().getResources().getDrawable(R.drawable.ic_tweet_action_inline_retweet_on):getContext().getResources().getDrawable(R.drawable.ic_tweet_action_inline_retweet_off);
        viewHolder.btRetweetedIcon.setCompoundDrawablesWithIntrinsicBounds(retweeted,null,null,null);

        viewHolder.btFavouriteIcon.setText(NumberFormat.getNumberInstance(Locale.US).format(tweet.getFavoriteCount()));
        Drawable favortited = tweet.isFavorited()?getContext().getResources().getDrawable(R.drawable.ic_tweet_action_inline_favorite_on):getContext().getResources().getDrawable(R.drawable.ic_tweet_action_inline_favorite_off);
        viewHolder.btFavouriteIcon.setCompoundDrawablesWithIntrinsicBounds(favortited,null,null,null);
        return convertView;
    }

    private void showDetailedTweet(String idStr) {

        Intent i = new Intent(this.getContext(), TweetActivity.class);
        Log.i("ID", "ID " + idStr);

        i.putExtra("id", idStr); // pass arbitrary data to launched activity
        getContext().startActivity(i);
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(long dateMillis) {
        String relativeDate="";
        if(dateMillis<0)
            return  relativeDate;
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        String refCratedTimeString = relativeDate.toString().replace(" ago","");
        refCratedTimeString = refCratedTimeString.replace(" days","d");
        refCratedTimeString = refCratedTimeString.replace(" day","d");
        refCratedTimeString = refCratedTimeString.replace(" hours","h");
        refCratedTimeString = refCratedTimeString.replace(" hour","h");
        refCratedTimeString = refCratedTimeString.replace(" minutes","m");
        refCratedTimeString = refCratedTimeString.replace(" minute","m");
        return refCratedTimeString;
    }
}
