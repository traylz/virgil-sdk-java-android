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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;

import retrofit2.Response;

/**
 * Functional tests for Identity client.
 *
 * @author Andrii Iakovenko
 *
 */
public class IdentityClientTest {

	private static final Logger LOGGER = Logger.getLogger(IdentityClientTest.class.getName());

	static final String IDENTITY_CLIENT_GROUP = "identity_client";

	private ClientFactory factory;

	private MailinatorClient mailClient;
	private String account;
	private String email;

	private String actionId = null;
	private String confirmationCode;
	private String validationToken;

	@BeforeGroups(groups = { IDENTITY_CLIENT_GROUP }, dependsOnGroups = { IdentityServiceTest.IDENTITY_SERVICE_GROUP })
	public void beforeGroups(ITestContext ctx) {
		account =  (String) ctx.getAttribute(ContextOfFunctionalTest.CLIENT_ACCOUNT);
		email = (String) ctx.getAttribute(ContextOfFunctionalTest.CLIENT_EMAIL);
		mailClient = new MailinatorClient();
		
		factory = (ClientFactory) ctx.getAttribute(ContextOfFunctionalTest.CLIENT_FACTORY);
	}
	
	@AfterClass
	public void afterClass(ITestContext ctx) {
		ctx.setAttribute(ContextOfFunctionalTest.CLIENT_VALIDATION_TOKEN, validationToken);
	}

	@BeforeGroups(groups = { IDENTITY_CLIENT_GROUP })
	public void beforeIdentity() {
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP })
	public void identity_verify() {
		actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, email);

		assertNotNull(actionId);
		assertFalse(actionId.isEmpty());
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP }, dependsOnMethods = "identity_verify")
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

	@Test(groups = { IDENTITY_CLIENT_GROUP }, dependsOnMethods = "identity_waitForEmail")
	public void identity_confirm() {

		ValidatedIdentity identity = factory.getIdentityClient().confirm(actionId, confirmationCode);
		validationToken = identity.getToken();

		assertNotNull(validationToken);
		assertFalse(validationToken.isEmpty());
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP }, dependsOnMethods = "identity_confirm")
	public void identity_validate() {

		ValidatedIdentity identity = new ValidatedIdentity();
		identity.setType(IdentityType.EMAIL);
		identity.setValue(email);
		identity.setToken(validationToken);

		boolean validated = factory.getIdentityClient().validate(identity);
		assertTrue(validated);
	}

}
