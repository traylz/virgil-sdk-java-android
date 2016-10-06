/*
 * Copyright (c) 2016, Virgil Security, Inc.
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of virgil nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.virgilsecurity.sdk.client;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

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

/**
 * Test cases for Virgil Client.
 *
 * @author Andrii Iakovenko
 *
 */
public class VirgilClientTest {

	private static String cardId;

	private Crypto crypto;
	private VirgilClient client;
	private RequestSigner requestSigner;
	private PrivateKey appKey;
	private KeyPair aliceKeys;

	private String APP_ID = "1ef2e45f6100792bc600828f1425b27ce7655a80543118f375bd894d7313aa00";
	private String APP_BUNDLE = "com.teonit.v4testapp";
	private String APP_TOKEN = "AT.11039a8217fc70c2d54dec1eef2638f5feb048c026f17f906b53e8019b56d58d";
	private String APP_PRIVATE_KEY_PASSWORD = "12345678";
	private String APP_PRIVATE_KEY = "-----BEGIN ENCRYPTED PRIVATE KEY-----\n"
			+ "MIGhMF0GCSqGSIb3DQEFDTBQMC8GCSqGSIb3DQEFDDAiBBASTCYEkLwzuwxJYJ19\n"
			+ "pcddAgIRLDAKBggqhkiG9w0CCjAdBglghkgBZQMEASoEEJNMrCJkO6nmIRQcXkdG\n"
			+ "SwcEQD/W3xmpYWr1c780N3QPCzbyaQRnBcwVEOiTG4zbgd4qi8R009d2HslkFd1n\n" + "B5ittvbB3FUnnrBPK+uoSltkG2o="
			+ "-----END ENCRYPTED PRIVATE KEY-----";

	@BeforeSuite
	public void setUp() {
		crypto = new VirgilCrypto();

		VirgilClientContext ctx = new VirgilClientContext(APP_TOKEN);

		ctx.setCardsServiceAddress("https://cards-stg.virgilsecurity.com");
		ctx.setReadOnlyCardsServiceAddress("https://cards-ro-stg.virgilsecurity.com");
		ctx.setIdentityServiceAddress("https://identity-stg.virgilsecurity.com");

		client = new VirgilClient(ctx);
		requestSigner = new RequestSigner(crypto);

		appKey = crypto.importPrivateKey(APP_PRIVATE_KEY.getBytes(), APP_PRIVATE_KEY_PASSWORD);
		aliceKeys = crypto.generateKeys();
	}

	@Test(priority = 1)
	public void createCard() throws VirgilServiceException {
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

	}

	@Test(dependsOnMethods = "createCard", priority = 2)
	public void getCard() {
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
	}

	@Test(dependsOnMethods = "createCard", priority = 3)
	public void searchCards_byApp() {
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
			assertTrue(found, "Created card should be found by search");
		} catch (VirgilServiceException e) {
			fail(e.getMessage());
		}
	}

	@Test(dependsOnMethods = "createCard", priority = 3)
	public void searchCards_byIdentity() {
		SearchCriteria criteria = SearchCriteria.byIdentity("alice");

		try {
			List<Card> cards = client.searchCards(criteria);
			assertNotNull(cards);
			assertFalse(cards.isEmpty());

			boolean found = false;
			for (Card card : cards) {
				if (cardId.equals(card.getId())) {
					found = true;
					break;
				}
			}
			assertTrue(found, "Created card should be found by search");
		} catch (VirgilServiceException e) {
			fail(e.getMessage());
		}
	}

	@Test(dependsOnMethods = "createCard", priority = 3)
	public void searchCards_byIdentities() {
		SearchCriteria criteria = SearchCriteria.byIdentities(Arrays.asList("alice"));

		try {
			List<Card> cards = client.searchCards(criteria);
			assertNotNull(cards);
			assertFalse(cards.isEmpty());

			boolean found = false;
			for (Card card : cards) {
				if (cardId.equals(card.getId())) {
					found = true;
					break;
				}
			}
			assertTrue(found, "Created card should be found by search");
		} catch (VirgilServiceException e) {
			fail(e.getMessage());
		}
	}

	@Test(dependsOnMethods = "createCard", priority = 4)
	public void revokeCard() {
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
