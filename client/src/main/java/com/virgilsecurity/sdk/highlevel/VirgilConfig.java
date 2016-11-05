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
package com.virgilsecurity.sdk.highlevel;

import com.virgilsecurity.sdk.client.CardValidator;
import com.virgilsecurity.sdk.client.RequestSigner;
import com.virgilsecurity.sdk.client.VirgilClient;
import com.virgilsecurity.sdk.client.utils.StringUtils;
import com.virgilsecurity.sdk.client.utils.VirgilCardValidator;
import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.KeyStorage;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;
import com.virgilsecurity.sdk.keystorage.VirgilKeyStorage;

/**
 * The {@code VirgilConfig} is responsible for the initialization of the
 * high-level SDK components.
 *
 * @author Andrii Iakovenko
 *
 */
public class VirgilConfig {

	private static ServiceContainer serviceContainer;

	static {
		serviceContainer = new ServiceContainer();
		init();
	}

	/**
	 * Initialize config.
	 */
	private static void init() {
		serviceContainer.registerSingleton(Crypto.class, VirgilCrypto.class);
		serviceContainer.registerSingleton(RequestSigner.class, RequestSigner.class);
		serviceContainer.registerSingleton(KeyStorage.class, VirgilKeyStorage.class);
	}

	/**
	 * <p>
	 * Initializes a Virgil high-level API with specified access token.
	 * </p>
	 * <p>
	 * The access token provides an authenticated secure access to the Virgil
	 * Security services and is passed with each API call. The access token also
	 * allows the API to associate your app’s requests with your Virgil Security
	 * developer’s account.
	 * </p>
	 * 
	 * @param accessToken
	 *            The access token.
	 * 
	 */
	public static void init(String accessToken) {
		if (StringUtils.isBlank(accessToken)) {
			throw new IllegalArgumentException();
		}

		Crypto crypto = serviceContainer.resolve(Crypto.class);

		VirgilClient client = new VirgilClient(accessToken);
		client.setCardValidator(new VirgilCardValidator(crypto));

		serviceContainer.registerInstance(VirgilClient.class, client);
	}

	/**
	 * Sets the card validator.
	 * 
	 * @param validator
	 *            The card validator to be set.
	 */
	public static void setCardValidator(CardValidator validator) {
		VirgilClient client = serviceContainer.resolve(VirgilClient.class);
		client.setCardValidator(validator);
	}

	/**
	 * Sets the keys storage.
	 * 
	 * @param storage
	 *            The key storage to be set.
	 */
	public static void setKeyStorage(KeyStorage storage) {
		serviceContainer.remove(KeyStorage.class);
		serviceContainer.registerInstance(KeyStorage.class, storage);
	}

	/**
	 * Restores the persisted high-level SDK components values to their
	 * corresponding default properties.
	 */
	public static void reset() {
		serviceContainer.clear();
		init();
	}

	static <T> T getService(Class<T> type) {
		return serviceContainer.resolve(type);
	}
}
