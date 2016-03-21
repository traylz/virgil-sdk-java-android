package com.virgilsecurity.ipmessaginglient.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Andrii Iakovenko.
 */
public class EncryptedMessage {

    @SerializedName("message")
    private String message;

    @SerializedName("sign")
    private String sign;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
