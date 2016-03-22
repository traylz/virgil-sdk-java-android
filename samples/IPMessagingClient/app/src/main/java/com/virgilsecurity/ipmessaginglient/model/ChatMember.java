package com.virgilsecurity.ipmessaginglient.model;


import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.crypto.Base64;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;

/**
 * Created by Andrii Iakovenko.
 */
public class ChatMember {

    /* Virgil Card identifier. */
    private String cardId;

    /* The identity. */
    private String identity;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public ChatMember() {}

    public ChatMember(VirgilCard virgilCard) {
        this.cardId = virgilCard.getId();
        this.identity = virgilCard.getIdentity().getValue();

        this.publicKey = new PublicKey(Base64.decode(virgilCard.getPublicKey().getKey()));
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}
