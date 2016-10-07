package com.virgilsecurity.virgilsamples;

import android.test.AndroidTestCase;

import com.virgilsecurity.sdk.client.RequestSigner;
import com.virgilsecurity.sdk.client.VirgilClient;
import com.virgilsecurity.sdk.client.VirgilClientContext;
import com.virgilsecurity.sdk.client.exceptions.VirgilServiceException;
import com.virgilsecurity.sdk.client.model.Card;
import com.virgilsecurity.sdk.client.model.RevocationReason;
import com.virgilsecurity.sdk.client.model.dto.SearchCriteria;
import com.virgilsecurity.sdk.client.requests.CreateCardRequest;
import com.virgilsecurity.sdk.client.requests.RevokeCardRequest;
import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;

import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by Andrii Iakovenko on 07.10.16.
 */
public class ClientTest extends AndroidTestCase {

    private static String cardId;

    private Crypto crypto;
    private VirgilClient client;
    private RequestSigner requestSigner;
    private PrivateKey appKey;
    private KeyPair aliceKeys;

    private String CARDS_SERVICE = "https://cards-stg.virgilsecurity.com";
    private String RO_CARDS_SERVICE = "https://cards-ro-stg.virgilsecurity.com";
    private String IDENTITY_SERVICE = "https://identity-stg.virgilsecurity.com";

    private String APP_ID = "1ef2e45f6100792bc600828f1425b27ce7655a80543118f375bd894d7313aa00";
    private String APP_BUNDLE = "com.teonit.v4testapp";
    private String APP_TOKEN = "AT.11039a8217fc70c2d54dec1eef2638f5feb048c026f17f906b53e8019b56d58d";
    private String APP_PRIVATE_KEY_PASSWORD = "12345678";
    private String APP_PRIVATE_KEY = "-----BEGIN ENCRYPTED PRIVATE KEY-----\nMIGhMF0GCSqGSIb3DQEFDTBQMC8GCSqGSIb3DQEFDDAiBBASTCYEkLwzuwxJYJ19\npcddAgIRLDAKBggqhkiG9w0CCjAdBglghkgBZQMEASoEEJNMrCJkO6nmIRQcXkdG\nSwcEQD/W3xmpYWr1c780N3QPCzbyaQRnBcwVEOiTG4zbgd4qi8R009d2HslkFd1n\nB5ittvbB3FUnnrBPK+uoSltkG2o=\n-----END ENCRYPTED PRIVATE KEY-----\n";


    public void testFlow() {
        crypto = new VirgilCrypto();

        VirgilClientContext ctx = new VirgilClientContext(APP_TOKEN);

        if (StringUtils.isNotBlank(CARDS_SERVICE)) {
            ctx.setCardsServiceAddress(CARDS_SERVICE);
        }

        if (StringUtils.isNotBlank(RO_CARDS_SERVICE)) {
            ctx.setReadOnlyCardsServiceAddress(RO_CARDS_SERVICE);
        }

        if (StringUtils.isNotBlank(IDENTITY_SERVICE)) {
            ctx.setIdentityServiceAddress(IDENTITY_SERVICE);
        }

        client = new VirgilClient(ctx);
        requestSigner = new RequestSigner(crypto);

        appKey = crypto.importPrivateKey(APP_PRIVATE_KEY.getBytes(), APP_PRIVATE_KEY_PASSWORD);
        aliceKeys = crypto.generateKeys();

        // Create card
        byte[] exportedPublicKey = crypto.exportPublicKey(aliceKeys.getPublicKey());
        CreateCardRequest createCardRequest = new CreateCardRequest("alice", "username", exportedPublicKey);

        try {
            requestSigner.selfSign(createCardRequest, aliceKeys.getPrivateKey());
            requestSigner.authoritySign(createCardRequest, APP_ID, appKey);

            Card aliceCard = client.createCard(createCardRequest);

            assertNotNull(aliceCard);
            assertNotNull(aliceCard.getId());
            assertNotNull(aliceCard.getIdentity());
            assertNotNull(aliceCard.getIdentityType());
            assertNotNull(aliceCard.getScope());
            assertNotNull(aliceCard.getVersion());

            cardId = aliceCard.getId();
        } catch (VirgilServiceException e) {
            fail(e.getMessage());
        }

        // Get card
        try {
            Card card = client.getCard(cardId);
            assertNotNull(card);
            assertNotNull(card.getId());
            assertNotNull(card.getIdentity());
            assertNotNull(card.getIdentityType());
            assertNotNull(card.getScope());
            assertNotNull(card.getVersion());
        } catch (VirgilServiceException e) {
            fail(e.getMessage());
        }

        // Search application cards
        SearchCriteria criteria = SearchCriteria.byAppBundle(APP_BUNDLE);

        try {
            List<Card> cards = client.searchCards(criteria);
            assertNotNull(cards);
            assertFalse(cards.isEmpty());

            boolean found = false;
            for (Card card : cards) {
                if (APP_ID.equals(card.getId())) {
                    found = true;
                    break;
                }
            }
            assertTrue("Created card should be found by search", found);
        } catch (VirgilServiceException e) {
            fail(e.getMessage());
        }

        // Revoke card
        RevokeCardRequest revokeRequest = new RevokeCardRequest(cardId, RevocationReason.UNSPECIFIED);

        requestSigner.selfSign(revokeRequest, aliceKeys.getPrivateKey());
        requestSigner.authoritySign(revokeRequest, APP_ID, appKey);

        try {
            client.revokeCard(revokeRequest);
        } catch (VirgilServiceException e) {
            fail(e.getMessage());
        }
    }

}
