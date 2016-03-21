package com.virgilsecurity.ipmessaginglient.model;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents Identify identifier.
 * Created by Andrii Iakovenko.
 *
 * @see  com.virgilsecurity.sdk.client.model.Identity
 */
public class Identifier {

    @SerializedName("identifier")
    private String identifier;

    /**
     * Create new instance of Identifier.
     * @param identifier the identity's identifier.
     */
    public Identifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the identity's identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

}
