package com.virgilsecurity.ipmessaginglient.model;

/**
 * This class represents a remote channel of communication between multiple IP Messaging clients.
 * Members can be added or invited to join channels.
 *
 * Created by Andrii Iakovenko.
 */
public class Channel {

    private String userName;
    private String channelName;

    public Channel() {
    }

    public Channel(String channelName, String userName) {
        this.userName = userName;
        this.channelName = channelName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
