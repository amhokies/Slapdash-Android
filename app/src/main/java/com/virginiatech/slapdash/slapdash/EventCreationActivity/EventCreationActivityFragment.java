package com.virginiatech.slapdash.slapdash.EventCreationActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TimePicker;

import com.virginiatech.slapdash.slapdash.HelperClasses.CompatibilityHelper;
import com.virginiatech.slapdash.slapdash.InvitingFriendList_Fragment.FbFriendFragment;
import com.virginiatech.slapdash.slapdash.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
/**
 * A placeholder fragment containing a simple view.
 */
public class EventCreationActivityFragment extends Fragment {
    private EditText title;
    private EditText description;
    private static DatePicker startDatePicker;
    private static TimePicker startTimePicker;
    private NumberPicker timeoutPicker;
    private ImageView selectionImg;
    private Spinner permissionSpinner;
    private int selectionCounter = 0;
    private static Calendar cal = Calendar.getInstance();

    private static OnEventCreationFragmentInteractionListener  mListener;
    public EventCreationActivityFragment() {
    }

    public static EventCreationActivityFragment newInstance() {
        EventCreationActivityFragment fragment = new EventCreationActivityFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_event_creation2, container, false);

        /* Initiliaze the view elements */
        title = (EditText) mainView.findViewById(R.id.create_event_title);
        selectionImg = (ImageView) mainView.findViewById(R.id.eventTypeImage);
        description = (EditText) mainView.findViewById(R.id.create_event_description);
        startDatePicker = (DatePicker) mainView.findViewById(R.id.create_event_datepicker);
        startTimePicker = (TimePicker) mainView.findViewById(R.id.create_event_timepicker);
        timeoutPicker = (NumberPicker) mainView.findViewById(R.id.create_event_numberpicker);

        selectionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(selectionCounter){
                    case 0:
                        selectionImg.setImageResource(R.drawable.burger_480);
                        selectionCounter++;
                        EventCreationActivity.thisEvent.setCategory("Food");
                        break;
                    case 1:
                        selectionImg.setImageResource(R.drawable.drink_480);
                        selectionCounter++;
                        EventCreationActivity.thisEvent.setCategory("Drink");
                        break;
                    case 2:
                        selectionImg.setImageResource(R.drawable.play_480);
                        selectionCounter++;
                        EventCreationActivity.thisEvent.setCategory("Play");
                        break;
                    case 3:
                        selectionImg.setImageResource(R.drawable.random_480);
                        selectionCounter = 0;
                        EventCreationActivity.thisEvent.setCategory("SlapDash");
                        break;
                }
            }}
        );

        if(mListener != null){
            mListener.OnEventStartTimeChanged(new Date());
        } else {
            onAttach(getContext());   // To get back a pointer to mListener
        }

        title.setFocusableInTouchMode(false);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setFocusableInTouchMode(true);
                v.requestFocusFromTouch();
            }
        });
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mListener.OnEventTitleChanged(charSequence.toString());
            }
        });

        description.setFocusableInTouchMode(false);
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setFocusableInTouchMode(true);
                v.requestFocusFromTouch();
            }
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mListener.OnEventDescriptionChanged(charSequence.toString());
                Log.d("SLAPDASH", "TitleText changed to:" + charSequence);
            }
        });

        startDatePicker.setOnClickListener(new DatePicker.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newDateFragment = new DatePickerFragment();
                newDateFragment.show(getFragmentManager(), "datePicker");
                Log.d("SLAPDASH", "startDatePicker Clicked");
            }
        });
        startDatePicker.setSpinnersShown(false);
        startTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newTimeFragment = new TimePickerFragment();
                newTimeFragment.show(getFragmentManager(), "timePicker");
                Log.d("SLAPDASH", "onTimePicker Clicked");
            }
        });

        timeoutPicker.setMaxValue(60);
        timeoutPicker.setMinValue(1);
        timeoutPicker.setValue(1);
        timeoutPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        timeoutPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mListener.OnEventTimeoutChanged(i1);
                Log.d("SLAPDASH", "Timeout value changed to: " + i1);
            }
        });

        ArrayList<String> permissionA = new ArrayList<>();
        permissionA.add("Open");
        permissionA.add("Closed");
        permissionA.add("Approval");
        final SpinnerAdapter spinnerAdapter = new PermissionSpinnerAdapter(permissionA);

        permissionSpinner = (Spinner) mainView.findViewById(R.id.create_event_permission);
        permissionSpinner.setAdapter(spinnerAdapter);
        permissionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String permission = (String) spinnerAdapter.getItem(i);
                mListener.OnEventPermissionChanged(permission);
                Log.d("SLAPDASH", "permission changed to :" + permission);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainView.getWindowToken(), 0);
        return mainView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FbFriendFragment.OnListFragmentInteractionListener) {
            mListener = (OnEventCreationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnEventCreationFragmentInteractionListener {
        void OnEventTitleChanged(String newTitle);
        void OnEventDescriptionChanged(String newDescription);
        void OnEventStartTimeChanged(Date newStartDate);
        void OnEventTimeoutChanged(int newTimeout);
        void OnEventPermissionChanged(String newPermission);
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            TimePickerDialog tpd = new TimePickerDialog(getActivity(), this, hour, minute, false);
            return tpd;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            cal = Calendar.getInstance();
            cal.set(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH),
                    hourOfDay,
                    minute);

            CompatibilityHelper.setTime(view, hourOfDay, minute);
            mListener.OnEventStartTimeChanged(cal.getTime());
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            cal = Calendar.getInstance();
            cal.set(year, month, day);

            startDatePicker.init(year, month, day, null);
            mListener.OnEventStartTimeChanged(cal.getTime());
        }
    }
}
