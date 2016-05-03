package com.virgilsecurity.ipmessaginglient.utils;

import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.virgilsecurity.ipmessaginglient.Application;
import com.virgilsecurity.ipmessaginglient.Constants;
import com.virgilsecurity.ipmessaginglient.model.ChatMember;
import com.virgilsecurity.ipmessaginglient.model.EncryptedMessage;
import com.virgilsecurity.ipmessaginglient.model.Identifier;
import com.virgilsecurity.ipmessaginglient.model.Message;
import com.virgilsecurity.ipmessaginglient.service.IPMessagingService;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.utils.StringUtils;
import com.virgilsecurity.sdk.crypto.CryptoHelper;
import com.virgilsecurity.sdk.crypto.PublicKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Andrii Iakovenko.
 */
public class MessagingClient {

    private static final String TAG = "MessagingClient";

    private static MessagingClient instance;

    private OkHttpClient.Builder httpClient;
    private Retrofit.Builder builder;

    private MembersCache cache;

    public static MessagingClient getInstance() {
        if (instance == null) {
            String baseUrl = PreferenceManager.getDefaultSharedPreferences(Application.getInstance()).getString(Constants.PREFERENCES.MESSAGING_SERVICE_ADDRESS, "");

            MessagingClient client = new MessagingClient();

            client.httpClient = new OkHttpClient.Builder();
            client.httpClient.readTimeout(60, TimeUnit.SECONDS);
            client.httpClient.connectTimeout(60, TimeUnit.SECONDS);

            Gson gson = new GsonBuilder().create();
            client.builder = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create(gson));

            client.cache = new MembersCache();

            instance = client;
        }

        return instance;
    }

    public static void reset() {
        instance = null;
    }

    private <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

    public static IPMessagingService getMessagingService() {
        return getInstance().createService(IPMessagingService.class);
    }

    /**
     * Get channel members.
     *
     * @param channelName
     * @param identityToken
     * @return
     */
    public List<ChatMember> getChannelMembers(String channelName, String identityToken) {
        List<ChatMember> members = new ArrayList<>();
        try {
            retrofit2.Response<List<Identifier>> response = MessagingClient.getMessagingService().channelMembers(channelName, identityToken).execute();
            if (response.code() < 400) {
                List<Identifier> identifiers = response.body();
                for (Identifier identifier : identifiers) {
                    ChatMember member = cache.getMember(identifier.getIdentifier());
                    if (member != null) {
                        members.add(member);
                    } else {
                        Log.w(TAG, "Member '" + identifier.getIdentifier() + "' not found");
                    }
                }
            } else {
                Log.e(TAG, "Get channel members error: " + response.code());
            }
        } catch (IOException e) {
            Log.e(TAG, "Get channel members error", e);
        }
        return members;
    }

    public EncryptedMessage encryptMessage(ChatMember me, String channelName, String identityToken, String message) {
        EncryptedMessage encryptedMessage = new EncryptedMessage();

        // Encrypt message for all channel members
        Map<String, PublicKey> recipients = new HashMap<>();
        for (ChatMember recipient : MessagingClient.getInstance().getChannelMembers(channelName, identityToken)) {
            recipients.put(recipient.getCardId(), recipient.getPublicKey());
        }

        try {
            String em = CryptoHelper.encrypt(message, recipients);
            String sign = CryptoHelper.signBase64(em, me.getPrivateKey());

            encryptedMessage.setMessage(em);
            encryptedMessage.setSign(sign);
        } catch (Exception e) {
            Log.e(TAG, "Encrypt message error", e);
        }

        return encryptedMessage;
    }

    public void sendMessage(ChatMember me, String channelName, String identityToken, String messageText) {

        Message message = new Message();
        message.setMessage(messageText);

        try {
            retrofit2.Response<Void> response = MessagingClient.getMessagingService().postMessage(channelName, identityToken, message).execute();
            if (response.code() >= 400) {
                Log.w(TAG, "Message is not sent. Status: " + response.code());
            }
        } catch (IOException e) {
            Log.e(TAG, "Sending message error", e);
        }
    }

    /**
     * Get new messages from channel
     *
     * @param me
     * @param channelName
     * @param identityToken
     * @param lastMessageId
     * @return
     */
    public List<Message> getMessages(ChatMember me, String channelName, String identityToken, Long lastMessageId) {
        Log.d(TAG, "Get messages from channel " + channelName);
        List<Message> messages = new ArrayList<>();
        try {
            Gson gson = new Gson();
            retrofit2.Response<List<Message>> response = MessagingClient.getMessagingService().getMessages(channelName, identityToken, lastMessageId).execute();
            if (response.code() < 400) {
                messages = response.body();
                Log.d(TAG, "Received messages count: " + messages.size());
                for (Message message : messages) {
                    try {
                        EncryptedMessage encryptedMessage = gson.fromJson(message.getMessage(), EncryptedMessage.class);
                        ChatMember sender = cache.getMember(message.getSenderIdentifier());
                        if (sender != null) {
                            try {
                                boolean isValid = CryptoHelper.verifyBase64(encryptedMessage.getMessage(), encryptedMessage.getSign(), sender.getPublicKey());
                                if (isValid) {
                                    message.setMessage(CryptoHelper.decrypt(encryptedMessage.getMessage(), me.getCardId(), me.getPrivateKey()));
                                } else {
                                    message.setMessage("The message signature is not valid");
                                }
                            } catch (Exception e) {
                                Log.w(TAG, "Message '" + message.getId() + "' processing error", e);
                                message.setMessage("The message couldn't be processed");
                            }
                        } else {
                            message.setMessage("Sender not found!");
                        }
                    }
                    catch (Exception e) {
                        Log.e(TAG, "Message '" + message.getId() + "' processing error", e);
                    }
                }
            } else {
                Log.e(TAG, "Get channel messages error: " + response.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Get channel messages error", e);
        }
        return messages;

    }

    public static class MembersCache {
        private Map<String, ChatMember> members;

        public MembersCache() {
            members = new HashMap<>();
        }

        public void addMember(String identifier, ChatMember member) {
            members.put(identifier, member);
        }

        public ChatMember getMember(String identifier) {
            if (StringUtils.isBlank(identifier)) {
                return null;
            }

            // chat member presents in cache
            if (members.containsKey(identifier)) {
                return members.get(identifier);
            }

            // obtain chat member from Virgil Service
            SearchCriteria.Builder criteriaBuilder = new SearchCriteria.Builder().setValue(identifier).setIncludeUnauthorized(true);
            List<VirgilCard> cards = Application.getClientFactory().getPublicKeyClient().search(criteriaBuilder.build());
            if (!cards.isEmpty()) {
                ChatMember member = new ChatMember(cards.get(0));

                members.put(identifier, member);
                return member;
            }
            return null;
        }

        public void clear() {
            members.clear();
        }
    }

}
