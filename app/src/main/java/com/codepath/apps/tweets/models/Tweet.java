package com.codepath.apps.tweets.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

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
@Table(name = "Tweets")
public class Tweet extends Model implements Parcelable{
    @Column(name = "body")
    private String body;
    @Column(name = "uid", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;
    @Column(name = "idStr")
    private String idStr;
    @Column(name = "createdAt")
    private String createdAt;
    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;
    @Column(name = "mediaURL")
    private String mediaURL;
    @Column(name = "retweetedUserName")
    private String retweetedUserName;
    @Column(name = "createdAtMillis")
    private long createdAtMillis;
    private static String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
    private static SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
    @Column(name = "isRetweeted")
    private Boolean isRetweeted;
    @Column(name = "isFavorited")
    private Boolean isFavorited;
    @Column(name = "retweetCount")
    private int retweetCount;
    @Column(name = "favoriteCount")
    private int favoriteCount;

    public Tweet() {

    }


    public static Tweet fromJson(JSONObject tweetJson){

        Tweet tweet = new Tweet();
        try {
            //TODO remove the urls
            tweet.body = tweetJson.getString("text").replace("\n", "<br>");

            tweet.createdAt = tweetJson.getString("created_at");
            tweet.uid = tweetJson.getLong("id");
            Log.i("Model", "ID: " + tweet.uid + "\tBody " + tweet.body);
            tweet.idStr = tweetJson.getString("id_str");
            tweet.user = User.fromJSON(tweetJson.getJSONObject("user"));
            tweet.setCreatedAtMillis(tweet.createdAt);

            if(!tweetJson.getJSONObject("entities").isNull("media")){
                //TODO add multiple images if exists.
                tweet.mediaURL = tweetJson.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url");
            }

            if(!tweetJson.isNull("retweeted_status")){
                JSONObject retweetedStatus = tweetJson.getJSONObject("retweeted_status");
                tweet.body = retweetedStatus.getString("text").replace("\n", "<br>");
                tweet.retweetedUserName = tweet.user.getName();
                tweet.user = User.fromJSON(retweetedStatus.getJSONObject("user"));
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

    private Tweet(Parcel source) {
        this.body = source.readString();
        this.createdAt = source.readString();
        this.uid = source.readLong();
        boolean[] boolValues = new boolean[2];
        source.readBooleanArray(boolValues);
        this.isFavorited = boolValues[0];
        this.isRetweeted = boolValues[1];
        this.user = source.readParcelable(User.class.getClassLoader());
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeString(this.createdAt);
        dest.writeLong(this.uid);
        dest.writeBooleanArray(new boolean[] {this.isFavorited, this.isRetweeted});
        dest.writeParcelable(this.user, 0);
    }

    @SuppressWarnings("unused")
    public static final Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) { return new Tweet(source); }

        @Override
        public Tweet[] newArray(int size) { return new Tweet[size]; }
    };
}
