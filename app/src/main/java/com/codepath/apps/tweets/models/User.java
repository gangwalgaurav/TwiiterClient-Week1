package com.codepath.apps.tweets.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gangwal on 2/21/15.
 */
public class User {

    private String name;
    private long uid;
    private String screenName;
    private String profileImageUrl;
    private Boolean isFollowing;
    private Boolean isVerified;


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
}
