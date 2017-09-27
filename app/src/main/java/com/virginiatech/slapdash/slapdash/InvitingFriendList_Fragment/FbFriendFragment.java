package com.virginiatech.slapdash.slapdash.InvitingFriendList_Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.virginiatech.slapdash.slapdash.DataModelClasses.Friend;
import com.virginiatech.slapdash.slapdash.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FbFriendFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private MyFbFriendRecyclerViewAdapter adapter;

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                 Public Methods                                          //
    /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FbFriendFragment() {}

    //-------------------------------------------------------------------------------------------
    public static FbFriendFragment newInstance(int columnCount) {
        FbFriendFragment fragment = new FbFriendFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    //-------------------------------------------------------------------------------------------
    /**
     *  The Logic to get the list of friends from facebook
     */
    public String getCurrentUserID() {
        return Profile.getCurrentProfile().getId();
    }

    //-------------------------------------------------------------------------------------------
    public void populateFriendsList() {
        Bundle param = new Bundle();
        param.putString("fields", "id,name");
        GraphRequest.Callback callback = new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                Log.d("SLAPDASH", response.getJSONObject().toString());
                if (response.getError() != null) {
                    Log.d("SLAPDASH", response.toString());
                } else {
                    try {
                        JSONArray res = response.getJSONObject().getJSONArray("data");
                        ArrayList<Friend> tempFriendList = new ArrayList<>();
                        for (int i = 0; i < res.length(); i++) {

                            JSONObject json = res.getJSONObject(i);
                            tempFriendList.add(new Friend(json.getString("name"),
                                    json.getString("id")));
                            Log.d("SLAPDASH", json.getString("name"));
                        }
                        adapter.addNewFriends(tempFriendList);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException jserr) {
                        Log.e("SLAPDASH", jserr.toString());
                    }
                }
            }
        };
        
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + getCurrentUserID() + "/friends",
                param,
                HttpMethod.GET,
                callback
        ).executeAsync();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                     Interface                                           //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public interface OnListFragmentInteractionListener {
        void OnFbFriendFragmentCreated();
        void OnInvitedFriendButtonClicked(String friendId);
        void OnUninvitedFriendButtonClicked(String friendId);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                     Overrides                                           //
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fbfriend_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            ArrayList<Friend> friendList = new ArrayList<>();
            adapter = new MyFbFriendRecyclerViewAdapter(friendList, mListener,
                    getActivity().getApplicationContext());
            recyclerView.setAdapter(adapter);
        }
        populateFriendsList();
        mListener.OnFbFriendFragmentCreated();
        return view;
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
