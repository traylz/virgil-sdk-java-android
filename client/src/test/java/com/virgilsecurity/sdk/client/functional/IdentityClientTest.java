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

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.exceptions.ServiceException;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;

/**
 * Functional tests for Identity client.
 *
 * @author Andrii Iakovenko
 *
 */
public class IdentityClientTest extends GenericFunctionalTest {

	public static final String IDENTITY_CLIENT_GROUP = "IDENTITY_CLIENT_GROUP";

	private String emailAddress;
	private String accessToken;

	@BeforeClass
	public void setUp() {
		emailAddress = getPropertyByName(CLIENT_EMAIL);
		accessToken = getPropertyByName(ACCESS_TOKEN);

		try {
			clearMailbox(emailAddress);
		} catch (IOException e) {
			fail("Mailbox is not empty");
		}
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP })
	public void verifyEmailIdentity_success() throws IOException, InterruptedException {
		ClientFactory factory = createClientFactory(accessToken);
		String actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, emailAddress);

		assertNotNull(getConfirmationCodeFromEmail(emailAddress));

		assertNotNull(actionId);
		assertFalse(StringUtils.isBlank(actionId));
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP })
	public void verifyEmailIdentityNoAccessToken_success() throws IOException, InterruptedException {
		ClientFactory factory = createClientFactory(null);
		String actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, emailAddress);

		assertNotNull(getConfirmationCodeFromEmail(emailAddress));

		assertNotNull(actionId);
		assertFalse(StringUtils.isBlank(actionId));
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP }, expectedExceptions = { ServiceException.class })
	public void verifyApplicationIdentity_fail() {
		ClientFactory factory = createClientFactory(accessToken);
		factory.getIdentityClient().verify(IdentityType.APPLICATION, emailAddress);
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP }, expectedExceptions = { ServiceException.class })
	public void verifyApplicationIdentityNoAccessToken_fail() {
		ClientFactory factory = createClientFactory(null);
		factory.getIdentityClient().verify(IdentityType.APPLICATION, emailAddress);
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP }, expectedExceptions = { ServiceException.class })
	public void verifyCustomIdentity_fail() {
		ClientFactory factory = createClientFactory(accessToken);
		factory.getIdentityClient().verify("custom", emailAddress);
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP }, expectedExceptions = { ServiceException.class })
	public void verifyCustomIdentityNoAccessToken_fail() {
		ClientFactory factory = createClientFactory(null);
		factory.getIdentityClient().verify("custom", emailAddress);
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP })
	public void confirmValidConfirmationCodeWithAccessToken_success() throws IOException, InterruptedException {
		ClientFactory factory = createClientFactory(null);
		String actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, emailAddress);

		assertNotNull(actionId);

		String confirmationCode = getConfirmationCodeFromEmail(emailAddress);

		assertNotNull(confirmationCode);

		factory = createClientFactory(accessToken);

		ValidatedIdentity identity = factory.getIdentityClient().confirm(actionId, confirmationCode);

		assertNotNull(identity);
		assertEquals(emailAddress, identity.getValue());
		assertEquals(IdentityType.EMAIL, identity.getType());
		assertNotNull(identity.getToken());
		assertFalse(identity.getToken().isEmpty());
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP })
	public void confirmValidConfirmationCodeNoAccessToken_success() throws IOException, InterruptedException {
		ClientFactory factory = createClientFactory(null);
		String actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, emailAddress);

		assertNotNull(actionId);

		String confirmationCode = getConfirmationCodeFromEmail(emailAddress);

		assertNotNull(confirmationCode);

		ValidatedIdentity identity = factory.getIdentityClient().confirm(actionId, confirmationCode);

		assertNotNull(identity);
		assertEquals(emailAddress, identity.getValue());
		assertEquals(IdentityType.EMAIL, identity.getType());
		assertNotNull(identity.getToken());
		assertFalse(identity.getToken().isEmpty());
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP }, expectedExceptions = { ServiceException.class })
	public void confirmInalidConfirmationCode_fail() throws IOException, InterruptedException {
		ClientFactory factory = createClientFactory(null);
		String actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, emailAddress);

		assertNotNull(actionId);

		getConfirmationCodeFromEmail(emailAddress);
		factory.getIdentityClient().confirm(actionId, "InVaLiDCoDe");
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP }, enabled = false)
	public void validate_success() throws IOException, InterruptedException {
		ClientFactory factory = createClientFactory(accessToken);
		String actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, emailAddress);

		assertNotNull(actionId);

		String confirmationCode = getConfirmationCodeFromEmail(emailAddress);

		assertNotNull(confirmationCode);

		ValidatedIdentity identity = factory.getIdentityClient().confirm(actionId, confirmationCode);

		assertNotNull(identity);

		boolean validated = factory.getIdentityClient().validate(identity);
		assertTrue(validated);
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP })
	public void validateNoAccessToken_success() throws IOException, InterruptedException {
		ClientFactory factory = createClientFactory(null);
		String actionId = factory.getIdentityClient().verify(IdentityType.EMAIL, emailAddress);

		assertNotNull(actionId);

		String confirmationCode = getConfirmationCodeFromEmail(emailAddress);

		assertNotNull(confirmationCode);

		ValidatedIdentity identity = factory.getIdentityClient().confirm(actionId, confirmationCode);

		assertNotNull(identity);

		boolean validated = factory.getIdentityClient().validate(identity);
		assertTrue(validated);
	}

	@Test(groups = { IDENTITY_CLIENT_GROUP })
	public void validateInvalidToken_fail() {
		ClientFactory factory = createClientFactory(accessToken);

		ValidatedIdentity identity = new ValidatedIdentity(IdentityType.EMAIL, emailAddress);
		identity.setToken("InVaLidToKeN");

		boolean validated = factory.getIdentityClient().validate(identity);
		assertFalse(validated);
	}

}
