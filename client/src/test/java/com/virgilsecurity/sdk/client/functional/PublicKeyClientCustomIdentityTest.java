/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.virgilsecurity.sdk.client.functional;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.util.List;
import java.util.UUID;

import org.testng.ITestContext;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.publickey.PublicKeyInfo;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria.Builder;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.client.utils.ValidationTokenGenerator;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;
import com.virgilsecurity.sdk.crypto.Password;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.utils.Obfuscator;

/**
 *
 * Functional test for custom identity.
 * 
 * @author Andrii Iakovenko
 */
public class PublicKeyClientCustomIdentityTest {

	static final String CUSTOM_IDENTITY_CLIENT_GROUP = "custom_identity";

	private String identityValue;
	private PrivateKey appPrivateKey;
	private Password appPrivateKeyPassword;
	private ClientFactory factory;

	private KeyPair keyPair;

	private VirgilCard virgilCard;

	@BeforeGroups(groups = { CUSTOM_IDENTITY_CLIENT_GROUP })
	public void beforePublicKey(ITestContext ctx) {
		identityValue = UUID.randomUUID().toString();
		appPrivateKey = new PrivateKey((String) ctx.getAttribute(ContextOfFunctionalTest.APPLICATION_PRIVATE_KEY));
		appPrivateKeyPassword = new Password(
				(String) ctx.getAttribute(ContextOfFunctionalTest.APPLICATION_PRIVATE_KEY_PASSWORD));
		factory = (ClientFactory) ctx.getAttribute(ContextOfFunctionalTest.CLIENT_FACTORY);

		keyPair = KeyPairGenerator.generate();
	}

	@Test(groups = { CUSTOM_IDENTITY_CLIENT_GROUP })
	public void createCard() {
		ValidatedIdentity identity = new ValidatedIdentity();
		identity.setType(IdentityType.CUSTOM);
		identity.setValue(identityValue);

		try {
			String validationToken = ValidationTokenGenerator.generate(IdentityType.CUSTOM, identityValue,
					appPrivateKey, appPrivateKeyPassword);
			identity.setToken(validationToken);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder().setPublicKey(keyPair.getPublic())
				.setIdentity(identity);

		virgilCard = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(virgilCard);
	}

	@Test(groups = { CUSTOM_IDENTITY_CLIENT_GROUP }, dependsOnMethods = "createCard")
	public void searchForCards() {
		Builder criteriaBuilder = new Builder();
		criteriaBuilder.setType(IdentityType.CUSTOM);
		criteriaBuilder.setValue(identityValue);

		List<VirgilCard> cards = factory.getPublicKeyClient().search(criteriaBuilder.build());

		assertNotNull(cards);
		assertFalse(cards.isEmpty());

		boolean found = false;
		for (VirgilCard card : cards) {
			assertNotNull(card.getId());

			if (virgilCard.getId().equals(card.getId())) {
				found = true;
			}
		}

		assertTrue(found);
	}

	@Test(groups = { CUSTOM_IDENTITY_CLIENT_GROUP }, dependsOnMethods = { "createCard" })
	public void getPublicKey() {
		PublicKeyInfo publicKey = factory.getPublicKeyClient().getKey(virgilCard.getPublicKey().getId());

		assertNotNull(publicKey);
		assertNotNull(publicKey.getId());
		assertNotNull(publicKey.getCreatedAt());
		assertNotNull(publicKey.getKey());
	}

	@Test(groups = { CUSTOM_IDENTITY_CLIENT_GROUP }, dependsOnMethods = { "createCard", "getPublicKey" })
	public void deleteCard() {
		ValidatedIdentity identity = new ValidatedIdentity();
		identity.setType(IdentityType.CUSTOM);
		identity.setValue(identityValue);

		try {
			String validationToken = ValidationTokenGenerator.generate(IdentityType.CUSTOM, identityValue,
					appPrivateKey, appPrivateKeyPassword);
			identity.setToken(validationToken);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		factory.getPublicKeyClient().deleteCard(identity, virgilCard.getId(), keyPair.getPrivate());
	}

	@Test(groups = { CUSTOM_IDENTITY_CLIENT_GROUP })
	public void createCard_HashedIdentity_SuccessfullyCreatedCard() {
		String hashedIdentity = Obfuscator.obfuscate(identityValue, "724fTy6JmZxTNuM7");

		ValidatedIdentity identity = new ValidatedIdentity();
		identity.setType(IdentityType.CUSTOM);
		identity.setValue(hashedIdentity);

		try {
			String validationToken = ValidationTokenGenerator.generate(IdentityType.CUSTOM, hashedIdentity,
					appPrivateKey, appPrivateKeyPassword);
			identity.setToken(validationToken);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder()
				.setPublicKey(keyPair.getPublic().getAsBase64String()).setIdentity(identity);

		VirgilCard virgilCard = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(virgilCard);
		assertNotNull(virgilCard.getId());
		assertNotNull(virgilCard.getIdentity());
		assertEquals(IdentityType.CUSTOM, virgilCard.getIdentity().getType());
		assertEquals(hashedIdentity, virgilCard.getIdentity().getValue());
	}

}
