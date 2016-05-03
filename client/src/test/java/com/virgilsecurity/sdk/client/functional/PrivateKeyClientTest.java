/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.virgilsecurity.sdk.client.functional;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.fail;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.model.Identity;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.Token;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.privatekey.PrivateKeyInfo;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria.Builder;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.client.utils.ValidationTokenGenerator;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;
import com.virgilsecurity.sdk.crypto.Password;
import com.virgilsecurity.sdk.crypto.PrivateKey;

/**
 * Functional tests for Private Keys client.
 * 
 * @author Andrii Iakovenko
 */
public class PrivateKeyClientTest extends GenericFunctionalTest {

	private static final String VIRGILSECURITY_PRIVATE_KEYS_AUTHORIZER = "com.virgilsecurity.private-keys";

	private static final String CUSTOM_IDENTITY_TYPE = "custom";

	static final String PRIVATE_KEY_CLIENT_GROUP = "privateKey_client";

	private String appId;
	private String accessToken;
	private String emailAddress;

	private PrivateKey appPrivateKey;
	private Password appPrivateKeyPassword;

	private KeyPair keyPair;

	@BeforeClass(dependsOnGroups = {PublicKeyClientTest.PUBLIC_KEY_CLIENT_GROUP})
	public void setUp() {
		appId = getPropertyByName(APPLICATION_ID);
		emailAddress = getPropertyByName(CLIENT_EMAIL);
		accessToken = getPropertyByName(ACCESS_TOKEN);

		appPrivateKey = new PrivateKey(StringUtils.replace(getPropertyByName(APPLICATION_PRIVATE_KEY), "\\n", "\n"));
		appPrivateKeyPassword = new Password(getPropertyByName(APPLICATION_PRIVATE_KEY_PASSWORD));

		keyPair = KeyPairGenerator.generate();
	}

	@Test(groups = { PRIVATE_KEY_CLIENT_GROUP })
	public void stashGetDeleteGlobalCardKey_success() {
		ClientFactory factory = createClientFactory(accessToken);

		// Get service card
		Builder criteriaBuilder = new Builder();
		criteriaBuilder.setType(IdentityType.APPLICATION);
		criteriaBuilder.setValue(VIRGILSECURITY_PRIVATE_KEYS_AUTHORIZER);

		List<VirgilCard> cards = factory.getPublicKeyClient().search(criteriaBuilder.build());

		assertNotNull(cards);
		assertFalse(cards.isEmpty());
		VirgilCard serviceCard = cards.get(0);

		// Create global card first
		Identity identity = new Identity(IdentityType.EMAIL, emailAddress);

		// Verify and confirm identity
		String actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, emailAddress);
		String confirmationCode = getConfirmationCodeFromEmail(emailAddress);
		Token token = new Token();
		token.setCountToLive(3);
		identity = factory.getIdentityClient().confirm(actionId, confirmationCode, token);

		VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder().setPublicKey(keyPair.getPublic())
				.setIdentity(identity);

		VirgilCard card = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(card);

		// Stash private key
		factory.getPrivateKeyClient(serviceCard).stash(card.getId(), keyPair.getPrivate());

		// Get private key
		PrivateKeyInfo privateKeyInfo = factory.getPrivateKeyClient(serviceCard).get(card.getId(),
				(ValidatedIdentity) identity);
		assertNotNull(privateKeyInfo);
		assertNotNull(privateKeyInfo.getCardId());
		assertEquals(keyPair.getPrivate().getAsBase64String(), privateKeyInfo.getKey());

		// Destroy private key
		factory.getPrivateKeyClient(serviceCard).destroy(card.getId(), keyPair.getPrivate());
	}

	@Test(groups = { PRIVATE_KEY_CLIENT_GROUP })
	public void stashGetDeletePrivateCardKey_success() {
		ClientFactory factory = createClientFactory(accessToken);

		// Get service card
		Builder criteriaBuilder = new Builder();
		criteriaBuilder.setType(IdentityType.APPLICATION);
		criteriaBuilder.setValue(VIRGILSECURITY_PRIVATE_KEYS_AUTHORIZER);

		List<VirgilCard> cards = factory.getPublicKeyClient().search(criteriaBuilder.build());

		assertNotNull(cards);
		assertFalse(cards.isEmpty());
		VirgilCard serviceCard = cards.get(0);

		// Create private card first
		String identityValue = UUID.randomUUID().toString();

		ValidatedIdentity identity = new ValidatedIdentity(CUSTOM_IDENTITY_TYPE, identityValue);

		try {
			String validationToken = ValidationTokenGenerator.generate(CUSTOM_IDENTITY_TYPE, identityValue,
					appPrivateKey, appPrivateKeyPassword);
			identity.setToken(validationToken);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder().setPublicKey(keyPair.getPublic())
				.setIdentity(identity);

		VirgilCard card = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(card);

		// Stash private key
		factory.getPrivateKeyClient(serviceCard).stash(card.getId(), keyPair.getPrivate());

		// Get private key
		try {
			String validationToken = ValidationTokenGenerator.generate(CUSTOM_IDENTITY_TYPE, identityValue,
					appPrivateKey, appPrivateKeyPassword);
			identity.setToken(validationToken);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		PrivateKeyInfo privateKeyInfo = factory.getPrivateKeyClient(serviceCard).get(card.getId(),
				(ValidatedIdentity) identity);
		assertNotNull(privateKeyInfo);
		assertNotNull(privateKeyInfo.getCardId());
		assertEquals(keyPair.getPrivate().getAsBase64String(), privateKeyInfo.getKey());

		// Destroy private key
		factory.getPrivateKeyClient(serviceCard).destroy(card.getId(), keyPair.getPrivate());
	}

}
