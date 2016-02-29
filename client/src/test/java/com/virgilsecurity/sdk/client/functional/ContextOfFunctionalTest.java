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

import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;

import com.virgilsecurity.sdk.client.ClientFactory;

/**
 * Functional tests context.
 *
 * @author Andrii Iakovenko
 *
 */
public class ContextOfFunctionalTest {
	static final String IDENTITY_STG_HOST = "https://identity-stg.virgilsecurity.com";
	static final String PUBLIC_KEYS_STG_HOST = "https://keys-stg.virgilsecurity.com";
	static final String PRIVATE_KEYS_STG_HOST = "https://keys-private-stg.virgilsecurity.com";

	// System properties
	public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	public static final String APPLICATION_ID = "APPLICATION_ID";
	public static final String SERVICE_ACCOUNT = "SERVICE_ACCOUNT";
	public static final String CLIENT_ACCOUNT = "CLIENT_ACCOUNT";

	public static final String USE_STAGE_ENV = "USE_STAGE_ENV";

	private static final String EMAIL_SERVICE = "@mailinator.com";

	// Data shared between tests
	public static final String SERVICE_EMAIL = "SERVICE_EMAIL";
	public static final String SERVICE_VALIDATION_TOKEN = "SERVICE_VALIDATION_TOKEN";
	public static final String CLIENT_EMAIL = "CLIENT_EMAIL";
	public static final String CLIENT_VALIDATION_TOKEN = "CLIENT_VALIDATION_TOKEN";
	public static final String CLIENT_FACTORY = "CLIENT_FACTORY";

	@BeforeSuite
	public void fetchData(ITestContext ctx) {
		ctx.setAttribute(ACCESS_TOKEN, System.getProperty(ACCESS_TOKEN));
		ctx.setAttribute(APPLICATION_ID, System.getProperty(APPLICATION_ID));
		ctx.setAttribute(SERVICE_ACCOUNT, System.getProperty(SERVICE_ACCOUNT));
		ctx.setAttribute(CLIENT_ACCOUNT, System.getProperty(CLIENT_ACCOUNT));
		ctx.setAttribute(SERVICE_EMAIL, System.getProperty(SERVICE_ACCOUNT) + EMAIL_SERVICE);
		ctx.setAttribute(CLIENT_EMAIL, System.getProperty(CLIENT_ACCOUNT) + EMAIL_SERVICE);

		// Initialize Client factory
		ClientFactory factory = new ClientFactory(System.getProperty(ACCESS_TOKEN));

		String useTestEnv = System.getProperty(USE_STAGE_ENV);
		if (useTestEnv != null && "true".equals(useTestEnv.trim())) {
			factory.setIdentityBaseUrl(IDENTITY_STG_HOST);
			factory.setPublicKeyBaseUrl(PUBLIC_KEYS_STG_HOST);
			factory.setPrivateKeyBaseUrl(PRIVATE_KEYS_STG_HOST);
		}

		ctx.setAttribute(ContextOfFunctionalTest.CLIENT_FACTORY, factory);
	}

}
