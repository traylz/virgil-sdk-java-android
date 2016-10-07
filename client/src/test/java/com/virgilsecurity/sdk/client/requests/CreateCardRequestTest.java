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
package com.virgilsecurity.sdk.client.requests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.virgilsecurity.sdk.client.model.GlobalIdentityType;
import com.virgilsecurity.sdk.client.model.dto.DeviceInfo;
import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;

/**
 * TODO: add type description
 *
 * @author Andrii Iakovenko
 *
 */
public class CreateCardRequestTest {

	private static final String IDENTITY = "test@mail.com";
	private static final String IDENTITY_TYPE = GlobalIdentityType.APPLICATION.getValue();

	private Crypto crypto;
	private CreateCardRequest request;
	private DeviceInfo deviceInfo;

	@Before
	public void setUp() {
		crypto = new VirgilCrypto();
		KeyPair keyPair = crypto.generateKeys();
		byte[] publicKey = crypto.exportPublicKey(keyPair.getPublicKey());

		deviceInfo = new DeviceInfo();
		deviceInfo.setDevice("Google Nexus 6");
		deviceInfo.setDeviceName("MyDevice");

		request = new CreateCardRequest(IDENTITY, IDENTITY_TYPE, publicKey, null, deviceInfo);
	}

	@Test
	public void getSnapshot() {
		String snapshot = request.getSnapshot();

		CreateCardRequest restoredRequest = new CreateCardRequest(null, null, null);
		restoredRequest.restoreRequest(snapshot, null);

		assertEquals(request.getIdentity(), restoredRequest.getIdentity());
		assertEquals(request.getIdentityType(), restoredRequest.getIdentityType());
		assertEquals(request.getData(), restoredRequest.getData());
		assertEquals(request.getInfo().getDevice(), restoredRequest.getInfo().getDevice());
		assertEquals(request.getInfo().getDeviceName(), restoredRequest.getInfo().getDeviceName());
		assertEquals(request.getSignatures(), restoredRequest.getSignatures());
		assertEquals(request.getSnapshot(), restoredRequest.getSnapshot());
		assertEquals(request.getPublicKey(), restoredRequest.getPublicKey());
	}

}
