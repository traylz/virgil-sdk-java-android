/*
 * Copyright (c) 2015, Virgil Security, Inc.
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
package com.virgilsecurity.sdk.crypto;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

/**
 *
 * @author Andrii Iakovenko
 */
public class ChunkCipherTest extends GenericSamplesTest {

	private static final int MAX_RECIPIENTS = 4;

	@Test
	public void createCipher() {
		try (ChunkCipher encCipher = new ChunkCipher()) {

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void encrypt_and_decrypt_withPassword() {
		Password password = new Password(RandomStringUtils.random(8));

		try (ChunkCipher encCipher = new ChunkCipher()) {
			encCipher.addPasswordRecipient(password);

			try (ChunkCipher decCipher = new ChunkCipher()) {

				for (String sampleName : getAllSamples()) {
					byte[] sample = readResource(SAMPLES_DIR + sampleName);

					int chunkSize = 16 * (RandomUtils.nextInt(10) + 1);
					encCipher.startEncryption();

					decCipher.setContentInfo(encCipher.getContentInfo());
					decCipher.startDecryptionWithPassword(password);

					byte[] decrypted = processData(encCipher, decCipher, chunkSize, sample);
					assertNotNull(decrypted);
					assertArrayEquals("sample and decrypted data should be equals", sample, decrypted);
				}
			} catch (Exception e) {
				fail(e.getMessage());
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void encrypt_and_decrypt_withKey() {
		Map<Recipient, KeyPair> recipients = new HashMap<>();
		for (int i = 0; i < MAX_RECIPIENTS; i++) {
			Recipient recipient = new Recipient(RandomStringUtils.random(8));
			KeyPair keyPair = KeyPairGenerator.generate();
			recipients.put(recipient, keyPair);
		}

		try (ChunkCipher encCipher = new ChunkCipher(); ChunkCipher decCipher = new ChunkCipher();) {
			for (Map.Entry<Recipient, KeyPair> entry : recipients.entrySet()) {
				encCipher.addKeyRecipient(entry.getKey(), entry.getValue().getPublic());
			}

			for (String sampleName : getAllSamples()) {
				byte[] sample = readResource(SAMPLES_DIR + sampleName);

				int chunkSize = 16 * (RandomUtils.nextInt(10) + 1);

				for (Entry<Recipient, KeyPair> entry : recipients.entrySet()) {
					encCipher.startEncryption();

					decCipher.setContentInfo(encCipher.getContentInfo());
					decCipher.startDecryptionWithKey(entry.getKey(), entry.getValue().getPrivate());

					byte[] decrypted = processData(encCipher, decCipher, chunkSize, sample);
					assertNotNull(decrypted);
					assertArrayEquals("sample and decrypted data should be equals", sample, decrypted);
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void encrypt_and_decrypt_withKeyWithPassword() {
		Map<Recipient, KeyPair> keyPairs = new HashMap<>();
		Map<Recipient, Password> passwords = new HashMap<>();

		for (int i = 0; i < MAX_RECIPIENTS; i++) {
			Recipient recipient = new Recipient(UUID.randomUUID().toString());

			KeyPair keyPair = KeyPairGenerator.generate();
			keyPairs.put(recipient, keyPair);

			Password password = new Password(RandomStringUtils.random(8));
			passwords.put(recipient, password);
		}

		try (ChunkCipher encCipher = new ChunkCipher(); ChunkCipher decCipher = new ChunkCipher()) {
			for (Map.Entry<Recipient, KeyPair> entry : keyPairs.entrySet()) {
				encCipher.addKeyRecipient(entry.getKey(), entry.getValue().getPublic());
			}

			for (String sampleName : getAllSamples()) {
				byte[] sample = readResource(SAMPLES_DIR + sampleName);

				int chunkSize = 16 * (RandomUtils.nextInt(10) + 1);

				for (Entry<Recipient, KeyPair> entry : keyPairs.entrySet()) {
					Recipient recipient = entry.getKey();
					PrivateKey key = entry.getValue().getPrivate();
					Password password = passwords.get(recipient);

					encCipher.startEncryption();

					decCipher.setContentInfo(encCipher.getContentInfo());
					decCipher.startDecryptionWithKey(recipient, key, password);

					byte[] decrypted = processData(encCipher, decCipher, chunkSize, sample);
					assertNotNull(decrypted);
					assertArrayEquals("sample and decrypted data should be equals", sample, decrypted);
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private byte[] processData(ChunkCipher encCipher, ChunkCipher decCipher, int chunkSize, byte[] data) {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {

			try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {

				while (is.available() > 0) {
					byte[] buf = new byte[Math.min(chunkSize, is.available())];
					is.read(buf);

					// Encode chunk
					byte[] encoded = encCipher.process(buf);

					// Decode chunk
					os.write(decCipher.process(encoded));
				}
			} catch (IOException ex) {
				fail(ex.getMessage());
			}
			encCipher.finish();
			decCipher.finish();

			return os.toByteArray();

		} catch (IOException ex) {
			fail(ex.getMessage());
		}
		return null;
	}
}
