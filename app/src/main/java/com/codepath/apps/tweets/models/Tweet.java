package com.codepath.apps.tweets.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by gangwal on 2/21/15.
 */
public class Tweet {

    private String body;
    private long uid;
    private String idStr;
    private String createdAt;
    private User user;
    private String mediaURL;
    private String retweetedUserName;
    private long createdAtMillis;
    private static String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
    private static SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);

    private Boolean isRetweeted,isFavorited;
    private int retweetCount,favoriteCount;


    public static Tweet fromJson(JSONObject tweetJson){

        Tweet tweet = new Tweet();
        try {
            //TODO remove the urls
            tweet.body = tweetJson.getString("text").replace("\n", "<br>");

            tweet.createdAt = tweetJson.getString("created_at");
            tweet.uid = tweetJson.getLong("id");
            tweet.idStr = tweetJson.getString("id_str");
            tweet.user = User.fromJson(tweetJson.getJSONObject("user"));
            tweet.setCreatedAtMillis(tweet.createdAt);

            if(!tweetJson.getJSONObject("entities").isNull("media")){
                //TODO add multiple images if exists.
                tweet.mediaURL = tweetJson.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url");
            }

            if(!tweetJson.isNull("retweeted_status")){
                JSONObject retweetedStatus = tweetJson.getJSONObject("retweeted_status");
                tweet.body = retweetedStatus.getString("text").replace("\n", "<br>");
                tweet.retweetedUserName = tweet.user.getName();
                tweet.user = User.fromJson(retweetedStatus.getJSONObject("user"));
            }
            tweet.isRetweeted = tweetJson.getBoolean("retweeted");
            tweet.retweetCount = tweetJson.getInt("retweet_count");
            tweet.isFavorited = tweetJson.getBoolean("favorited");
            tweet.favoriteCount = tweetJson.getInt("favorite_count");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJsonArray(JSONArray tweetsJson) {

        ArrayList<Tweet> tweets = new ArrayList<Tweet>();

        for(int i=0;i<tweetsJson.length();i++){
            JSONObject tweetJson = null;
            try {
                tweetJson = tweetsJson.getJSONObject(i);
                tweets.add(fromJson(tweetJson));
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }

        public String getBody() {
        return body;
    }

    public long getCreatedAtMillis() {
        return createdAtMillis;
    }

    public void setCreatedAtMillis(String rawJsonDate) {
        long dateMillis=-1;
        try {
            sf.setLenient(true);
            dateMillis = sf.parse(rawJsonDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.createdAtMillis = dateMillis;
    }

    public long getUid() {
        Log.i("GET UID:::",""+uid);
        return uid;
    }

    public String getIdStr() {
        return idStr;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public String getRetweetedUserName() {
        return retweetedUserName;
    }

    public Boolean isFavorited(){ return isFavorited; }

    public Boolean isRetweeted(){ return isRetweeted; }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }
}
