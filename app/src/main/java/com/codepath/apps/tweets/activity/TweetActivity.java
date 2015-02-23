package com.codepath.apps.tweets.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.tweets.R;
import com.codepath.apps.tweets.TwitterApplication;
import com.codepath.apps.tweets.TwitterClient;
import com.codepath.apps.tweets.helpers.Utilities;
import com.codepath.apps.tweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TweetActivity extends ActionBarActivity {

    private final String TAG = TweetActivity.class.getSimpleName();
    private TwitterClient client;
    private Tweet tweet;

    private ImageView ivDetailsProfileImage;
    private TextView tvDetailsUserName;
    private TextView tvDetailsScreenName;
    private TextView tvDetailsDetailsBody;
    private ImageView ivVerifiedAccount;
    private ImageButton ibFollowing;
    private ImageView ivDetailsMedia;
    private TextView tvCreatedTime;
    private Button btDetailsRetweetCount;
    private Button btDetailsFavoriteCount;
    private TextView tvDetailsRetweetCount;
    private TextView tvDetailsFavoriteCount;
    private Button btShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        client = TwitterApplication.getRestClient();
        String id = getIntent().getStringExtra("id");
        populateTweet(id);
        setupViews();
    }

    private void setupViews() {
        ivDetailsProfileImage = (ImageView)findViewById(R.id.ivDetailProfileImage);
        tvDetailsUserName = (TextView)findViewById(R.id.tvDetailsUserName);
        tvDetailsScreenName = (TextView)findViewById(R.id.tvDetailsScreenName);
        tvDetailsDetailsBody = (TextView)findViewById(R.id.tvDetailsBody);
        ivVerifiedAccount = (ImageView)findViewById(R.id.ivVerified);
        ibFollowing = (ImageButton)findViewById(R.id.ibDetailsFollowing);
        ivDetailsMedia = (ImageView)findViewById(R.id.ivDetailsMedia);
        tvCreatedTime = (TextView)findViewById(R.id.tvDetailsCreatedTime);
        btDetailsRetweetCount = (Button)findViewById(R.id.btDetailsRetweet);
        btDetailsFavoriteCount = (Button)findViewById(R.id.btDetailsFavorite);
        tvDetailsRetweetCount = (TextView)findViewById(R.id.tvDetailsRetweetLabel);
        tvDetailsFavoriteCount = (TextView)findViewById(R.id.tvDetailsFavoriteLabel);
        btShareButton = (Button)findViewById(R.id.btDetailsShare);
    }

    private void populateTweet(final String uid) {
        if(!Utilities.isNetworkAvailable(this)){
            Toast.makeText(this,R.string.NO_INTERNET_CONNECTION,Toast.LENGTH_SHORT).show();
            return;
        }
        client.getTweetDetail(uid, new JsonHttpResponseHandler() {
            //Success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    tweet = Tweet.fromJson(response);

                    ivDetailsProfileImage.setImageResource(android.R.drawable.screen_background_light_transparent);
                    Picasso.with(TweetActivity.this).load(tweet.getUser().getProfileImageUrl()).into(ivDetailsProfileImage);

                    tvDetailsUserName.setText(Html.fromHtml("<b>" + tweet.getUser().getName() + "</b>"));
                    tvDetailsScreenName.setText("@"+(tweet.getUser().getScreenName()));
                    ivVerifiedAccount.setImageResource(R.drawable.ic_profile_verified);
                    ivVerifiedAccount.setVisibility(tweet.getUser().isVerified()==true? (View.VISIBLE) : (View.GONE));
                    ibFollowing.setImageResource(0);
                    final int followed = tweet.getUser().isFollowing()?(R.drawable.ic_tweet_action_inline_follow_on):(R.drawable.ic_tweet_action_inline_follow_off);

                    ibFollowing.setImageResource(followed);
                    tvDetailsDetailsBody.setText(Html.fromHtml(tweet.getBody()));

                    ivDetailsMedia.setImageResource(0);
                    if(TextUtils.isEmpty(tweet.getMediaURL())){
                        ivDetailsMedia.setVisibility(View.GONE);
                    }
                    else{
                        ivDetailsMedia.setVisibility(View.VISIBLE);
                        Picasso.with(TweetActivity.this).load(tweet.getMediaURL()).into(ivDetailsMedia);
                        ivDetailsMedia.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(TweetActivity.this, ImageDisplayActivity.class);
                                String imageInfo = tweet.getMediaURL();
                                intent.putExtra("imageInfo", imageInfo);
                                startActivity(intent);
                            }
                        });
                    }
                    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy  hh:mm a");
                    String date = format.format(Date.parse(tweet.getCreatedAt()));
                    tvCreatedTime.setText(date);

                    final Animation anim = AnimationUtils.loadAnimation(TweetActivity.this, R.anim.scale);
                    ibFollowing.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int followed1;
                            if(tweet.getUser().isFollowing())
                                ibFollowing.setImageResource(R.drawable.ic_tweet_action_inline_follow_off);
                            else
                                ibFollowing.setImageResource(R.drawable.ic_tweet_action_inline_follow_on);

                            ibFollowing.startAnimation(anim);

                        }
                    });

                    btShareButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onShareItem(ivDetailsMedia);
                        }
                    });

                    //TODO Need to diff between retweets and retweet
                    tvDetailsRetweetCount.setText(Html.fromHtml("<b>" + NumberFormat.getNumberInstance(Locale.US).format(tweet.getRetweetCount()) + "</b> RETWEETS"));
                    tvDetailsFavoriteCount.setText(Html.fromHtml("<b>" +NumberFormat.getNumberInstance(Locale.US).format(tweet.getFavoriteCount()) +"</b> FAVORITES"));

                    Drawable retweeted = tweet.isRetweeted()?getResources().getDrawable(R.drawable.ic_tweet_action_inline_retweet_on):getResources().getDrawable(R.drawable.ic_tweet_action_inline_retweet_off);
                    btDetailsRetweetCount.setCompoundDrawablesWithIntrinsicBounds(retweeted,null,null,null);
                    Drawable favortited = tweet.isFavorited()?getResources().getDrawable(R.drawable.ic_tweet_action_inline_favorite_on):getResources().getDrawable(R.drawable.ic_tweet_action_inline_favorite_off);
                    btDetailsFavoriteCount.setCompoundDrawablesWithIntrinsicBounds(favortited,null,null,null);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    Log.e(TAG, "Failure " + errorResponse.toString());
                    Toast.makeText(TweetActivity.this, "Failure" + errorResponse.toString(), Toast.LENGTH_SHORT).show();
                    writeToFile(errorResponse.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("json.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Log.i(TAG, "File Written");
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSubmit(View v) {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra("code", 200); // ints work too
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }

    // Can be triggered by a view event such as a button press
    public void onShareItem(View v) {
        // Get access to bitmap image from view
        ImageView ivImage = (ImageView) findViewById(R.id.ivImageResult);
        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(ivImage);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            // ...sharing failed, handle error
        }
    }

        // Returns the URI path to the Bitmap displayed in specified ImageView
        public Uri getLocalBitmapUri(ImageView imageView) {
            // Extract Bitmap from ImageView drawable
            Drawable drawable = imageView.getDrawable();
            Bitmap bmp = null;
            if (drawable instanceof BitmapDrawable){
                bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            } else {
                return null;
            }
            // Store image to default external storage directory
            Uri bmpUri = null;
            try {
                File file =  new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
                file.getParentFile().mkdirs();
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                bmpUri = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;
        }

}
