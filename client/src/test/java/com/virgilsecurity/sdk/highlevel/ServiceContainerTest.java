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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.virgilsecurity.sdk.client.exceptions.ServiceIsAlreadyRegisteredException;
import com.virgilsecurity.sdk.client.exceptions.ServiceNotRegisteredException;
import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;
import com.virgilsecurity.sdk.highlevel.ServiceContainer;

/**
 * Unit tests for {@code ServiceContainer}
 *
 * @author Andrii Iakovenko
 *
 */
public class ServiceContainerTest {

	private ServiceContainer serviceContainer;

	@Before
	public void setUp() {
		serviceContainer = new ServiceContainer();
	}

	@Test
	public void registerSingleton_manyConstructors() {
		serviceContainer.registerSingleton(String.class, String.class);
		String str = serviceContainer.resolve(String.class);

		assertNotNull(str);
		assertThat(str, instanceOf(String.class));
	}

	@Test
	public void registerSingleton() {
		serviceContainer.registerSingleton(Crypto.class, VirgilCrypto.class);
		Crypto crypto = serviceContainer.resolve(Crypto.class);

		assertNotNull(crypto);
		assertThat(crypto, instanceOf(VirgilCrypto.class));
	}

	@Test(expected = ServiceIsAlreadyRegisteredException.class)
	public void registerSingleton_theSame() {
		serviceContainer.registerSingleton(Crypto.class, VirgilCrypto.class);
		serviceContainer.registerSingleton(Crypto.class, VirgilCrypto.class);
	}

	@Test(expected = ServiceNotRegisteredException.class)
	public void remove() {
		serviceContainer.registerSingleton(Crypto.class, VirgilCrypto.class);
		serviceContainer.remove(Crypto.class);

		serviceContainer.resolve(Crypto.class);
	}

	@Test
	public void registerInstance() {
		serviceContainer.registerInstance(Crypto.class, new VirgilCrypto());
		Crypto crypto = serviceContainer.resolve(Crypto.class);

		assertNotNull(crypto);
		assertThat(crypto, instanceOf(VirgilCrypto.class));
	}

	@Test(expected = ServiceIsAlreadyRegisteredException.class)
	public void registerInstance_theSame() {
		serviceContainer.registerInstance(Crypto.class, new VirgilCrypto());
		serviceContainer.registerInstance(Crypto.class, new VirgilCrypto());
	}

	@Test
	public void remove_nonRegistered() {
		serviceContainer.remove(Crypto.class);
	}

	@Test
	public void resolve() {
		serviceContainer.registerSingleton(Crypto.class, VirgilCrypto.class);
		serviceContainer.registerSingleton(String.class, String.class);

		assertThat(serviceContainer.resolve(String.class), instanceOf(String.class));
		assertThat(serviceContainer.resolve(Crypto.class), instanceOf(VirgilCrypto.class));
	}

	@Test
	public void resolve_theSameType() {
		serviceContainer.registerSingleton(Crypto.class, VirgilCrypto.class);
		serviceContainer.registerSingleton(String.class, String.class);

		Crypto crypto1 = serviceContainer.resolve(Crypto.class);
		Crypto crypto2 = serviceContainer.resolve(Crypto.class);

		assertSame(crypto1, crypto2);
	}

}
