package com.codepath.apps.tweets.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

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

    public User() {
    }


    public static User fromJson(JSONObject userJson)
    {
        User user = new User();
        try {

            user.name = userJson.getString("name");
            user.uid = userJson.getLong("id");
            user.screenName = userJson.getString("screen_name");
            user.profileImageUrl = userJson.getString("profile_image_url");
            user.isFollowing = userJson.getBoolean("following");
            user.isVerified = userJson.getBoolean("verified");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;

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

    private User(Parcel source) {
        this.name = source.readString();
        this.screenName = source.readString();
        this.uid = source.readLong();
        this.profileImageUrl = source.readString();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public  void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.screenName);
        dest.writeLong(this.uid);
        dest.writeString(this.profileImageUrl);
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
