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
package com.virgilsecurity.sdk.client.functional;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.teonit.mailinator.MailinatorClient;
import org.teonit.mailinator.model.Inbox;
import org.teonit.mailinator.model.Message;
import org.teonit.mailinator.model.MessageData;
import org.testng.ITestContext;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.virgilsecurity.sdk.client.IdentityClient;
import com.virgilsecurity.sdk.client.model.Identity;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.Action;
import com.virgilsecurity.sdk.client.model.identity.Confirmation;
import com.virgilsecurity.sdk.client.model.identity.Token;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.service.IdentityService;

import retrofit2.Response;

/**
 * Functional tests for Identity service.
 *
 * @author Andrii Iakovenko
 *
 */
public class IdentityServiceTest {

	private static final Logger LOGGER = Logger.getLogger(IdentityServiceTest.class.getName());

	static final String IDENTITY_SERVICE_GROUP = "identity_service";

	private IdentityService identityService;

	private MailinatorClient mailClient;
	private String account;
	private String email;

	private String actionId = null;
	private String confirmationCode = null;
	private String validationToken = null;

	@BeforeGroups(groups = { IDENTITY_SERVICE_GROUP })
	public void beforeSuite(ITestContext ctx) {
		account = (String) ctx.getAttribute(ContextOfFunctionalTest.SERVICE_ACCOUNT);
		email = (String) ctx.getAttribute(ContextOfFunctionalTest.SERVICE_EMAIL);
		mailClient = new MailinatorClient();
	}

	@BeforeGroups(groups = { IDENTITY_SERVICE_GROUP })
	public void beforeIdentity() {
		identityService = new IdentityClient(ContextOfFunctionalTest.IDENTITY_STG_HOST).createService(IdentityService.class);
	}

	@Test(groups = { IDENTITY_SERVICE_GROUP })
	public void identity_verify() {
		Identity request = new Identity();
		request.setType(IdentityType.EMAIL);
		request.setValue(email);
		try {
			Response<Action> response = identityService.verify(request).execute();
			LOGGER.log(Level.FINE, "Verify. Response code: {}", response.code());
			assertTrue(response.isSuccessful());
			assertNotNull(response.body());

			Action verifyResponse = response.body();
			assertNotNull(verifyResponse.getActionId());
			assertFalse(verifyResponse.getActionId().isEmpty());
			actionId = verifyResponse.getActionId();
			LOGGER.log(Level.FINE, verifyResponse.getActionId());
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			fail(ex.getMessage());
		}
	}

	@Test(groups = { IDENTITY_SERVICE_GROUP }, dependsOnMethods = "identity_verify")
	public void identity_waitForEmail() throws IOException, InterruptedException {
		// Wait email during 2 minutes
		for (int i = 0; i < 12; i++) {
			// Read email from mail server
			Response<Inbox> response = mailClient.getService().inbox(account).execute();
			LOGGER.log(Level.FINE, "Emails. Response code: {}", response.code());
			if (response.isSuccessful() && !response.body().getItems().isEmpty()) {
				// Read email and extract confirmation code

				// Inbox contains only one email because we use new mailbox for
				// each test
				String messageId = response.body().getItems().get(0).getId();
				assertNotNull(messageId);
				assertFalse(messageId.isEmpty());
				Response<MessageData> messageResponse = null;
				for (int j = 0; j < 5; j++) {
					// Make several attempts to read email (mailinator can
					// refuse calls with 429 status)
					messageResponse = mailClient.getService().message(messageId).execute();
					LOGGER.log(Level.FINE, "Email. Response code: {}", response.code());
					if (messageResponse.isSuccessful()) {
						break;
					}
					// Try to get email in 5 sec
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
			// There is no email yet. Let's wait for 10 seconds
			Thread.sleep(10 * 1000);
		}
	}

	@Test(groups = { IDENTITY_SERVICE_GROUP }, dependsOnMethods = "identity_waitForEmail")
	public void identity_confirm() {
		Confirmation request = new Confirmation();
		request.setActionId(actionId);
		request.setConfirmationCode(confirmationCode);

		Token token = new Token();
		token.setTimeToLive(3600);
		token.setCountToLive(12);
		request.setToken(token);

		try {
			Response<ValidatedIdentity> response = identityService.confirm(request).execute();
			LOGGER.log(Level.FINE, "Confirm. Response code: {}", response.code());
			assertTrue(response.isSuccessful());
			assertNotNull(response.body());

			ValidatedIdentity confirmResponse = response.body();
			assertEquals(email, confirmResponse.getValue());
			assertEquals(IdentityType.EMAIL, confirmResponse.getType());
			assertNotNull(confirmResponse.getToken());

			validationToken = confirmResponse.getToken();
			LOGGER.log(Level.FINE, "Validation token: {}", validationToken);
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			fail(ex.getMessage());
		}
	}

	@Test(groups = { IDENTITY_SERVICE_GROUP }, dependsOnMethods = "identity_confirm")
	public void identity_validate() {
		ValidatedIdentity request = new ValidatedIdentity();
		request.setType(IdentityType.EMAIL);
		request.setValue(email);
		request.setToken(validationToken);

		try {
			Response<Void> response = identityService.validate(request).execute();
			LOGGER.log(Level.FINE, "Validate. Response code: {}", response.code());

			assertTrue(response.isSuccessful());
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			fail(ex.getMessage());
		}
	}

}
