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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.virgilsecurity.sdk.client.model.Card;
import com.virgilsecurity.sdk.client.utils.ConvertionUtils;
import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.KeyPair;

/**
 * Unit tests for {@linkplain VirgilCard}.
 *
 * @author Andrii Iakovenko
 *
 */
public class VirgilCardTest {

	private static final String TEXT = "Let's try to encrypt this text";

	private Crypto crypto;
	private KeyPair keyPair;
	private Card card;
	private VirgilCard virgilCard;

	@Before
	public void setUp() {
		crypto = VirgilConfig.getService(Crypto.class);
		keyPair = crypto.generateKeys();

		card = new Card();
		card.setId(UUID.randomUUID().toString());
		card.setIdentity("test@mail.com");
		card.setPublicKey(crypto.exportPublicKey(keyPair.getPublicKey()));

		virgilCard = new VirgilCard(card);
	}

	@Test
	public void constructor() {
		assertEquals(card.getId(), virgilCard.getId());
		assertEquals(card.getIdentity(), virgilCard.getIdentity());
		assertEquals(card.getIdentityType(), virgilCard.getIdentityType());
		assertArrayEquals(card.getPublicKey(), virgilCard.getPublicKey());
	}

	@Test
	public void encrypt() {
		byte[] cipherData = virgilCard.encrypt(ConvertionUtils.toBytes(TEXT));
		byte[] decryptedData = crypto.decrypt(cipherData, keyPair.getPrivateKey());

		assertEquals(TEXT, ConvertionUtils.toString(decryptedData));
	}

	@Test
	public void verify() {
		byte[] data = ConvertionUtils.toBytes(TEXT);
		byte[] signature = crypto.sign(data, keyPair.getPrivateKey());

		assertTrue(virgilCard.verify(data, signature));
	}

	@Test
	public void encryptText() {
		byte[] cipherData = virgilCard.encryptText(TEXT);
		byte[] decryptedData = crypto.decrypt(cipherData, keyPair.getPrivateKey());

		assertEquals(TEXT, ConvertionUtils.toString(decryptedData));
	}

	@Test
	public void verifyText() {
		byte[] data = ConvertionUtils.toBytes(TEXT);
		byte[] signature = crypto.sign(data, keyPair.getPrivateKey());

		assertTrue(virgilCard.verifyText(TEXT, signature));
	}

}
