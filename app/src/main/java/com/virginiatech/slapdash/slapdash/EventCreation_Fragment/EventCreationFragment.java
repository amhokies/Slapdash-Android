package com.virginiatech.slapdash.slapdash.EventCreation_Fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.virginiatech.slapdash.slapdash.EventCreationActivity.EventCreationActivity;
import com.virginiatech.slapdash.slapdash.MainActivity;
import com.virginiatech.slapdash.slapdash.DataModelClasses.EventType;
import com.virginiatech.slapdash.slapdash.R;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Nelson on 9/26/2016.
 */
public class EventCreationFragment extends Fragment {
    private EventType currentType;
    private ImageView imgEventSelection;


    public EventCreationFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static EventCreationFragment newInstance() {
        EventCreationFragment fragment = new EventCreationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button btnPlay, btnFood, btnDrink, btnRandom, btnCreateEvent;


        View rootView = inflater.inflate(R.layout.fragment_event_creation, container, false);
        btnPlay = (Button) rootView.findViewById(R.id.btn_play);
        btnFood = (Button) rootView.findViewById(R.id.btn_food);
        btnDrink = (Button) rootView.findViewById(R.id.btn_drink);
        btnRandom = (Button) rootView.findViewById(R.id.btn_random);
        btnCreateEvent = (Button) rootView.findViewById(R.id.btnCreateEvent);
                imgEventSelection = (ImageView) rootView.findViewById(R.id.imgvEventSelection);


        //The starting picture is Food
        currentType = EventType.FOOD;

        //On Clicks For Buttons
        btnPlay.setOnClickListener(new View.OnClickListener(){
            //Perform action on click
            public void onClick(View v){
                currentType = EventType.PLAY;
                imgEventSelection.setImageResource(R.drawable.play_480);
            }
        });
        btnFood.setOnClickListener(new View.OnClickListener(){
            //Perform action on click
            public void onClick(View v){
                currentType = EventType.FOOD;
                imgEventSelection.setImageResource(R.drawable.burger_480);
            }
        });
        btnDrink.setOnClickListener(new View.OnClickListener(){
            //Perform action on click
            public void onClick(View v){
                currentType = EventType.DRINK;
                imgEventSelection.setImageResource(R.drawable.drink_480);
            }
        });
        btnRandom.setOnClickListener(new View.OnClickListener(){
            //Perform action on click
            public void onClick(View v){
                currentType = EventType.RANDOM;
                imgEventSelection.setImageResource(R.drawable.random_480);
            }
        });
        btnCreateEvent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent startLoginActivityIntent = new Intent(getApplicationContext(), EventCreationActivity.class);
                startLoginActivityIntent.putExtra("EventType", currentType);
                startActivityForResult(startLoginActivityIntent,
                        MainActivity.getEVENT_CREATION_ACTIVITY());
            }
        });
        return rootView;
    }
}