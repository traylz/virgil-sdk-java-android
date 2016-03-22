package com.virgilsecurity.ipmessaginglient.db;

import android.provider.BaseColumns;

import com.virgilsecurity.ipmessaginglient.model.Channel;

/**
 * Created by Andrii Iakovenko.
 */
public class Contract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    public static final String DATABASE_NAME = "ipmessaging.db";
    public static final int DATABASE_VERSION = 1;

    public static abstract class Channel implements BaseColumns {
        public static final String TABLE_NAME = "channel";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_TOKEN = "token";
        public static final String COLUMN_NAME_LAST_MESSAGE_ID = "last_message_id";
    }

    public static final String SQL_CREATE_CHANNELS =
            "CREATE TABLE " + Channel.TABLE_NAME + " (" +
                    Channel._ID + " INTEGER PRIMARY KEY," +
                    Channel.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    Channel.COLUMN_NAME_TOKEN + TEXT_TYPE + COMMA_SEP +
                    Channel.COLUMN_NAME_LAST_MESSAGE_ID + INTEGER_TYPE +
                    " )";

    public static final String SQL_DELETE_CHANNELS =
            "DROP TABLE IF EXISTS " + Channel.TABLE_NAME;
}
