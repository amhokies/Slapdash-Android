package com.virginiatech.slapdash.slapdash.EventRespondActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.virginiatech.slapdash.slapdash.HelperClasses.EventObjectId;
import com.virginiatech.slapdash.slapdash.MainActivity;
import com.virginiatech.slapdash.slapdash.R;

/**
 * Created by nima on 10/23/16.
 */

public class EventRespondWait_Fragment extends DialogFragment {
    private Button yesBtn, noBtn;
    private ProgressBar waitPrgBr;
    private TextView respondTv;
    private ImageView respondIv;
    private String eventId;

    public EventRespondWait_Fragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View created = inflater.inflate(R.layout.event_responding_wait_fragment, container);
        yesBtn = (Button) created.findViewById(R.id.respond_yes_default);
        noBtn = (Button) created.findViewById(R.id.respond_no_default);
        waitPrgBr = (ProgressBar) created.findViewById(R.id.respond_progress_bar);
        respondTv = (TextView) created.findViewById(R.id.respond_text);
        respondIv = (ImageView) created.findViewById(R.id.respond_image);
        yesBtn.setVisibility(View.GONE);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventObjectId.getInstance(getActivity().getApplicationContext()).
                        updateEventObjectIdToken(eventId);
                startMainActivityOnTop();
            }
        });
        noBtn.setVisibility(View.GONE);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainActivityOnTop();
            }
        });
        return created;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.get("eventid") != null)
        {
            eventId = (String) savedInstanceState.get("eventid");
            Log.d(MainActivity.DEBUG, eventId);
        }
        super.onCreate(savedInstanceState);
    }

    public void SucceededToRespond(boolean accepted){
        waitPrgBr.setVisibility(View.GONE);
        if(!accepted){
            respondTv.setText("You are now out of this event.");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                respondIv.setImageDrawable(getResources().getDrawable(R.drawable.removed,
                        getActivity().getTheme()));
            } else {
                respondIv.setImageDrawable(getResources().getDrawable(R.drawable.removed));
            }
        } else {
            respondTv.setText("You are now part of this event. " +
                    "Would you like to set this event as your main event?");
            yesBtn.setVisibility(View.VISIBLE);
            noBtn.setVisibility(View.VISIBLE);
        }
        respondIv.setVisibility(View.VISIBLE);

        if(!accepted){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                        startMainActivityOnTop();
                }
            }, 1000);
        }
    }

    private void startMainActivityOnTop(){
        Intent mainAcitvityIntent = new Intent(getActivity().getApplicationContext(),
                MainActivity.class);
        mainAcitvityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainAcitvityIntent);
        if(getActivity()!= null){
            getActivity().finish();
        }
    }
}
