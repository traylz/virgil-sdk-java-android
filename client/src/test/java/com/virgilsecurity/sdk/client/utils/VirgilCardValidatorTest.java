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
package com.virgilsecurity.sdk.client.utils;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.virgilsecurity.sdk.client.model.Card;
import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;

/**
 * Unit tests for {@link VirgilCardValidator}
 *
 * @author Andrii Iakovenko
 *
 */
public class VirgilCardValidatorTest {

	private Crypto crypto;
	private VirgilCardValidator validator;

	@Before
	public void setUp() {
		crypto = new VirgilCrypto();
		validator = new VirgilCardValidator(crypto);
		validator.addVerifier("1ef2e45f6100792bc600828f1425b27ce7655a80543118f375bd894d7313aa00",
				ConvertionUtils.base64ToArray("MCowBQYDK2VwAyEAMUJeUOZuodMPxg3/MrMxPVw+2+WYGrHcQ5S4NISIvSA="));
	}

	@Test
	@Ignore("Production private key is not available")
	public void validate() {
		Card card = new Card();
		card.setVersion("4.0");
		card.setSnapshot(ConvertionUtils.base64ToArray(
				"eyJpZGVudGl0eSI6ImFsaWNlIiwiaWRlbnRpdHlfdHlwZSI6InVzZXJuYW1lIiwicHVibGljX2tleSI6Ik1Db3dCUVlESzJWd0F5RUFCc1h5bkFFcXpwaysrV0VTQUdEYUxRZlNLKzcxYUNKU21DUGN4UjZOekVNPSIsInNjb3BlIjoiYXBwbGljYXRpb24iLCJkYXRhIjp7fX0="));

		Map<String, byte[]> signatures = new HashMap<>();
		signatures.put("e680bef87ba75d331b0a02bfa6a20f02eb5c5ba9bc96fc61ca595404b10026f4",
				ConvertionUtils.base64ToArray(
						"MFEwDQYJYIZIAWUDBAICBQAEQBm/5X9MpSC7vbjidANxS6zwOM8SaZWx3dGiKHFciRiJNw5DiYGFGS98QVKovHv6874Ctq7mTsOJvk7MWb+tewU="));
		signatures.put("74c7b7ce5b4191e987c81f1c5659b1f433de780012360b1adb2bb0083a9d8bd9",
				ConvertionUtils.base64ToArray(
						"MFEwDQYJYIZIAWUDBAICBQAEQKvVGs5G21csy3iYPCsE/8rDM0cvYf2cWZHXrdtZOP0nKtYLGHz544gKUWQEHaLApE4SM3qaqsUlifM9Z4HzKwY="));
		signatures.put("1ef2e45f6100792bc600828f1425b27ce7655a80543118f375bd894d7313aa00",
				ConvertionUtils.base64ToArray(
						"MFEwDQYJYIZIAWUDBAICBQAEQC4fewf2T10NRF3QKxXIgmG472Fs/1ZdXjxR1aOvowfjlRdCZIhtqY86S+wlmNOPtKFrH0uIRnzj2GBXxrNcvQU="));
		card.setSignatures(signatures);

		assertTrue(validator.validate(card));
	}

	@Test
	public void validate_legacyCard() {
		VirgilCardValidator validator = new VirgilCardValidator(crypto);

		Card card = new Card();
		card.setVersion("3.0");

		assertTrue(validator.validate(card));
	}

}
