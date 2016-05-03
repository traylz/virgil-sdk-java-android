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

import static org.testng.AssertJUnit.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.mailinator.Email;
import com.mailinator.Email.EmailPart;
import com.mailinator.InboxMessage;
import com.mailinator.Mailinator;
import com.virgilsecurity.sdk.client.ClientFactory;

/**
 * Functional tests context.
 *
 * @author Andrii Iakovenko
 *
 */
public class GenericFunctionalTest {
	private static final Logger LOGGER = Logger.getLogger(GenericFunctionalTest.class.getName());

	static final String IDENTITY_STG_HOST = "https://identity-stg.virgilsecurity.com";
	static final String PUBLIC_KEYS_STG_HOST = "https://keys-stg.virgilsecurity.com";
	static final String PRIVATE_KEYS_STG_HOST = "https://keys-private-stg.virgilsecurity.com";

	// System properties
	public static final String IDENTITY_HOST = "IDENTITY_HOST";
	public static final String PUBLIC_KEYS_HOST = "PUBLIC_KEYS_HOST";
	public static final String PRIVATE_KEYS_HOST = "PRIVATE_KEYS_HOST";
	public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	public static final String APPLICATION_ID = "APPLICATION_ID";
	public static final String APPLICATION_PUBLIC_KEY = "APPLICATION_PUBLIC_KEY";
	public static final String APPLICATION_PRIVATE_KEY = "APPLICATION_PRIVATE_KEY";
	public static final String APPLICATION_PRIVATE_KEY_PASSWORD = "APPLICATION_PRIVATE_KEY_PASSWORD";

	public static final String CLIENT_EMAIL = "CLIENT_EMAIL";
	public static final String MAILINATOR_API_TOKEN = "MAILINATOR_API_TOKEN";

	private String mailinatorApiToken;

	/**
	 * Create a new instance of {@code GenericFunctionalTest}
	 *
	 */
	public GenericFunctionalTest() {
		mailinatorApiToken = getPropertyByName(MAILINATOR_API_TOKEN);
	}

	public ClientFactory createClientFactory(String accessToken) {
		ClientFactory factory;

		if (accessToken == null) {
			factory = new ClientFactory();
		} else {
			factory = new ClientFactory(accessToken);
		}
		factory.setIdentityBaseUrl(getPropertyByName(IDENTITY_HOST));
		factory.setPublicKeyBaseUrl(getPropertyByName(PUBLIC_KEYS_HOST));
		factory.setPrivateKeyBaseUrl(getPropertyByName(PRIVATE_KEYS_HOST));

		return factory;
	}

	public String getPropertyByName(String propertyName) {
		if (StringUtils.isBlank(System.getProperty(propertyName))) {
			return null;
		}
		return System.getProperty(propertyName);
	}

	public void clearMailbox(String emailAddress) throws IOException {
		List<InboxMessage> inboxMessages = Mailinator.getInboxMessages(mailinatorApiToken, emailAddress);

		for (InboxMessage message : inboxMessages) {
			Mailinator.deleteEmail(mailinatorApiToken, message.getId());
		}
	}

	public String getConfirmationCodeFromEmail(String emailAddress) {
		String confirmationCode = null;

		// Wait for email
		try {
			Thread.sleep(10 * 1000);

			// Wait email during 2 minutes
			for (int i = 0; i < 12; i++) {

				List<InboxMessage> inboxMessages = Mailinator.getInboxMessages(mailinatorApiToken, emailAddress);

				if (inboxMessages.isEmpty()) {
					Thread.sleep(10 * 1000);

					continue;
				}

				// Sort messages, latest should be first
				Collections.sort(inboxMessages, new Comparator<InboxMessage>() {

					@Override
					public int compare(InboxMessage o1, InboxMessage o2) {
						return (int) (o2.getTime() - o1.getTime());
					}
				});

				String emailId = inboxMessages.get(0).getId();

				Email email = Mailinator.getEmail(mailinatorApiToken, emailId);

				for (EmailPart emailPart : email.getEmailParts()) {
					String body = emailPart.getBody();

					// Parse confirmation email an extract confirmation code
					Pattern pattern = Pattern.compile("(Your confirmation code is [^>]+[>])(\\w+)");
					Matcher matcher = pattern.matcher(body);
					if (matcher.find()) {
						confirmationCode = matcher.group(2);

						// Delete email
						Mailinator.deleteEmail(mailinatorApiToken, emailId);
					} else {
						fail("Can't find confirmation code in email");
					}
				}
			}
		} catch (InterruptedException | IOException e) {
			fail(e.getMessage());
		}

		return confirmationCode;
	}

}
