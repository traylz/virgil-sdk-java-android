package com.virgilsecurity.ipmessaginglient.service;

import com.virgilsecurity.ipmessaginglient.Constants;
import com.virgilsecurity.ipmessaginglient.model.Channel;
import com.virgilsecurity.ipmessaginglient.model.Identifier;
import com.virgilsecurity.ipmessaginglient.model.IdentityToken;
import com.virgilsecurity.ipmessaginglient.model.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Andrii Iakovenko.
 */
public interface IPMessagingService {

    @GET("/channels")
    @Deprecated
    Call<Channel> getChannels();

    @POST("/channels/{channel_name}/join")
    Call<IdentityToken> joinChannel(@Path("channel_name") String channelName, @Body Identifier identifier);

    @POST("/channels/{channel_name}/leave")
    @Deprecated
    Call<IdentityToken> leaveChannel(@Path("channel_name") String channelName, @Header(Constants.HEADERS.IDENTITY_TOKEN) String identityToken, @Body Identifier identifier);

    @GET("/channels/{channel_name}/members")
    Call<List<Identifier>> channelMembers(@Path("channel_name") String channelName, @Header(Constants.HEADERS.IDENTITY_TOKEN) String identityToken);

    @POST("/channels/{channel_name}/messages")
    Call<Void> postMessage(@Path("channel_name") String channelName, @Header(Constants.HEADERS.IDENTITY_TOKEN) String identityToken, @Body Message message);

    @GET("/channels/{channel_name}/messages")
    Call<List<Message>> getMessages(@Path("channel_name") String channelName, @Header(Constants.HEADERS.IDENTITY_TOKEN) String identityToken, @Query("last_message_id") Long lastMessageId);

}
