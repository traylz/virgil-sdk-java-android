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
package com.virgilsecurity.sdk.client;

import java.io.IOException;

import com.virgilsecurity.sdk.client.exceptions.ServiceException;
import com.virgilsecurity.sdk.client.http.ResponseCallback;
import com.virgilsecurity.sdk.client.http.VoidResponseCallback;
import com.virgilsecurity.sdk.client.model.APIError;
import com.virgilsecurity.sdk.client.model.Identity;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.Action;
import com.virgilsecurity.sdk.client.model.identity.Confirmation;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.service.IdentityService;

import retrofit2.Response;

/**
 * Identity client.
 *
 * @author Andrii Iakovenko
 *
 */
public class IdentityClient extends AbstractClient {

	public static final String IDENTITY_BASE_URL = "https://identity.virgilsecurity.com";

	/**
	 * Create a new instance of IdentityClient
	 *
	 */
	public IdentityClient() {
		this(IDENTITY_BASE_URL);
	}

	/**
	 * Create a new instance of IdentityClient
	 *
	 * @param baseUrl
	 */
	public IdentityClient(String baseUrl) {
		super(baseUrl, null);
	}

	/**
	 * Verify identity.
	 * 
	 * @param type
	 *            The type of verified identity.
	 * @param value
	 *            The value of verified identity.
	 * @return action id.
	 */
	public String verify(IdentityType type, String value) {
		try {
			// Identity service doesn't support Custom identity type
			assert !IdentityType.CUSTOM.equals(type);

			Response<Action> response = createService(IdentityService.class).verify(new Identity(type, value))
					.execute();
			return ((Action) handleResponse(response)).getActionId();
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Verify identity.
	 * 
	 * @param type
	 *            The type of verified identity.
	 * @param value
	 *            The value of verified identity.
	 * @param callback
	 * @throws IOException
	 */
	public void verify(IdentityType type, String value, ResponseCallback<Action> callback) throws IOException {
		// Identity service doesn't support Custom identity type
		assert !IdentityType.CUSTOM.equals(type);

		createService(IdentityService.class).verify(new Identity(type, value)).enqueue(callback);
	}

	/**
	 * Confirms the identity from the {@linkplain #verify(IdentityType, String)
	 * verify} step to obtain an identity confirmation token.
	 * 
	 * @param actionId
	 *            the action identifier.
	 * @param confirmationCode
	 *            the confirmation code.
	 * @return
	 * @throws ServiceException
	 */
	public ValidatedIdentity confirm(String actionId, String confirmationCode) {
		Confirmation confirmation = new Confirmation();
		confirmation.setActionId(actionId);
		confirmation.setConfirmationCode(confirmationCode);

		try {
			Response<ValidatedIdentity> response = createService(IdentityService.class).confirm(confirmation).execute();
			return ((ValidatedIdentity) handleResponse(response));
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Confirms the identity from the {@linkplain #verify(IdentityType, String)
	 * verify} step to obtain an identity confirmation token.
	 * 
	 * @param actionId
	 *            the action identifier.
	 * @param confirmationCode
	 *            the confirmation code.
	 * @param callback
	 *            the retrofit callback.
	 * @throws IOException
	 */
	public void confirm(String actionId, String confirmationCode, ResponseCallback<ValidatedIdentity> callback)
			throws IOException {
		Confirmation confirmation = new Confirmation();
		confirmation.setActionId(actionId);
		confirmation.setConfirmationCode(confirmationCode);

		createService(IdentityService.class).confirm(confirmation).enqueue(callback);
	}

	/**
	 * Validates the passed token.
	 * 
	 * @param identity
	 *            the identity for validation. The {@code token} should be
	 *            defined.
	 * @return {@code true} if identity validated.
	 */
	public boolean validate(ValidatedIdentity identity) {
		try {
			Response<Void> response = createService(IdentityService.class).validate(identity).execute();
			if (response.code() < 400) {
				return true;
			} else if (response.code() == 400) {
				return false;
			}
			APIError error = new APIError(response.code(), response.errorBody().string());
			throw new ServiceException(error);
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Validates the passed token.
	 * 
	 * @param identity
	 *            the identity for validation. The {@code token} should be
	 *            defined.
	 * @param callback
	 *            the retrofit callback.
	 * @throws IOException
	 */
	public void validate(ValidatedIdentity identity, VoidResponseCallback callback) throws IOException {
		createService(IdentityService.class).validate(identity).enqueue(callback);
	}

}
