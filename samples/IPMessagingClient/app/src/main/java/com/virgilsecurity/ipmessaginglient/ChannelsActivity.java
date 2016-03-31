package com.virgilsecurity.ipmessaginglient;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.virgilsecurity.ipmessaginglient.adapter.ChannelAdapter;
import com.virgilsecurity.ipmessaginglient.db.Contract;
import com.virgilsecurity.ipmessaginglient.db.DBHelper;
import com.virgilsecurity.ipmessaginglient.model.Channel;
import com.virgilsecurity.ipmessaginglient.model.Identifier;
import com.virgilsecurity.ipmessaginglient.model.IdentityToken;
import com.virgilsecurity.ipmessaginglient.utils.CommonUtils;
import com.virgilsecurity.ipmessaginglient.utils.MessagingClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelsActivity extends AppCompatActivity {

    private static final String TAG = "ChannelsActivity";

    private ListView channelsLV;

    private String identifier;

    private DBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbHelper = new DBHelper(this);

        channelsLV = (ListView) findViewById(android.R.id.list);
        channelsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChannelAdapter adapter = (ChannelAdapter) channelsLV.getAdapter();
                Channel channel = adapter.getItem(position);

                Log.d(TAG, "Channel '" + channel.getChannelName() + "' selected");

                Intent intent = new Intent();
                intent.putExtra(Constants.EXTRA.CHANNEL_NAME, channel.getChannelName());
                intent.putExtra(Constants.EXTRA.IDENTITY_TOKEN, channel.getUserName());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        channelsLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ChannelAdapter adapter = (ChannelAdapter) channelsLV.getAdapter();
                Channel channel = adapter.getItem(position);

                removeChannel(channel.getChannelName());

                return true;
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(ChannelsActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                AlertDialog.Builder builder = new AlertDialog.Builder(ChannelsActivity.this);
                builder
                        .setView(input)
                        .setMessage(R.string.join_channel)
                        .setPositiveButton(R.string.join, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                joinChannel(input.getText().toString());
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Close the dialog
                            }
                        });
                builder.create().show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        identifier = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREFERENCES.IDENTITY, "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChannels();
    }

    private void getChannels() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                Contract.Channel._ID,
                Contract.Channel.COLUMN_NAME_NAME,
                Contract.Channel.COLUMN_NAME_TOKEN,
                Contract.Channel.COLUMN_NAME_LAST_MESSAGE_ID
        };

        String sortOrder = Contract.Channel.COLUMN_NAME_NAME + " DESC";

        Cursor c = db.query(Contract.Channel.TABLE_NAME, projection, null, null, null, null, sortOrder);

        ArrayList<Channel> channels = new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String name = c.getString(c.getColumnIndexOrThrow(Contract.Channel.COLUMN_NAME_NAME));
            String token = c.getString(c.getColumnIndexOrThrow(Contract.Channel.COLUMN_NAME_TOKEN));

            channels.add(new Channel(name, token));
        }

        ChannelAdapter adapter = new ChannelAdapter(this, channels);
        channelsLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void joinChannel(final String channelName) {
        MessagingClient.getMessagingService().joinChannel(channelName, new Identifier(identifier)).enqueue(new Callback<IdentityToken>() {
            @Override
            public void onResponse(Call<IdentityToken> call, Response<IdentityToken> response) {
                // Save channel in DB
                // Gets the data repository in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(Contract.Channel.COLUMN_NAME_NAME, channelName);
                values.put(Contract.Channel.COLUMN_NAME_TOKEN, response.body().getToken());
                values.put(Contract.Channel.COLUMN_NAME_LAST_MESSAGE_ID, 0);

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(Contract.Channel.TABLE_NAME, null, values);

                getChannels();
            }

            @Override
            public void onFailure(Call<IdentityToken> call, Throwable t) {
                CommonUtils.showToast(R.string.join_channel_failed);
            }
        });

    }

    private void removeChannel(final String name) {
        Log.d(TAG, "Removing channel '" + name);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(Contract.Channel.TABLE_NAME, Contract.Channel.COLUMN_NAME_NAME + " = ?", new String[] {name});

        getChannels();
    }

}
