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

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.teonit.mailinator.MailinatorClient;
import org.teonit.mailinator.model.Inbox;
import org.teonit.mailinator.model.Message;
import org.teonit.mailinator.model.MessageData;
import org.testng.ITestContext;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.privatekey.PrivateKeyInfo;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;

import retrofit2.Response;

/**
 * Functional tests for Private Keys client.
 * 
 * @author Andrii Iakovenko
 */
public class PrivateKeyClientTest {

	static final String PRIVATE_KEY_CLIENT_GROUP = "privateKey_client";

	private String account;
	private String email;
	private ClientFactory factory;

	private VirgilCard serviceCard;

	private KeyPair keyPair;
	private VirgilCard virgilCard;
	private PrivateKeyInfo privateKeyInfo;

	@BeforeGroups(groups = { PRIVATE_KEY_CLIENT_GROUP }, dependsOnGroups = { IdentityClientTest.IDENTITY_CLIENT_GROUP })
	public void beforePrivateKey(ITestContext ctx) {
		account = (String) ctx.getAttribute(ContextOfFunctionalTest.CLIENT_ACCOUNT);
		email = (String) ctx.getAttribute(ContextOfFunctionalTest.CLIENT_EMAIL);
		factory = (ClientFactory) ctx.getAttribute(ContextOfFunctionalTest.CLIENT_FACTORY);

		keyPair = KeyPairGenerator.generate();
	}

	@Test(groups = { PRIVATE_KEY_CLIENT_GROUP }, dependsOnGroups = { IdentityClientTest.IDENTITY_CLIENT_GROUP })
	public void createCard() {
		ValidatedIdentity identity = obtainValidatedIdentity();

		VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder()
				.setPublicKey(keyPair.getPublic().getAsBase64String()).setIdentity(identity);

		virgilCard = factory.getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());
		assertNotNull(virgilCard);
	}

	@Test(groups = { PRIVATE_KEY_CLIENT_GROUP }, dependsOnMethods = "createCard")
	public void obtainServiceCard() {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setValue("com.virgilsecurity.private-keys");

		List<VirgilCard> cards = factory.getPublicKeyClient().searchApp(criteria);
		assertFalse(cards.isEmpty());
		serviceCard = cards.get(0);
	}

	@Test(groups = { PRIVATE_KEY_CLIENT_GROUP }, dependsOnMethods = "obtainServiceCard")
	public void stashPrivateKey() {
		factory.getPrivateKeyClient(serviceCard).stash(virgilCard.getId(), keyPair.getPrivate());
	}

	@Test(groups = { PRIVATE_KEY_CLIENT_GROUP }, dependsOnMethods = "stashPrivateKey")
	public void getPrivateKey() {
		ValidatedIdentity identity = obtainValidatedIdentity();

		privateKeyInfo = factory.getPrivateKeyClient(serviceCard).get(virgilCard.getId(), identity);

		assertNotNull(privateKeyInfo);
		assertNotNull(privateKeyInfo.getCardId());
		assertEquals(keyPair.getPrivate().getAsBase64String(), privateKeyInfo.getKey());
	}

	@Test(groups = { PRIVATE_KEY_CLIENT_GROUP }, dependsOnMethods = "getPrivateKey")
	public void destroyPrivateKey() {
		factory.getPrivateKeyClient(serviceCard).destroy(virgilCard.getId(), keyPair.getPrivate());
	}

	private ValidatedIdentity obtainValidatedIdentity() {
		// Get validation token
		String actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, email);
		String confirmationCode = null;

		MailinatorClient mailClient = new MailinatorClient();
		try {
			for (int i = 0; i < 12; i++) {
				Response<Inbox> response;

				response = mailClient.getService().inbox(account).execute();
				if (response.isSuccess() && !response.body().getItems().isEmpty()) {
					String messageId = response.body().getItems().get(0).getId();
					assertNotNull(messageId);
					assertFalse(messageId.isEmpty());
					Response<MessageData> messageResponse = null;
					for (int j = 0; j < 5; j++) {
						messageResponse = mailClient.getService().message(messageId).execute();
						if (messageResponse.isSuccess()) {
							break;
						}
						Thread.sleep(5 * 1000);
					}
					assertNotNull(messageResponse.body());
					Message message = messageResponse.body().getData();
					assertNotNull(message);
					String body = message.getParts().get(0).getBody();

					// Parse confirmation email an extract confirmation code
					Pattern pattern = Pattern.compile("(Your confirmation code is [^>]+[>])(\\w+)");
					Matcher matcher = pattern.matcher(body);
					if (matcher.find()) {
						confirmationCode = matcher.group(2);

						// Delete email
						try {
							mailClient.getService().delete(messageId).execute();
						} catch (Exception e) {

						}
					} else {
						fail("Can't find confirmation code in email");
					}
					break;
				}
				Thread.sleep(10 * 1000);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ValidatedIdentity identity = factory.getIdentityClient().confirm(actionId, confirmationCode);

		return identity;
	}

}
