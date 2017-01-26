package com.virginiatech.slapdash.slapdash.EventList_Fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.virginiatech.slapdash.slapdash.DataModelClasses.Event;
import com.virginiatech.slapdash.slapdash.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nima Yahyazadeh on 10/24/16.
 *
 * To hold the list of events retrieved from the APIs.
 */
public class EventListAdapter extends ArrayAdapter<Event> {
        private List<Event> thisList;
        public EventListAdapter(Context context, List<Event> events){
            super(context, 0, events);
            thisList = events;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            Event thisEvent = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).
                        inflate(R.layout.event_single_layout, parent, false);
            }

            TextView eventTitle = (TextView) convertView.findViewById(R.id.single_event_title);
            TextView eventStartTime = (TextView) convertView.findViewById(R.id.single_event_starttime);
            TextView eventAdmin = (TextView) convertView.findViewById(R.id.single_event_admin);
            //TextView eventLocation = (TextView) convertView.findViewById(R.id.single_event_location);
            ///TextView eventDesc = (TextView) convertView.findViewById(R.id.single_event_desc);


            if (thisEvent != null) {
                /* Title */
                String title = "Unknown Event";
                if (thisEvent.getTitle() != null) {
                    title = thisEvent.getTitle().length() > 25 ?
                            thisEvent.getTitle().substring(0, 25) + "\u2026" : thisEvent.getTitle();
                }
                eventTitle.setText(title);

                /* Start Date */
                Date startDate = new Date(thisEvent.getStarttimeLong());
                String pattern = "EEEE MM/dd/yy hh:mm aa";
                SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
                eventStartTime.setText(sdf.format(startDate));

                /* Slapper Full Name */
                String adminFullName = "By";
                if(thisEvent.getAdmin() != null &&thisEvent.getAdmin().getFullName() != null) {
                    adminFullName += thisEvent.getAdmin().getFullName();
                } else {
                    adminFullName += "Unknown";
                }
                eventAdmin.setText(adminFullName);
            }

            return convertView;
        }

        @Nullable
        @Override
        public Event getItem(int position) {
            return thisList.get(position);
        }

        @Override
        public int getCount() {
            return thisList.size();
        }
}
