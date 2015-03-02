package com.codepath.apps.tweets.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by gangwal on 2/21/15.
 */
@Table(name = "Users")
public class User extends Model implements Parcelable{
    @Column(name = "name")
    private String name;
    @Column(name = "uid", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;
    @Column(name = "screenName")
    private String screenName;
    @Column(name = "profileImageUrl")
    private String profileImageUrl;
    @Column(name = "isFollowing")
    private Boolean isFollowing;
    @Column(name = "isVerified")
    private Boolean isVerified;
    @Column(name = "current_user")
    public boolean currentUser;
    @Column(name="bannerImageUrl")
    private String bannerImageUrl;
    @Column(name="tagline")
    private String tagline;
    @Column(name = "notifications")
    public boolean notifications;
    @Column(name = "followers_count")
    public String followersCount;
    @Column(name = "friends_count")
    public String followingCount;
    @Column(name = "statuses_count")
    public String tweetsCount;


    public User() {
    }


    public static User fromJSON(JSONObject userJson)
    {
        User user = new User();
        try {

            user.name = userJson.getString("name");
            user.uid = userJson.getLong("id");
            user.screenName = userJson.getString("screen_name");
            user.profileImageUrl = userJson.getString("profile_image_url");
            user.isFollowing = userJson.getBoolean("following");
            user.isVerified = userJson.getBoolean("verified");
            if (!userJson.isNull("current_user")) {
                user.currentUser = true;
            } else {
                user.currentUser = false;
            }
            if (!userJson.isNull("profile_banner_url"))
                user.bannerImageUrl = userJson.getString("profile_banner_url");
            user.tagline = userJson.getString("description");
            if (!userJson.isNull("notifications"))
                user.notifications = userJson.getBoolean("notifications");
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            if (!userJson.isNull("followers_count"))
                user.followersCount = formatter.format(userJson.getLong("followers_count"));
            if (!userJson.isNull("friends_count"))
                user.followingCount = formatter.format(userJson.getLong("friends_count"));
            if (!userJson.isNull("statuses_count"))
                user.tweetsCount = formatter.format(userJson.getLong("statuses_count"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;

    }

    public static ArrayList<User> fromJSONArray(JSONArray jsonArray) {
        ArrayList<User> users = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject userJSON = null;
            try {
                userJSON = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            User user = User.fromJSON(userJSON);
            if (user != null) {
                users.add(user);
            }
        }

        return users;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public Boolean isFollowing() {
        return isFollowing;
    }
    public Boolean isVerified() { return isVerified; }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public String getTagline() {
        return tagline;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public String getFollowersCount() {
        return followersCount;
    }

    public String getFollowingCount() {
        return followingCount;
    }

    public String getTweetsCount() {
        return tweetsCount;
    }

    public boolean isCurrentUser() {
        return currentUser;
    }

    private User(Parcel source) {
        this.name = source.readString();
        this.screenName = source.readString();
        this.uid = source.readLong();
        this.profileImageUrl = source.readString();
        this.bannerImageUrl = source.readString();
        this.tagline = source.readString();
        this.followersCount = source.readString();
        this.followingCount = source.readString();
        this.tweetsCount = source.readString();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public  void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.screenName);
        dest.writeLong(this.uid);
        dest.writeString(this.profileImageUrl);
        dest.writeString(this.bannerImageUrl);
        dest.writeString(this.tagline);
        dest.writeString(this.followersCount);
        dest.writeString(this.followingCount);
        dest.writeString(this.tweetsCount);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
