package com.virgilsecurity.ipmessaginglient.model;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents identity token which should be used for
 * Created by Andrii Iakovenko.
 */
public class IdentityToken {

    @SerializedName("identity_token")
    private String token;

    public IdentityToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
