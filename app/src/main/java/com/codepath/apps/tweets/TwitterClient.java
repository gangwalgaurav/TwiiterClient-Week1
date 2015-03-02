package com.codepath.apps.tweets;

import android.content.Context;
import android.text.TextUtils;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.util.LinkedList;
import java.util.List;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "F8duIbH0U40ImKl1WsyqRWU0q";
	public static final String REST_CONSUMER_SECRET = "kKLKdu0AvJFM5nGIe7CXtSeDm1nbz3wWrzGHd5VCchVqZlSSfb";
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getInterestingnessList(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */


    public void getHomeTimeline(String maxId,AsyncHttpResponseHandler handler){

        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(maxId)) {
            params.put("max_id",maxId);
        }
        params.put("count",25);
        params.put("since_id",1);

        getClient().get(apiUrl, params, handler);
    }

    public void getMetionsTimeline(String maxId,AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count",25);
        getClient().get(apiUrl, params, handler);
    }

    public void getUserTimeline(String maxId,String userId,AsyncHttpResponseHandler handler){

        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(maxId)) {
            params.put("max_id",maxId);
        }
        params.put("user_id",userId);
        params.put("count",25);
        params.put("since_id",1);

        getClient().get(apiUrl, params, handler);
    }

    /**
     * get info about user with user_id
     * @param user_id
     * @param handler
     */
    public void getUserInfo(long user_id,AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        params.add("user_id",Long.toString(user_id));
        getClient().get(apiUrl,params, handler);
    }

    /**
     * get Info about current User
     * @param handler
     */
    public void getCurrentUserCredentials(AsyncHttpResponseHandler handler) {
               String apiUrl = getApiUrl("account/verify_credentials.json");
               client.get(apiUrl, null, handler);
    }
    //Compose request
    public void postTweetUpdate(String tweet, AsyncHttpResponseHandler handler) {
        List <BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("status", tweet));
                String apiUrl = getApiUrl("statuses/update.json?" + URLEncodedUtils.format(params, "utf-8"));
                client.post(apiUrl, null, handler);
            }
    public void getTweetDetail(long id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.add("id",""+id);
//        params.add("id","210462857140252672");
        String apiUrl = getApiUrl("statuses/show.json");
        client.get(apiUrl, params, handler);
    }

    public void getSearchTopTweets(String query, String maxID, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        if(!TextUtils.isEmpty(maxID)){
            params.put("max_id", ""+(Long.parseLong(maxID)-1));
            params.put("count", "80");
        }
        params.put("since_id", "1");
        params.put("result_type", "popular");
        params.put("q", query);

        client.get(apiUrl, params, handler);
    }

    public void getSearchAllTweets(String query, String maxID, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        if(!TextUtils.isEmpty(maxID)){
            params.put("max_id", ""+(Long.parseLong(maxID)-1));
            params.put("count", "80");
        }
        params.put("since_id", "1");
        params.put("result_type", "recent");
        params.put("q", query);

        client.get(apiUrl, params, handler);
    }

    //statuses/show.json

}