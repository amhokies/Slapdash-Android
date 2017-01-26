package com.virginiatech.slapdash.slapdash.InvitingFriendList_Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.virginiatech.slapdash.slapdash.DataModelClasses.Friend;
import com.virginiatech.slapdash.slapdash.InvitingFriendList_Fragment.FbFriendFragment.OnListFragmentInteractionListener;
import com.virginiatech.slapdash.slapdash.R;

import java.util.List;

/**
 *
 */
public class MyFbFriendRecyclerViewAdapter extends RecyclerView.Adapter<MyFbFriendRecyclerViewAdapter.ViewHolder> {

    private final List<Friend> friendsList;
    private final OnListFragmentInteractionListener mListener;
    private final ImageLoader mImageLoader;

    public MyFbFriendRecyclerViewAdapter(List<Friend> friends,
                                         OnListFragmentInteractionListener listener,
                                         Context context) {
        friendsList = friends;
        mListener = listener;
        mImageLoader = FbFriendRequestQueueProvider.getInstance(context).getImageLoader();
    }


    public void addNewFriends(List<Friend> newFriends) {
        friendsList.addAll(newFriends);
        Log.d("SLAPDASH", "Size of friendList = " + friendsList.size());
    }

    public void addNewFriends(Friend newFriend) {
        friendsList.add(newFriend);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_fbfriend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = friendsList.get(position);
        holder.mProfileName.setText(friendsList.get(position).getFullName());
        //holder.mProfilePictureView.setProfileId(friendsList.get(position).getFbToken());
        //holder.mProfilePictureView.setCropped(true);
        holder.updateTheInviteButton();
        holder.mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                holder.updateTheInviteButton();
            }
        });
        holder.mNetworkImageView.setImageUrl("https://graph.facebook.com/v2.8/" +
                                                holder.mItem.getFbToken() +
                                                "/picture?type=normal", mImageLoader);
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mProfileName;
        public Button mButton;
        private NetworkImageView mNetworkImageView;
        public Friend mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mProfileName = (TextView) view.findViewById(R.id.facebook_friend_name);
            mButton = (Button) view.findViewById(R.id.invite_friend);
            mNetworkImageView = (NetworkImageView) view.findViewById(R.id.friend_picture);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mProfileName.getText() + "'";
        }

        public void updateTheInviteButton() {
            boolean invited = mItem.isInvited();
            Drawable newDraw;
            Activity context = (Activity) mListener;
            Log.d("SLAPDASH", "User:" + mItem.getFullName() + ", Invited " + invited);
            if(!invited){ // Invite if not already invited
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    newDraw = context.getResources().getDrawable(
                            R.drawable.facebood_friend_invite_button, context.getTheme());
                }
                else {
                    newDraw = context.getResources().getDrawable(
                            R.drawable.facebood_friend_invite_button);
                }
                mButton.setText("Invite");
                mButton.setTextColor(Color.parseColor("#a4c639"));
                mListener.OnUninvitedFriendButtonClicked(mItem.getFbToken());
            } else { // Uninvited if already invited
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    newDraw = context.getResources().getDrawable(
                            R.drawable.fb_friend_invited, context.getTheme());
                }
                else {
                    newDraw = context.getResources().getDrawable(
                            R.drawable.fb_friend_invited);
                }
                mButton.setText("Invited");
                mButton.setTextColor(Color.parseColor("#edeeef"));
                mListener.OnInvitedFriendButtonClicked(mItem.getFbToken());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mButton.setBackground(newDraw);
            } else {
                mButton.setBackgroundDrawable(newDraw);
            }
            mItem.setInvited(!invited);
        }
    }
}
