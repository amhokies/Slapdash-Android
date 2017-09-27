package com.virginiatech.slapdash.slapdash.FriendList_Fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.virginiatech.slapdash.slapdash.DataModelClasses.User;
import com.virginiatech.slapdash.slapdash.InvitingFriendList_Fragment.FbFriendRequestQueueProvider;
import com.virginiatech.slapdash.slapdash.R;
import com.virginiatech.slapdash.slapdash.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by Nima on 10/11/16.
 * <p>
 * The adapter Class that encapsulates the information needed to
 * populate the list of friends.
 */

public class FriendListAdapter extends ArrayAdapter<User> {
    private final ImageLoader mImageLoader;

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                 Public Methods                                          //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public FriendListAdapter(Context context, ArrayList<User> friends) {
        super(context, 0, friends);
        mImageLoader = FbFriendRequestQueueProvider.getInstance(getContext()).getImageLoader();
    }

    //--------------------------------------------------------------------------------------------
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        User thisFriend = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.friend_single_layout, parent, false);
        }

        TextView fullName = (TextView) convertView
                .findViewById(R.id.single_friend_name);

        RoundedImageView fbPhotoView = (RoundedImageView) convertView
                .findViewById(R.id.single_friend_picture);

        if (thisFriend != null) {
            String fullNameString = thisFriend.getFullName();
            if (fullNameString != null) {
                fullName.setText(fullNameString);
            }

            fbPhotoView.setImageUrl("https://graph.facebook.com/v2.8/" +
                    thisFriend.getFbtokenid() +
                    "/picture?type=normal", mImageLoader);
        }

        return convertView;
    }
}