package com.virginiatech.slapdash.slapdash.Event_Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.virginiatech.slapdash.slapdash.DataModelClasses.EventType;
import com.virginiatech.slapdash.slapdash.R;

public class EventFragment extends Fragment {
    private EventType currentType;
    private ImageView imgEventSelection;


    public EventFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static EventFragment newInstance() {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_event_creation, container, false);


        return rootView;
    }
}
