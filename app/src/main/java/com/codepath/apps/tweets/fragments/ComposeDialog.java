package com.codepath.apps.tweets.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.tweets.R;
import com.codepath.apps.tweets.TwitterApplication;
import com.codepath.apps.tweets.TwitterClient;
import com.codepath.apps.tweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by gangwal on 2/22/15.
 */
public class ComposeDialog  extends DialogFragment{

    private static FragmentManager sFragmentManager;
    private TwitterClient client;
    private User loggedInUser;
    private onTweetListener mCallback;

    private ImageButton ibtnBack;
    private ImageView ivProfileImage;
    private TextView tvUserName;
    private TextView tvScreenName;
    private TextView tvCharCount;
    private Button btnTweet;
    private EditText etTweetText;
    private ImageButton ibtnLocation;
    private ImageButton ibtnGallery;
    private static final int MAX_CHAR_COUNT_TWEET=140;
    private final String TAG = ComposeDialog.class.getSimpleName();
    private static String sHint;

    public interface onTweetListener {
              public void onTweetSubmit();
         }

    public static ComposeDialog getInstance(String hint,FragmentManager fragmentManager){
        sHint = hint;
        sFragmentManager = fragmentManager;
        return new ComposeDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.compose_tweet_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getLoggedInUserCredentials();

        setupViews(view);
        return view;
    }

    private void getLoggedInUserCredentials() {
        client = TwitterApplication.getRestClient();
        client.getCurrentUserCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                loggedInUser = User.fromJson(response);
                if (loggedInUser == null) {
                    Toast.makeText(getDialog().getContext(), "Not able to get Current User's info", Toast.LENGTH_LONG).show();
                    getDialog().dismiss();
                }
                tvUserName.setText(loggedInUser.getName());
                tvScreenName.setText(loggedInUser.getScreenName());
                Picasso.with(getDialog().getContext()).load(loggedInUser.getProfileImageUrl()).into(ivProfileImage);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ERROR", errorResponse.toString());

            }
        });
    }

    private void setupViews(View view) {
        ibtnBack = (ImageButton) view.findViewById(R.id.ibtnBackButton);
        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        ivProfileImage = (ImageView)view.findViewById(R.id.ivLoggedInUserProfileImage);
        ivProfileImage.setImageResource(android.R.color.transparent);

        tvUserName = (TextView)view.findViewById(R.id.tvLoggedInUserName);
        tvScreenName = (TextView)view.findViewById(R.id.tvLoggedInUserScreenName);
        tvCharCount = (TextView)view.findViewById(R.id.tvCharCount);
        btnTweet = (Button)view.findViewById(R.id.btnTweet);
        btnTweet.setEnabled(false);
        btnTweet.setAlpha((float) 0.5);
        etTweetText = (EditText)view.findViewById(R.id.etTweetText);
        etTweetText.setHint(sHint);
        etTweetText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(etTweetText.getText().toString().length()==0){
                    if((sHint.contains(getActivity().getResources().getString(R.string.Reply_Tweet_hint)))){
//                        etTweetText.setText("");
                        String replyTo = sHint.replace(getActivity().getResources().getString(R.string.Reply_Tweet_hint),"");
                        etTweetText.append(replyTo + " ");
                    }
                }
            }
        });
        etTweetText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = etTweetText.getText().toString();
                tvCharCount.setText("" + (MAX_CHAR_COUNT_TWEET - text.length()));

                if (text.length() > 0 && text.length() <= MAX_CHAR_COUNT_TWEET) {
                    btnTweet.setAlpha(1);
                    btnTweet.setEnabled(true);
//                    btnTweet.invalidate();
                } else {
                    tvCharCount.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    btnTweet.setEnabled(false);
                    btnTweet.setAlpha((float) 0.5);

                }
                if (text.length() <= MAX_CHAR_COUNT_TWEET) {
                    tvCharCount.setTextColor(getResources().getColor(R.color.secondary_text_color));
                }

            }
        });
        tvCharCount.setTextColor(getResources().getColor(R.color.secondary_text_color));
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTweet();
            }
        });
    }

    private void submitTweet() {
        client.postTweetUpdate(etTweetText.getText().toString(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                mCallback.onTweetSubmit();
                getDialog().dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getDialog().getContext(), "Failed to submit the tweet. Please try again later.", Toast.LENGTH_LONG).show();
                Log.e(TAG, throwable.toString());
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mCallback = (onTweetListener) activity;
        }catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onTweetListener");
        }
    }

}
