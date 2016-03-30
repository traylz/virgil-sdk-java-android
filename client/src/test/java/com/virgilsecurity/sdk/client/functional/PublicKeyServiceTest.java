/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.virgilsecurity.sdk.client.functional;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.ITestContext;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.virgilsecurity.sdk.client.PublicKeyClient;
import com.virgilsecurity.sdk.client.model.APIError;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.publickey.DeleteRequest;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria.Builder;
import com.virgilsecurity.sdk.client.model.publickey.Sign;
import com.virgilsecurity.sdk.client.model.publickey.SignResponse;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.client.service.PublicKeyService;
import com.virgilsecurity.sdk.crypto.CryptoHelper;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;

import retrofit2.Response;

/**
 *
 * @author Andrii Iakovenko
 */
public class PublicKeyServiceTest {

	private static final Logger LOGGER = Logger.getLogger(PublicKeyServiceTest.class.getName());

	static final String PUBLIC_KEY_SERVICE_GROUP = "publicKey_service";

	private String accessToken;
	private String appId;
	private String email;
	private String validationToken;

	private PublicKeyService publicKeyService;
	private List<VirgilCard> cards;

	private KeyPair keyPair;
	private VirgilCard signerCard;
	private VirgilCard signedCard;

	@BeforeGroups(groups = { PUBLIC_KEY_SERVICE_GROUP }, dependsOnGroups = {
			IdentityServiceTest.IDENTITY_SERVICE_GROUP })
	public void beforePublicKey(ITestContext ctx) {
		accessToken = (String) ctx.getAttribute(ContextOfFunctionalTest.ACCESS_TOKEN);
		appId = (String) ctx.getAttribute(ContextOfFunctionalTest.APPLICATION_ID);
		email = (String) ctx.getAttribute(ContextOfFunctionalTest.SERVICE_EMAIL);
		validationToken = (String) ctx.getAttribute(ContextOfFunctionalTest.SERVICE_VALIDATION_TOKEN);

		keyPair = KeyPairGenerator.generate();
		publicKeyService = new PublicKeyClient(ContextOfFunctionalTest.PUBLIC_KEYS_STG_HOST, accessToken)
				.createService(PublicKeyService.class, keyPair.getPrivate());
	}

	@Test(groups = { PUBLIC_KEY_SERVICE_GROUP })
	public void createCard() {
		ValidatedIdentity identity = new ValidatedIdentity();
		identity.setType(IdentityType.EMAIL);
		identity.setValue(email);

		VirgilCardTemplate request = new VirgilCardTemplate();
		request.setPublicKey(keyPair.getPublic().getAsBase64String());
		request.setIdentity(identity);

		try {
			Response<VirgilCard> response = publicKeyService.createCard(request).execute();
			if (response.isSuccessful()) {

			} else {
				APIError error = new APIError(response.code(), response.errorBody().string());
				LOGGER.log(Level.SEVERE, "Create card. {0}", error.toString());
			}

			assertTrue(response.isSuccessful());
			assertNotNull(response.body());

			signerCard = response.body();
			assertNotNull(signerCard);

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, null, e);
			fail(e.getMessage());
		}
	}

	@Test(groups = { PUBLIC_KEY_SERVICE_GROUP }, dependsOnMethods = "createCard")
	public void searchForCards() {
		Builder criteriaBuilder = new Builder();
		criteriaBuilder.setValue(email);
		criteriaBuilder.setIncludeUnconfirmed(true);

		try {
			Response<List<VirgilCard>> response = publicKeyService.search(criteriaBuilder.build()).execute();

			if (response.isSuccessful()) {
			} else {
				APIError error = new APIError(response.code(), response.errorBody().string());
				LOGGER.log(Level.SEVERE, "Search card. {0}", error.toString());
			}

			assertTrue(response.isSuccessful());
			assertNotNull(response.body());

			List<VirgilCard> cards = response.body();
			assertFalse(cards.isEmpty());

			for (VirgilCard card : cards) {
				assertNotNull(card.getId());
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, null, e);
			fail(e.getMessage());
		}
	}

	@Test(groups = { PUBLIC_KEY_SERVICE_GROUP }, dependsOnMethods = "createCard")
	public void searchForApplicationCards() {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setValue(appId);

		try {
			Response<List<VirgilCard>> response = publicKeyService.searchApp(criteria).execute();

			if (response.isSuccessful()) {
			} else {
				APIError error = new APIError(response.code(), response.errorBody().string());
				LOGGER.log(Level.SEVERE, "Search card. {0}", error.toString());
			}

			assertTrue(response.isSuccessful());
			assertNotNull(response.body());

			cards = response.body();
			assertFalse(cards.isEmpty());

			for (VirgilCard card : cards) {
				assertNotNull(card.getId());
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, null, e);
			fail(e.getMessage());
		}
	}

	@Test(groups = { PUBLIC_KEY_SERVICE_GROUP }, dependsOnMethods = "searchForApplicationCards")
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

		signedCard = new PublicKeyClient(ContextOfFunctionalTest.PUBLIC_KEYS_STG_HOST, accessToken)
				.createCard(vcBuilder.build(), kp.getPrivate());
		assertNotNull(signedCard);

		/* Sign created Virgil Card */
		// Create sign
		Sign sign = new Sign();
		sign.setSignedCardId(signedCard.getId());
		try {
			sign.setSignedDigest(CryptoHelper.sign(signedCard.getHash(), keyPair.getPrivate()));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, null, e);
			fail(e.getMessage());
		}
		// Sign card
		try {
			Response<SignResponse> response = publicKeyService.signCard(signerCard.getId(), signerCard.getId(), sign)
					.execute();
			assertTrue(response.isSuccessful());

			SignResponse signData = response.body();

			assertNotNull(signData);
			assertNotNull(signData.getId());
			assertNotNull(signData.getCreatedAt());
			assertNotNull(signData.getSignerCardId());
			assertNotNull(signData.getSignedCardId());
			assertNotNull(signData.getSignedDigest());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, null, e);
			fail(e.getMessage());
		}
	}

	@Test(groups = { PUBLIC_KEY_SERVICE_GROUP }, dependsOnMethods = "signCard")
	public void unsignCard() {
		Sign sign = new Sign();
		sign.setSignedCardId(signedCard.getId());

		try {
			Response<Void> response = publicKeyService.unsignCard(signerCard.getId(), signerCard.getId(), sign)
					.execute();
			assertTrue(response.isSuccessful());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, null, e);
			fail(e.getMessage());
		}
	}

	@Test(groups = { PUBLIC_KEY_SERVICE_GROUP }, dependsOnMethods = { "createCard" })
	public void getPublicKey() {

	}

	@Test(groups = { PUBLIC_KEY_SERVICE_GROUP }, dependsOnMethods = { "createCard", "unsignCard" })
	public void deleteCard() {

		// Delete all Virgil Cards
		ValidatedIdentity identity = new ValidatedIdentity();
		identity.setType(IdentityType.EMAIL);
		identity.setValue(email);
		identity.setToken(validationToken);

		DeleteRequest request = new DeleteRequest();
		request.setIdentity(identity);
		try {
			Response<Void> response = publicKeyService.delete(signerCard.getId(), signerCard.getId(), request)
					.execute();

			if (!response.isSuccessful()) {
				System.out.println(new APIError(response.code(), response.errorBody().string()));
			}
			assertTrue(response.isSuccessful());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, null, e);
			fail(e.getMessage());
		}

	}

}
