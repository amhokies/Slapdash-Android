package com.virginiatech.slapdash.slapdash.SQLDatabaseService;

import android.provider.BaseColumns;

/**
 * Created by nima on 10/18/16.
 */

public final class EventReaderContract {

    private EventReaderContract(){}

    public static class EventEntry implements BaseColumns{
        public static final String TABLE_NAME = "Event Name";
        public static final String EVENT_ID = "_id";
        public static final String EVENT_TITLE = "Title";
        public static final String EVENT_DESCRIPTION = "Description";
        public static final String EVENT_ADMIN = "Admin";
        public static final String EVENT_START_TIME = "Start time";
        public static final String EVENT_STATUS = "Status";
        public static final String INIVITED_COUNT = "Invitations";
    }
}
