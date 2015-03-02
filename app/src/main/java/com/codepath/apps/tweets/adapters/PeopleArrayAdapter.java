package com.codepath.apps.tweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.tweets.R;
import com.codepath.apps.tweets.activity.ProfileActivity;
import com.codepath.apps.tweets.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by gangwal on 3/2/15.
 */
public class PeopleArrayAdapter extends ArrayAdapter<User> {


    private class ViewHolder {
        ImageView ivProfilePicture;
        TextView tvUserName;
        TextView tvBody;
        TextView tvScreenName;
        ImageButton btnFollowingIcon;
    }

    public PeopleArrayAdapter(Context context, List<User> tweets) {
        super(context, R.layout.item_people, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_people, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivProfilePicture = (ImageView) convertView.findViewById(R.id.ivProfilePic);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBodyText);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvUserNameHandle);
            viewHolder.btnFollowingIcon = (ImageButton) convertView.findViewById(R.id.btnFollowing);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvUserName.setText(user.getName());
        viewHolder.tvScreenName.setText(user.getScreenName());
        viewHolder.tvBody.setText(user.getTagline());
        if(user.isVerified()){
            viewHolder.tvUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_profile_verified, 0);
        }else{
            viewHolder.tvUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        viewHolder.tvUserName.setText("@" + user.getScreenName());
        viewHolder.ivProfilePicture.setImageResource(android.R.color.transparent);

        Picasso.with(getContext())
                .load(user.getProfileImageUrl())
                .fit()
                .into(viewHolder.ivProfilePicture);

        viewHolder.ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("user_id", user.getUid());//TODO need to set this user_id to a constant in ProfileActivity class
                getContext().startActivity(intent);
            }
        });

        viewHolder.btnFollowingIcon.setVisibility(View.VISIBLE);
        if (user.isFollowing()) {
//            viewHolder.btnFollowingIcon.setBackgroundResource((R.drawable.rounded_blue_edge_button));
            viewHolder.btnFollowingIcon.setImageResource(R.drawable.btn_inline_following_default);
            //setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tweet_action_inline_follow_off, 0, 0, 0);
        } else {
//            viewHolder.btnFollowingIcon.setBackgroundResource((R.drawable.rounded_blue_edge_button));
            viewHolder.btnFollowingIcon.setImageResource(R.drawable.btn_inline_follow_default);
//            viewHolder.btnFollowingIcon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_action_default, 0, 0, 0);
        }
        return convertView;
    }
}
