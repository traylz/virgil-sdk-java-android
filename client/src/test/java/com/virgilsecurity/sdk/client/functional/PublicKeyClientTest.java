/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.virgilsecurity.sdk.client.functional;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;

import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.publickey.PublicKeyInfo;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria.Builder;
import com.virgilsecurity.sdk.client.model.publickey.SignResponse;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;

/**
 *
 * @author Andrii Iakovenko
 */
public class PublicKeyClientTest {

	static final String PUBLIC_KEY_CLIENT_GROUP = "publicKey_client";

	private String appId;
	private String email;
	private String validationToken;
	private ClientFactory factory;

	private List<VirgilCard> cards;

	private KeyPair keyPair;
	private VirgilCard signerCard;
	private VirgilCard signedCard;

	@BeforeGroups(groups = { PUBLIC_KEY_CLIENT_GROUP }, dependsOnGroups = { IdentityClientTest.IDENTITY_CLIENT_GROUP })
	public void beforePublicKey(ITestContext ctx) {
		appId = (String) ctx.getAttribute(ContextOfFunctionalTest.APPLICATION_ID);
		email = (String) ctx.getAttribute(ContextOfFunctionalTest.CLIENT_EMAIL);
		validationToken = (String) ctx.getAttribute(ContextOfFunctionalTest.CLIENT_VALIDATION_TOKEN);
		factory = (ClientFactory) ctx.getAttribute(ContextOfFunctionalTest.CLIENT_FACTORY);

		keyPair = KeyPairGenerator.generate();
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP })
	public void createCard() {
		ValidatedIdentity identity = new ValidatedIdentity();
		identity.setType(IdentityType.EMAIL);
		identity.setValue(email);

		VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder()
				.setPublicKey(keyPair.getPublic().getAsBase64String()).setIdentity(identity);

		signerCard = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(signerCard);
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP }, dependsOnMethods = "createCard")
	public void searchForCards() {
		Builder criteriaBuilder = new Builder();
		criteriaBuilder.setValue(email);
		criteriaBuilder.setIncludeUnconfirmed(true);

		List<VirgilCard> cards = factory.getPublicKeyClient().search(criteriaBuilder.build(), keyPair.getPrivate());

		assertNotNull(cards);
		assertFalse(cards.isEmpty());

		for (VirgilCard card : cards) {
			assertNotNull(card.getId());
		}
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP }, dependsOnMethods = "createCard")
	public void searchForApplicationCards() {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setValue(appId);

		cards = factory.getPublicKeyClient().searchApp(criteria, keyPair.getPrivate());
		assertFalse(cards.isEmpty());

		for (VirgilCard card : cards) {
			assertNotNull(card.getId());
		}
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP }, dependsOnMethods = "searchForApplicationCards")
	public void signCard() {
		// Create new Virgil Card for signing
		ValidatedIdentity identity = new ValidatedIdentity();
		identity.setType(IdentityType.EMAIL);
		identity.setValue(email);

		KeyPair kp = KeyPairGenerator.generate();
		VirgilCardTemplate request = new VirgilCardTemplate();
		request.setPublicKey(kp.getPublic().getAsBase64String());
		request.setIdentity(identity);

		VirgilCardTemplate.Builder vcBuilder = new VirgilCardTemplate.Builder().setIdentity(identity)
				.setPublicKey(kp.getPublic());

		signedCard = factory.getPublicKeyClient().createCard(vcBuilder.build(), kp.getPrivate());
		assertNotNull(signedCard);

		// Sign created Virgil Card
		SignResponse signData = factory.getPublicKeyClient().signCard(signedCard.getId(), signedCard.getHash(),
				signerCard.getId(), keyPair.getPrivate());
		assertNotNull(signData);
		assertNotNull(signData.getId());
		assertNotNull(signData.getCreatedAt());
		assertNotNull(signData.getSignerCardId());
		assertNotNull(signData.getSignedCardId());
		assertNotNull(signData.getSignedDigest());
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP }, dependsOnMethods = "signCard")
	public void unsignCard() {
		factory.getPublicKeyClient().unsignCard(signedCard.getId(), signerCard.getId(), keyPair.getPrivate());
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP }, dependsOnMethods = { "createCard" })
	public void getPublicKey() {
		PublicKeyInfo publicKey = factory.getPublicKeyClient().getKey(signerCard.getPublicKey().getId());

		assertNotNull(publicKey);
		assertNotNull(publicKey.getId());
		assertNotNull(publicKey.getCreatedAt());
		assertNotNull(publicKey.getKey());
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP }, dependsOnMethods = { "createCard", "unsignCard", "getPublicKey" })
	public void deleteCard() {
		ValidatedIdentity identity = new ValidatedIdentity();
		identity.setType(IdentityType.EMAIL);
		identity.setValue(email);
		identity.setToken(validationToken);

		factory.getPublicKeyClient().deleteCard(identity, signerCard.getId(), keyPair.getPrivate());
	}

}
