package com.virginiatech.slapdash.slapdash.EventList_Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.virginiatech.slapdash.slapdash.DataModelClasses.Event;
import com.virginiatech.slapdash.slapdash.HelperClasses.CompatibilityHelper;
import com.virginiatech.slapdash.slapdash.MainActivity;
import com.virginiatech.slapdash.slapdash.R;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by Nelson on 10/2/2016.
 */
public class EventListFragment extends Fragment {
    Fragment self = this;
    private static final String EVENT_LIST_FRAGMENT_TAG = "EVENT-LIST-FRAGMENT";
    private ListView eventListView;
    private EventListAdapter adapter;
    private OnFragmentInteractionListener mListener;

    @Getter
    private List<Event> eventList;
    private Button syncEventsButton;

    public void setEventList(List<Event> eventList) {
        if (eventList != null) {
            this.eventList.clear();
            this.eventList.addAll(eventList);
            adapter.notifyDataSetChanged();
        }
    }

    public EventListFragment() {
        // Required empty public constructor
    }

    public static EventListFragment newInstance() {
        EventListFragment fragment = new EventListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.eventList = new ArrayList<>();
        adapter = new EventListAdapter(this.getContext(), eventList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View createdView = inflater.inflate(R.layout.fragment_event_list, container, false);

        eventListView = (ListView) createdView.findViewById(R.id.friend_list_view);
        eventListView.setDivider(CompatibilityHelper.getDrawable(getActivity(),
                R.drawable.event_list_divider));


        eventListView.setAdapter(adapter);
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(MainActivity.DEBUG, "EventList on item at position " + position + "Selected");
                mListener.onDefaultEventIdChanged(adapter.getItem(position));
                getActivity().getSupportFragmentManager().beginTransaction().remove(self).commit();
            }
        });

        syncEventsButton = (Button) createdView.findViewById(R.id.syncEventsButton);
        syncEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRetrieveListOfEvents();
            }
        });

        return createdView;
    }
        @Override
        public void setUserVisibleHint (boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser) {
                mListener.onRetrieveListOfEvents();
            } else {
                System.out.println("Not visible bitch");
            }
        }

        @Override
        public void setMenuVisibility ( final boolean visible){
            super.setMenuVisibility(visible);
            if (!visible) {
                //not visible anymore
            } else {
                mListener.onRetrieveListOfEvents();
            }
        }

    @Override
    public void onAttach (Context context){
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.onRetrieveListOfEvents();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onRetrieveListOfEvents();

        void onDefaultEventIdChanged(Event event);
    }
}