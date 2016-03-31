package com.virgilsecurity.ipmessaginglient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.virgilsecurity.ipmessaginglient.adapter.MessageAdapter;
import com.virgilsecurity.ipmessaginglient.model.ChatMember;
import com.virgilsecurity.ipmessaginglient.model.EncryptedMessage;
import com.virgilsecurity.ipmessaginglient.model.Message;
import com.virgilsecurity.ipmessaginglient.utils.MessagingClient;
import com.virgilsecurity.sdk.client.utils.StringUtils;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChannelActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ChannelActivity";

    private ListView messagesLV;
    MessageAdapter adapter;

    private String channelName;
    private String token;
    private long lastMessageId;

    private ChatMember me;

    private Timer timer;


    private Map<String, String> recipients = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        setContentView(R.layout.activity_channel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messagesLV = (ListView) findViewById(android.R.id.list);
        adapter = new MessageAdapter(this, new ArrayList<Message>());
        messagesLV.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isBlank(channelName)) {
                    Snackbar.make(view, R.string.channel_is_not_set, Snackbar.LENGTH_LONG)
                            .setAction(R.string.open_channels, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openChannels();
                                }
                            }).show();
                    return;
                }

                // Send message
                sendMessage();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadMyCard();
    }

    @Override
    protected void onStart() {
        super.onStart();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                loadMessages();
            }
        }, 3000, 3000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    private void sendMessage() {
        final EditText input = new EditText(ChannelActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(ChannelActivity.this);
        builder
                .setView(input)
                .setMessage(R.string.send_message)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String messageText = input.getText().toString();
                        new SendMessageTask().execute(messageText);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close the dialog
                    }
                });
        builder.create().show();
    }

    private void loadMessages() {
        if (StringUtils.isBlank(channelName) || StringUtils.isBlank(token)) {
            return;
        }
        new LoadMessagesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Constants.REQUEST_CODE.SETTINGS == requestCode) {
            if (RESULT_OK == resultCode) {
                loadMyCard();
            }
        } else if (Constants.REQUEST_CODE.CHANNELS == requestCode) {
            if (RESULT_OK == resultCode) {
                if (channelName == null || !channelName.equals(data.getStringExtra(Constants.EXTRA.CHANNEL_NAME))) {
                    lastMessageId = 0;
                }
                channelName = data.getStringExtra(Constants.EXTRA.CHANNEL_NAME);
                token = data.getStringExtra(Constants.EXTRA.IDENTITY_TOKEN);

                loadMessages();
            }
        }
    }

    private void openChannels() {
        Intent intent = new Intent(this, ChannelsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, Constants.REQUEST_CODE.CHANNELS);
    }

    private void loadMyCard() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        me = new ChatMember();
        me.setIdentity(prefs.getString(Constants.PREFERENCES.IDENTITY, ""));
        me.setCardId(prefs.getString(Constants.PREFERENCES.VIRGIL_CARD_ID, ""));

        String value = prefs.getString(Constants.PREFERENCES.PUBLIC_KEY, "");
        if (!StringUtils.isBlank(value)) {
            me.setPublicKey(new PublicKey(value));
        }

        value = prefs.getString(Constants.PREFERENCES.PRIVATE_KEY, "");
        if (!StringUtils.isBlank(value)) {
            me.setPrivateKey(new PrivateKey(value));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.channels, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, Constants.REQUEST_CODE.SETTINGS);
        } else if (id == R.id.nav_channels) {
            openChannels();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class SendMessageTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            EncryptedMessage em = MessagingClient.getInstance().encryptMessage(me, channelName, token, params[0]);
            MessagingClient.getInstance().sendMessage(me, channelName, token, new Gson().toJson(em));

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadMessages();
        }
    }

    class LoadMessagesTask extends AsyncTask<Void, Void, List<Message>> {

        @Override
        protected List<Message> doInBackground(Void... params) {
            List<Message> messages = MessagingClient.getInstance().getMessages(me, channelName, token, lastMessageId);
            if (!messages.isEmpty()) {
                lastMessageId = messages.get(messages.size() - 1).getId();
            }
            return messages;
        }

        @Override
        protected void onPostExecute(List<Message> messages) {
            super.onPostExecute(messages);

            adapter.addAll(messages);
            adapter.notifyDataSetChanged();
        }
    }
}
