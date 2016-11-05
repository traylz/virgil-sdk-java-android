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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Test;

import com.virgilsecurity.sdk.client.CardValidator;
import com.virgilsecurity.sdk.client.RequestSigner;
import com.virgilsecurity.sdk.client.VirgilClient;
import com.virgilsecurity.sdk.client.exceptions.ServiceNotRegisteredException;
import com.virgilsecurity.sdk.client.utils.VirgilCardValidator;
import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.KeyStorage;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;
import com.virgilsecurity.sdk.keystorage.VirgilKeyStorage;

/**
 * Unit tests for {@code VirgilConfig}
 *
 * @author Andrii Iakovenko
 *
 */
public class VirgilConfigTest {

	private static final String TOKEN = "just a token";

	@Test
	public void init() {
		VirgilConfig.init(TOKEN);

		assertThat(VirgilConfig.getService(Crypto.class), instanceOf(VirgilCrypto.class));
		assertThat(VirgilConfig.getService(RequestSigner.class), instanceOf(RequestSigner.class));
		assertThat(VirgilConfig.getService(KeyStorage.class), instanceOf(VirgilKeyStorage.class));
		assertThat(VirgilConfig.getService(VirgilClient.class), instanceOf(VirgilClient.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void init_nullAccessToken() {
		VirgilConfig.init(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void init_emptyAccessToken() {
		VirgilConfig.init("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void init_blankAccessToken() {
		VirgilConfig.init(" \t\n");
	}

	@Test
	public void setCardValidator() {
		VirgilConfig.init(TOKEN);
		Crypto crypto = VirgilConfig.getService(Crypto.class);
		CardValidator validator = new VirgilCardValidator(crypto);
		VirgilConfig.setCardValidator(validator);
	}

	@Test(expected = ServiceNotRegisteredException.class)
	public void setCardValidator_notInitializedConfig() {
		Crypto crypto = VirgilConfig.getService(Crypto.class);
		CardValidator validator = new VirgilCardValidator(crypto);
		VirgilConfig.setCardValidator(validator);
	}

	@Test
	public void setKeyStorage() {
		KeyStorage storage = new VirgilKeyStorage();
		VirgilConfig.setKeyStorage(storage);

		assertSame(storage, VirgilConfig.getService(KeyStorage.class));
	}

	@Test(expected = ServiceNotRegisteredException.class)
	public void reset() {
		VirgilConfig.init(TOKEN);
		VirgilConfig.reset();

		VirgilConfig.getService(VirgilClient.class);
	}

	@After
	public void tearDown() {
		VirgilConfig.reset();
	}
}
