package com.virginiatech.slapdash.slapdash.InvitingFriendList_Fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.virginiatech.slapdash.slapdash.DataModelClasses.Friend;
import com.virginiatech.slapdash.slapdash.InvitingFriendList_Fragment.
        FbFriendFragment.OnListFragmentInteractionListener;
import com.virginiatech.slapdash.slapdash.R;

import java.util.List;

/**
 * Created by Nima on 27/12/2017
 * <p>
 * The adapter that encapsulates the data needed to populate
 * the list of facebook friends.
 */
public class MyFbFriendRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    private final List<Friend> friendsList;
    private final OnListFragmentInteractionListener mListener;
    private final ImageLoader mImageLoader;

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                 Public Methods                                          //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public MyFbFriendRecyclerViewAdapter(List<Friend> friends,
                                         OnListFragmentInteractionListener listener,
                                         Context context) {
        friendsList = friends;
        mListener = listener;
        mImageLoader = FbFriendRequestQueueProvider.getInstance(context).getImageLoader();
    }

    //-------------------------------------------------------------------------------------------
    public void addNewFriends(List<Friend> newFriends) {
        friendsList.addAll(newFriends);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                 Overrides                                               //
    /////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_fbfriend, parent, false);
        return new ViewHolder(view);
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setMItem(friendsList.get(position));
        holder.getMProfileName().setText(friendsList.get(position).getFullName());
        holder.updateTheInviteButton(mListener);
        holder.getMButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.updateTheInviteButton(mListener);
            }
        });
        holder.getMNetworkImageView().setImageUrl("https://graph.facebook.com/v2.8/" +
                holder.getMItem().getFbToken() +
                "/picture?type=normal", mImageLoader);
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public int getItemCount() {
        return friendsList.size();
    }
}
