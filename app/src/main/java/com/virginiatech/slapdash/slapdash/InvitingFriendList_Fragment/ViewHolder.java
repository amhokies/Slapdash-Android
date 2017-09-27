package com.virginiatech.slapdash.slapdash.InvitingFriendList_Fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.virginiatech.slapdash.slapdash.DataModelClasses.Friend;
import com.virginiatech.slapdash.slapdash.HelperClasses.CompatibilityHelper;
import com.virginiatech.slapdash.slapdash.R;

import lombok.Data;

/**
 * Created by nima on 1/27/17.
 * <p>
 * It will hold the object and will represent one item on the
 * Facebook friend list.
 */

@Data
public class ViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final TextView mProfileName;
    private Button mButton;
    private NetworkImageView mNetworkImageView;
    private Friend mItem;

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                 Public Methods                                          //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public ViewHolder(View view) {
        super(view);
        mView = view;
        mProfileName = (TextView) view.findViewById(R.id.facebook_friend_name);
        mButton = (Button) view.findViewById(R.id.invite_friend);
        mNetworkImageView = (NetworkImageView) view.findViewById(R.id.friend_picture);
    }

    //-------------------------------------------------------------------------------------------
    public void updateTheInviteButton(FbFriendFragment.OnListFragmentInteractionListener listener) {
        boolean invited = mItem.isInvited();
        Drawable newDraw;
        Activity context = (Activity) listener;
        if (!invited) { // Invite if not already invited
            newDraw = CompatibilityHelper.getDrawable(context, R.drawable.facebood_friend_invite_button);
            mButton.setText("Invite");
            mButton.setTextColor(Color.parseColor("#a4c639"));
            listener.OnUninvitedFriendButtonClicked(mItem.getFbToken());
        } else { // Uninvited if already invited
            newDraw = CompatibilityHelper.getDrawable(context, R.drawable.fb_friend_invited);
            mButton.setText("Invited");
            mButton.setTextColor(Color.parseColor("#edeeef"));
            listener.OnInvitedFriendButtonClicked(mItem.getFbToken());
        }
        CompatibilityHelper.setBackground(mButton, newDraw);
        mItem.setInvited(!invited);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                 Overrides                                               //
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return super.toString() + " '" + mProfileName.getText() + "'";
    }
}
