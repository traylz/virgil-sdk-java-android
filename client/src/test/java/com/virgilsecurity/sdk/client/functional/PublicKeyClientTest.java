/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.virgilsecurity.sdk.client.functional;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.exceptions.ServiceException;
import com.virgilsecurity.sdk.client.model.Identity;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.Token;
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

/**
 *
 * @author Andrii Iakovenko
 */
public class PublicKeyClientTest extends GenericFunctionalTest {
	static final String PUBLIC_KEY_CLIENT_GROUP = "publicKey_client";

	/** Global cards authorized with this authorizer. */
	public static final String VIRGILSECURITY_AUTHORIZER = "com.virgilsecurity.identity";

	private static final String CUSTOM_IDENTITY_TYPE = "custom";

	private String appId;
	private String emailAddress;
	private String accessToken;
	private PrivateKey appPrivateKey;
	private Password appPrivateKeyPassword;

	private KeyPair keyPair;

	@BeforeClass(dependsOnGroups = {IdentityClientTest.IDENTITY_CLIENT_GROUP})
	public void setUp() {
		appId = getPropertyByName(APPLICATION_ID);
		emailAddress = getPropertyByName(CLIENT_EMAIL);
		accessToken = getPropertyByName(ACCESS_TOKEN);

		appPrivateKey = new PrivateKey(StringUtils.replace(getPropertyByName(APPLICATION_PRIVATE_KEY), "\\n", "\n"));
		appPrivateKeyPassword = new Password(getPropertyByName(APPLICATION_PRIVATE_KEY_PASSWORD));

		keyPair = KeyPairGenerator.generate();
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP })
	public void createGlobalCardUnautorized_success() {
		ClientFactory factory = createClientFactory(accessToken);

		Identity identity = new ValidatedIdentity(IdentityType.EMAIL, emailAddress);

		VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder().setPublicKey(keyPair.getPublic())
				.setIdentity(identity);

		VirgilCard card = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(card);
		assertNotNull(card.getId());
		assertNotNull(card.getCreatedAt());
		assertNotNull(card.getIdentity());
		assertNotNull(card.getIdentity().getId());
		assertEquals(IdentityType.EMAIL, card.getIdentity().getType());
		assertEquals(emailAddress, card.getIdentity().getValue());
		assertNull(card.getAuthorizedBy());
		assertNotNull(card.getPublicKey());
		assertNotNull(card.getPublicKey().getId());
		assertNotNull(card.getPublicKey().getKey());
		assertNotNull(card.getPublicKey().getCreatedAt());
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP })
	public void createGlobalCardAutorized_success() {
		ClientFactory factory = createClientFactory(accessToken);

		Identity identity = new Identity(IdentityType.EMAIL, emailAddress);

		// Verify and confirm identity
		String actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, emailAddress);
		String confirmationCode = getConfirmationCodeFromEmail(emailAddress);
		identity = factory.getIdentityClient().confirm(actionId, confirmationCode);

		VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder().setPublicKey(keyPair.getPublic())
				.setIdentity(identity);

		VirgilCard card = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(card);
		assertNotNull(card.getId());
		assertNotNull(card.getCreatedAt());
		assertNotNull(card.getIdentity());
		assertNotNull(card.getIdentity().getId());
		assertEquals(IdentityType.EMAIL, card.getIdentity().getType());
		assertEquals(emailAddress, card.getIdentity().getValue());
		assertEquals(VIRGILSECURITY_AUTHORIZER, card.getAuthorizedBy());
		assertNotNull(card.getPublicKey());
		assertNotNull(card.getPublicKey().getId());
		assertNotNull(card.getPublicKey().getKey());
		assertNotNull(card.getPublicKey().getCreatedAt());
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP })
	public void createPrivateCard_success() {
		ClientFactory factory = createClientFactory(accessToken);

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
		assertNotNull(card.getId());
		assertNotNull(card.getCreatedAt());
		assertNotNull(card.getIdentity());
		assertNotNull(card.getIdentity().getId());
		assertEquals(CUSTOM_IDENTITY_TYPE, card.getIdentity().getType());
		assertEquals(identityValue, card.getIdentity().getValue());
		assertEquals(appId, card.getAuthorizedBy());
		assertNotNull(card.getPublicKey());
		assertNotNull(card.getPublicKey().getId());
		assertNotNull(card.getPublicKey().getKey());
		assertNotNull(card.getPublicKey().getCreatedAt());
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP })
	public void createPrivateCardWithoutValiadtionToken_success() {
		ClientFactory factory = createClientFactory(accessToken);

		String identityValue = UUID.randomUUID().toString();

		ValidatedIdentity identity = new ValidatedIdentity(CUSTOM_IDENTITY_TYPE, identityValue);

		VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder().setPublicKey(keyPair.getPublic())
				.setIdentity(identity);

		VirgilCard card = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(card);
		assertNotNull(card.getId());
		assertNotNull(card.getCreatedAt());
		assertNotNull(card.getIdentity());
		assertNotNull(card.getIdentity().getId());
		assertEquals(CUSTOM_IDENTITY_TYPE, card.getIdentity().getType());
		assertEquals(identityValue, card.getIdentity().getValue());
		assertNull(card.getAuthorizedBy());
		assertNotNull(card.getPublicKey());
		assertNotNull(card.getPublicKey().getId());
		assertNotNull(card.getPublicKey().getKey());
		assertNotNull(card.getPublicKey().getCreatedAt());
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP })
	public void searchNonExistingCard_success() {
		ClientFactory factory = createClientFactory(accessToken);

		Builder criteriaBuilder = new Builder();
		criteriaBuilder.setValue(UUID.randomUUID().toString() + "@mailinator.com");
		criteriaBuilder.setIncludeUnauthorized(true);

		List<VirgilCard> cards = factory.getPublicKeyClient().search(criteriaBuilder.build());

		assertNotNull(cards);
		assertTrue(cards.isEmpty());
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP })
	public void searchGlobalCardByEmail_success() {
		ClientFactory factory = createClientFactory(accessToken);

		// Create card first
		Identity identity = new Identity(IdentityType.EMAIL, emailAddress);

		// Verify and confirm identity
		String actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, emailAddress);
		String confirmationCode = getConfirmationCodeFromEmail(emailAddress);
		identity = factory.getIdentityClient().confirm(actionId, confirmationCode);

		VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder().setPublicKey(keyPair.getPublic())
				.setIdentity(identity);

		VirgilCard virgilCard = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(virgilCard);

		// Build search criteria
		Builder criteriaBuilder = new Builder();
		criteriaBuilder.setType(IdentityType.EMAIL);
		criteriaBuilder.setValue(emailAddress);

		List<VirgilCard> cards = factory.getPublicKeyClient().search(criteriaBuilder.build());

		assertNotNull(cards);
		assertFalse(cards.isEmpty());

		boolean found = false;
		for (VirgilCard card : cards) {
			assertNotNull(card.getId());
			assertNotNull(card.getCreatedAt());
			assertNotNull(card.getIdentity());
			assertNotNull(card.getIdentity().getId());

			if (virgilCard.getId().equals(card.getId())) {
				found = true;
			}
		}
		assertTrue("Created card should be found", found);
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP })
	public void searchGlobalCardByApplication_success() {
		ClientFactory factory = createClientFactory(accessToken);

		// Build search criteria
		Builder criteriaBuilder = new Builder();
		criteriaBuilder.setType(IdentityType.APPLICATION);
		criteriaBuilder.setValue(VIRGILSECURITY_AUTHORIZER);

		List<VirgilCard> cards = factory.getPublicKeyClient().search(criteriaBuilder.build());

		assertNotNull(cards);
		assertFalse(cards.isEmpty());

		for (VirgilCard card : cards) {
			assertNotNull(card.getId());
			assertNotNull(card.getCreatedAt());
			assertNotNull(card.getIdentity());
			assertNotNull(card.getIdentity().getId());
		}
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP })
	public void searchPrivateCard_Authorized_success() {
		ClientFactory factory = createClientFactory(accessToken);

		// Create authorized private card first
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

		VirgilCard virgilCard = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(virgilCard);

		// Build search criteria
		Builder criteriaBuilder = new Builder();
		criteriaBuilder.setValue(identityValue);

		List<VirgilCard> cards = factory.getPublicKeyClient().search(criteriaBuilder.build());

		assertNotNull(cards);
		assertFalse(cards.isEmpty());

		boolean found = false;
		for (VirgilCard card : cards) {
			assertNotNull(card.getId());
			assertNotNull(card.getCreatedAt());
			assertNotNull(card.getIdentity());
			assertNotNull(card.getIdentity().getId());

			if (virgilCard.getId().equals(card.getId())) {
				found = true;
			}
		}
		assertTrue("Created card should be found", found);
	}
	
	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP })
	public void searchPrivateCard_Unauthorized_notFound() {
		ClientFactory factory = createClientFactory(accessToken);

		// Create authorized private card first
		String identityValue = UUID.randomUUID().toString();

		ValidatedIdentity identity = new ValidatedIdentity(CUSTOM_IDENTITY_TYPE, identityValue);

		VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder().setPublicKey(keyPair.getPublic())
				.setIdentity(identity);

		VirgilCard virgilCard = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(virgilCard);

		// Build search criteria
		Builder criteriaBuilder = new Builder();
		criteriaBuilder.setValue(identityValue);

		List<VirgilCard> cards = factory.getPublicKeyClient().search(criteriaBuilder.build());

		assertNotNull(cards);
		
		for (VirgilCard card : cards) {
			if (virgilCard.getId().equals(card.getId())) {
				fail("Created card should NOT be found");
			}
		}
	}
	
	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP })
	public void searchPrivateCard_Unauthorized_IncludeUnauthorized_success() {
		ClientFactory factory = createClientFactory(accessToken);

		// Create authorized private card first
		String identityValue = UUID.randomUUID().toString();

		ValidatedIdentity identity = new ValidatedIdentity(CUSTOM_IDENTITY_TYPE, identityValue);

		VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder().setPublicKey(keyPair.getPublic())
				.setIdentity(identity);

		VirgilCard virgilCard = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(virgilCard);

		// Build search criteria
		Builder criteriaBuilder = new Builder();
		criteriaBuilder.setValue(identityValue);
		criteriaBuilder.setIncludeUnauthorized(true);

		List<VirgilCard> cards = factory.getPublicKeyClient().search(criteriaBuilder.build());

		assertNotNull(cards);
		assertFalse(cards.isEmpty());

		boolean found = false;
		for (VirgilCard card : cards) {
			assertNotNull(card.getId());
			assertNotNull(card.getCreatedAt());
			assertNotNull(card.getIdentity());
			assertNotNull(card.getIdentity().getId());

			if (virgilCard.getId().equals(card.getId())) {
				found = true;
			}
		}
		assertTrue("Created card should be found", found);
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP })
	public void getGlobalCardsPublicKey_success() {
		ClientFactory factory = createClientFactory(accessToken);

		// Create card first
		Identity identity = new Identity(IdentityType.EMAIL, emailAddress);

		VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder().setPublicKey(keyPair.getPublic())
				.setIdentity(identity);

		VirgilCard card = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(card);

		// Get public key
		PublicKeyInfo publicKey = factory.getPublicKeyClient().getKey(card.getPublicKey().getId());

		assertNotNull(publicKey);
		assertNotNull(publicKey.getId());
		assertNotNull(publicKey.getCreatedAt());
		assertNotNull(publicKey.getKey());
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP }, expectedExceptions = { ServiceException.class }, enabled = false)
	public void getNonExistingPublicKey_fail() {
		ClientFactory factory = createClientFactory(accessToken);

		factory.getPublicKeyClient().getKey(UUID.randomUUID().toString());
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP })
	public void deleteGlobalCard_success() {
		ClientFactory factory = createClientFactory(accessToken);

		// Create authorized card first
		Identity identity = new Identity(IdentityType.EMAIL, emailAddress);
		// Verify and confirm identity
		String actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, emailAddress);
		String confirmationCode = getConfirmationCodeFromEmail(emailAddress);
		// Confirm identity for two validations
		Token token = new Token();
		token.setCountToLive(2);
		identity = factory.getIdentityClient().confirm(actionId, confirmationCode, token);

		VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder().setPublicKey(keyPair.getPublic())
				.setIdentity(identity);

		VirgilCard card = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(card);

		factory.getPublicKeyClient().deleteCard((ValidatedIdentity) identity, card.getId(), keyPair.getPrivate());
	}

	@Test(groups = { PUBLIC_KEY_CLIENT_GROUP })
	public void deletePrivateCard_success() {
		ClientFactory factory = createClientFactory(accessToken);

		// Create authorized card first
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

		// Generate validation token one more time
		try {
			String validationToken = ValidationTokenGenerator.generate(CUSTOM_IDENTITY_TYPE, identityValue,
					appPrivateKey, appPrivateKeyPassword);
			identity.setToken(validationToken);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		// Delete created card
		factory.getPublicKeyClient().deleteCard(identity, card.getId(), keyPair.getPrivate());
	}

}
