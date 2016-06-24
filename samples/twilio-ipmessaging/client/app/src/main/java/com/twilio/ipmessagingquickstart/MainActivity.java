package com.twilio.ipmessagingquickstart;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.twilio.common.TwilioAccessManager;
import com.twilio.common.TwilioAccessManagerFactory;
import com.twilio.common.TwilioAccessManagerListener;
import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.ChannelListener;
import com.twilio.ipmessaging.Constants;
import com.twilio.ipmessaging.ErrorInfo;
import com.twilio.ipmessaging.Member;
import com.twilio.ipmessaging.Message;
import com.twilio.ipmessaging.TwilioIPMessagingClient;
import com.twilio.ipmessaging.TwilioIPMessagingSDK;
import com.twilio.ipmessaging.UserInfo;
import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.model.Identity;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.crypto.Base64;
import com.virgilsecurity.sdk.crypto.CryptoHelper;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {


    final static String DEFAULT_CHANNEL_NAME = "general";
    final static String TAG = "TwilioIPMessaging";

    private RecyclerView mMessagesRecyclerView;
    private MessagesAdapter mMessagesAdapter;
    private ArrayList<Message> mMessages = new ArrayList<>();

    private EditText mWriteMessageEditText;
    private Button mSendChatMessageButton;

    private TwilioAccessManager mAccessManager;
    private TwilioIPMessagingClient mMessagingClient;

    private Channel mGeneralChannel;

    private String mServerTokenURL;
    private ClientFactory clientFactory;

    private String mIdentity;
    private String mCardId;
    private PrivateKey mPrivateKey;

    private Map<String, ChatMember> mMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMessagesRecyclerView = (RecyclerView) findViewById(R.id.messagesRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // for a chat app, show latest at the bottom
        layoutManager.setStackFromEnd(true);
        mMessagesRecyclerView.setLayoutManager(layoutManager);

        mMessagesAdapter = new MessagesAdapter();
        mMessagesRecyclerView.setAdapter(mMessagesAdapter);

        mWriteMessageEditText = (EditText) findViewById(R.id.writeMessageEditText);

        mSendChatMessageButton = (Button) findViewById(R.id.sendChatMessageButton);
        mSendChatMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGeneralChannel != null) {
                    String messageBody = mWriteMessageEditText.getText().toString();

                    SendMessageTask sendTask = new SendMessageTask(messageBody);
                    sendTask.execute((Void) null);
                }
            }
        });

        mMembers = new ConcurrentHashMap<>();

        // Load preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // URL of the token service
        mServerTokenURL = prefs.getString(com.twilio.ipmessagingquickstart.Constants.Prefs.TOKEN_SERVICE, "http://localhost:4567/token");

        // Load current user card preferences
        mIdentity = prefs.getString(com.twilio.ipmessagingquickstart.Constants.Prefs.IDENTITY, "");
        mCardId = prefs.getString(com.twilio.ipmessagingquickstart.Constants.Prefs.CARD_ID, "");
        mPrivateKey = new PrivateKey(prefs.getString(com.twilio.ipmessagingquickstart.Constants.Prefs.PRIVATE_KEY, ""));

        // Initialize Client Factory
        String accessToken = prefs.getString(com.twilio.ipmessagingquickstart.Constants.Prefs.ACCESS_TOKEN, "");
        clientFactory = new ClientFactory(accessToken);

        retrieveAccessTokenFromServer();
    }

    private void retrieveAccessTokenFromServer() {
        Ion.with(this)
                .load(mServerTokenURL)
                .addQuery("identity", mIdentity)
                .addQuery("device", Settings.Secure.getString(getBaseContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e == null) {
                            String identity = result.get("identity").getAsString();
                            String accessToken = result.get("token").getAsString();

                            setTitle(identity);

                            mAccessManager = TwilioAccessManagerFactory.createAccessManager(accessToken,
                                    mAccessManagerListener);

                            TwilioIPMessagingClient.Properties props =
                                    new TwilioIPMessagingClient.Properties(
                                            TwilioIPMessagingClient.SynchronizationStrategy.ALL, 500);

                            mMessagingClient = TwilioIPMessagingSDK.createClient(mAccessManager, props,
                                    mMessagingClientCallback);

                            mMessagingClient.getChannels().loadChannelsWithListener(
                                    new Constants.StatusListener() {
                                        @Override
                                        public void onSuccess() {
                                            final Channel defaultChannel = mMessagingClient.getChannels()
                                                    .getChannelByUniqueName(DEFAULT_CHANNEL_NAME);
                                            if (defaultChannel != null) {
                                                joinChannel(defaultChannel);
                                            } else {
                                                Map<String, Object> channelProps = new HashMap<>();
                                                channelProps.put(Constants.CHANNEL_FRIENDLY_NAME, "General Chat Channel");
                                                channelProps.put(Constants.CHANNEL_UNIQUE_NAME, DEFAULT_CHANNEL_NAME);
                                                channelProps.put(Constants.CHANNEL_TYPE, Channel.ChannelType.CHANNEL_TYPE_PUBLIC);
                                                mMessagingClient.getChannels().createChannel(channelProps, new Constants.CreateChannelListener() {
                                                    @Override
                                                    public void onCreated(final Channel channel) {
                                                        if (channel != null) {
                                                            Log.d(TAG, "Created default channel");
                                                            MainActivity.this.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    joinChannel(channel);
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(ErrorInfo errorInfo) {
                                                        Log.e(TAG, "Error creating channel: " + errorInfo.getErrorText());
                                                    }
                                                });
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(MainActivity.this,
                                    R.string.error_retrieving_access_token, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    private void joinChannel(final Channel channel) {
        Log.d(TAG, "Joining Channel: " + channel.getUniqueName());
        channel.join(new Constants.StatusListener() {
            @Override
            public void onSuccess() {
                mGeneralChannel = channel;
                Log.d(TAG, "Joined default channel");
                mGeneralChannel.setListener(mDefaultChannelListener);
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.e(TAG, "Error joining channel: " + errorInfo.getErrorText());
            }
        });
    }

    private TwilioAccessManagerListener mAccessManagerListener = new TwilioAccessManagerListener() {
        @Override
        public void onTokenExpired(TwilioAccessManager twilioAccessManager) {
            Log.d(TAG, "Access token has expired");
        }

        @Override
        public void onTokenUpdated(TwilioAccessManager twilioAccessManager) {
            Log.d(TAG, "Access token has updated");
        }

        @Override
        public void onError(TwilioAccessManager twilioAccessManager, String errorMessage) {
            Log.d(TAG, "Error with Twilio Access Manager: " + errorMessage);
        }
    };

    private Constants.CallbackListener<TwilioIPMessagingClient> mMessagingClientCallback =
            new Constants.CallbackListener<TwilioIPMessagingClient>() {
                @Override
                public void onSuccess(TwilioIPMessagingClient twilioIPMessagingClient) {
                    Log.d(TAG, "Success creating Twilio IP Messaging Client");
                }
            };

    private ChannelListener mDefaultChannelListener = new ChannelListener() {
        @Override
        public void onMessageAdd(final Message message) {
            Log.d(TAG, "Message added");
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // need to modify user interface elements on the UI thread
                    mMessages.add(message);
                    mMessagesAdapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public void onMessageChange(Message message) {
            Log.d(TAG, "Message changed: " + message.getMessageBody());
        }

        @Override
        public void onMessageDelete(Message message) {
            Log.d(TAG, "Message deleted");
        }

        @Override
        public void onMemberJoin(Member member) {
            Log.d(TAG, "Member joined: " + member.getUserInfo().getIdentity());
        }

        @Override
        public void onMemberChange(Member member) {
            Log.d(TAG, "Member changed: " + member.getUserInfo().getIdentity());
        }

        @Override
        public void onMemberDelete(Member member) {
            Log.d(TAG, "Member deleted: " + member.getUserInfo().getIdentity());
        }

        @Override
        public void onAttributesChange(Map<String, String> map) {
            Log.d(TAG, "Attributes changed: " + map.toString());
        }

        @Override
        public void onTypingStarted(Member member) {
            Log.d(TAG, "Started Typing: " + member.getUserInfo().getIdentity());
        }

        @Override
        public void onTypingEnded(Member member) {
            Log.d(TAG, "Ended Typing: " + member.getUserInfo().getIdentity());
        }

        @Override
        public void onSynchronizationChange(Channel channel) {

        }
    };


    class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {

            public TextView mMessageTextView;

            public ViewHolder(TextView textView) {
                super(textView);
                mMessageTextView = textView;
            }
        }

        public MessagesAdapter() {

        }

        @Override
        public MessagesAdapter
                .ViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            TextView messageTextView = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_text_view, parent, false);
            return new ViewHolder(messageTextView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Message message = mMessages.get(position);
            String messageBody = message.getMessageBody();

            try {
                messageBody = CryptoHelper.decrypt(messageBody, mCardId, mPrivateKey);
            } catch (Exception e) {
                Log.e(TAG, "Can't decrypt message", e);
                // TODO: show error message
            }

            String messageText = String.format("%s: %s", message.getAuthor(), messageBody);
            holder.mMessageTextView.setText(messageText);

        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }
    }

    private static class ChatMember {
        String cardId;
        PublicKey publicKey;

        public ChatMember(String cardId, PublicKey publicKey) {
            this.cardId = cardId;
            this.publicKey = publicKey;
        }
    }

    public class SendMessageTask extends AsyncTask<Void, Void, Boolean> {

        private String mMessageBody;

        public SendMessageTask(String messageBody) {
            this.mMessageBody = messageBody;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Find all VirgilCards for all chat members
            for (Member member : mGeneralChannel.getMembers().getMembers()) {
                String sid = member.getSid();
                if (mMembers.containsKey(sid)) {
                    continue;
                }

                String identity = member.getUserInfo().getIdentity();

                // Find Virgil Card for valid emails only
                if (isValidEmail(identity)) {

                    Log.d(TAG, "Looking for: " + identity);

                    SearchCriteria.Builder criteriaBuilder = new SearchCriteria.Builder();
                    criteriaBuilder.setValue(identity).setIncludeUnauthorized(true);
                    List<VirgilCard> cards = clientFactory.getPublicKeyClient().search(criteriaBuilder.build());

                    if (!cards.isEmpty()) {
                        VirgilCard card = cards.get(0);

                        String cardId = card.getId();
                        PublicKey publicKey = new PublicKey(Base64.decode(card.getPublicKey().getKey()));

                        mMembers.put(sid, new ChatMember(cardId, publicKey));

                        Log.w(TAG, "Found card: " + cardId);
                    } else {
                        Log.w(TAG, "No cards for: " + identity);
                    }
                }
            }

            // Build recipients map
            Map<String, PublicKey> recipients = new HashMap<>();
            for (ChatMember member : mMembers.values()) {
                recipients.put(member.cardId, member.publicKey);
            }

            // Encode message body
            if (!recipients.isEmpty()) {
                try {
                    mMessageBody = CryptoHelper.encrypt(mMessageBody, recipients);
                } catch (Exception e) {
                    // TODO: show error message
                }
            }

            // If message was not encrypted, sent is as is (unencrypted)
            Message message = mGeneralChannel.getMessages().createMessage(mMessageBody);
            Log.d(TAG, "Message created");
            mGeneralChannel.getMessages().sendMessage(message, new Constants.StatusListener() {
                @Override
                public void onSuccess() {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // need to modify user interface elements on the UI thread
                            mWriteMessageEditText.setText("");
                        }
                    });
                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    Log.e(TAG, "Error sending message: " + errorInfo.getErrorText());
                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
