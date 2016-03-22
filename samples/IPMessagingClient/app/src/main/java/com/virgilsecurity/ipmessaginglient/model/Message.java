package com.virgilsecurity.ipmessaginglient.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Andrii Iakovenko.
 */
public class Message {

    @SerializedName("id")
    private Long id;

    @SerializedName("created_at")
    private Long createdAt;

    @SerializedName("sender_identifier")
    private String senderIdentifier;

    @SerializedName("message")
    private String message;


    /**
     * Create new instance of Message.
     */
    public Message() {};

    /**
     * Create new instance of Message.
     * @param message the message to set.
     */
    public Message(String message) {
        this.message = message;
    };

    /**
     * @return the message identifier.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set message identifier.
     * @param id the message identifier to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the message creation date as timestamp.
     */
    public Long getCreatedAt() {
        return createdAt;
    }

    /**
     * Set message creation date.
     * @param createdAt the creation date to set as timestamp.
     */
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the sender's identifier.
     */
    public String getSenderIdentifier() {
        return senderIdentifier;
    }

    /**
     * Set sender's identifier.
     * @param senderIdentifier the sender's identifier to set.
     */
    public void setSenderIdentifier(String senderIdentifier) {
        this.senderIdentifier = senderIdentifier;
    }

    /**
     * @return the message body.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set message text.
     * @param message the message body to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
