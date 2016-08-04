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
package com.virgilsecurity.sdk.crypto;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.junit.Test;

/**
 * @author Andrii Iakovenko
 *
 */
public class StreamCipherTest extends GenericSamplesTest {

	private static final int MAX_RECIPIENTS = 4;

	@Test
	public void createCipher() {
		try (StreamCipher cipher = new StreamCipher()) {

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void single_cipher_encrypt_and_decrypt_withPassword_single_recipient() {
		Password password = new Password("12345678");
		try (StreamCipher cipher = new StreamCipher()) {
			cipher.addPasswordRecipient(password);

			for (String sampleName : getAllSamples()) {
				InputStream is = getClass().getClassLoader().getResourceAsStream(SAMPLES_DIR + sampleName);

				File encodedFile = File.createTempFile(sampleName, ".encoded");
				cipher.encrypt(is, new FileOutputStream(encodedFile));

				File decodedFile = File.createTempFile(sampleName, ".decoded");

				cipher.decryptWithPassword(new FileInputStream(encodedFile), new FileOutputStream(decodedFile),
						password);
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void two_ciphers_encrypt_and_decrypt_withPassword_no_contentInfo() {
		Password password = new Password("12345678");
		try (StreamCipher encCipher = new StreamCipher()) {
			encCipher.addPasswordRecipient(password);

			for (String sampleName : getAllSamples()) {
				InputStream is = getClass().getClassLoader().getResourceAsStream(SAMPLES_DIR + sampleName);

				File encodedFile = File.createTempFile(sampleName, ".encoded");
				encCipher.encrypt(is, new FileOutputStream(encodedFile));

				try (StreamCipher decCipher = new StreamCipher()) {
					decCipher.setContentInfo(encCipher.getContentInfo());

					File decodedFile = File.createTempFile(sampleName, ".decoded");
					decCipher.decryptWithPassword(new FileInputStream(encodedFile), new FileOutputStream(decodedFile),
							password);
				} catch (Exception e) {
					fail(e.getMessage());
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void two_ciphers_encrypt_and_decrypt_withPassword_single_recipient() {
		Password password = new Password("12345678");
		try (StreamCipher encCipher = new StreamCipher(); StreamCipher decCipher = new StreamCipher()) {
			encCipher.addPasswordRecipient(password);

			for (String sampleName : getAllSamples()) {
				InputStream is = getClass().getClassLoader().getResourceAsStream(SAMPLES_DIR + sampleName);

				File encodedFile = File.createTempFile(sampleName, ".encoded");
				encCipher.encrypt(is, new FileOutputStream(encodedFile), true);

				File decodedFile = File.createTempFile(sampleName, ".decoded");
				decCipher.decryptWithPassword(new FileInputStream(encodedFile), new FileOutputStream(decodedFile),
						password);
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void two_ciphers_encrypt_and_decrypt_WithPassword_few_recipients() {
		Password[] passwords = new Password[3];
		for (int i = 0; i < passwords.length; i++) {
			passwords[i] = new Password(UUID.randomUUID().toString().substring(0, 16));
		}

		try (StreamCipher encCipher = new StreamCipher()) {
			for (Password password : passwords) {
				encCipher.addPasswordRecipient(password);
			}

			for (String sampleName : getAllSamples()) {
				InputStream is = getClass().getClassLoader().getResourceAsStream(SAMPLES_DIR + sampleName);

				File encodedFile = File.createTempFile(sampleName, ".encoded");
				encCipher.encrypt(is, new FileOutputStream(encodedFile), true);

				for (Password password : passwords) {
					try (StreamCipher decCipher = new StreamCipher()) {

						File decodedFile = File.createTempFile(sampleName, ".decoded");
						decCipher.decryptWithPassword(new FileInputStream(encodedFile),
								new FileOutputStream(decodedFile), password);
					} catch (Exception e) {
						fail(e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void encrypt_and_decrypt_withKey_single_recipient() {
		KeyPair keyPair = KeyPairGenerator.generate();
		Recipient recipient = new Recipient(UUID.randomUUID().toString());

		try (StreamCipher encCipher = new StreamCipher()) {
			encCipher.addKeyRecipient(recipient, keyPair.getPublic());

			for (String sampleName : getAllSamples()) {
				InputStream is = getClass().getClassLoader().getResourceAsStream(SAMPLES_DIR + sampleName);

				File encodedFile = File.createTempFile(sampleName, ".encoded");
				encCipher.encrypt(is, new FileOutputStream(encodedFile));

				try (StreamCipher decCipher = new StreamCipher()) {
					decCipher.setContentInfo(encCipher.getContentInfo());

					File decodedFile = File.createTempFile(sampleName, ".decoded");
					decCipher.decryptWithKey(new FileInputStream(encodedFile), new FileOutputStream(decodedFile),
							recipient, keyPair.getPrivate());
				} catch (Exception e) {
					fail(e.getMessage());
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void encrypt_and_decrypt_withKey_few_recipients() {
		Map<Recipient, KeyPair> recipients = new HashMap<>();
		for (int i = 0; i < MAX_RECIPIENTS; i++) {
			Recipient recipient = new Recipient(UUID.randomUUID().toString());
			KeyPair keyPair = KeyPairGenerator.generate();
			recipients.put(recipient, keyPair);
		}

		try (StreamCipher encCipher = new StreamCipher()) {
			for (Entry<Recipient, KeyPair> entry : recipients.entrySet()) {
				encCipher.addKeyRecipient(entry.getKey(), entry.getValue().getPublic());
			}

			for (String sampleName : getAllSamples()) {
				InputStream is = getClass().getClassLoader().getResourceAsStream(SAMPLES_DIR + sampleName);

				File encodedFile = File.createTempFile(sampleName, ".encoded");
				encCipher.encrypt(is, new FileOutputStream(encodedFile), true);

				for (Entry<Recipient, KeyPair> entry : recipients.entrySet()) {
					try (StreamCipher decCipher = new StreamCipher()) {
						File decodedFile = File.createTempFile(sampleName, ".decoded");
						decCipher.decryptWithKey(new FileInputStream(encodedFile), new FileOutputStream(decodedFile),
								entry.getKey(), entry.getValue().getPrivate());
					} catch (Exception e) {
						fail(e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void encrypt_and_decrypt_withKeyWithPassword_single_recipient() {
		Password password = new Password(UUID.randomUUID().toString().substring(0, 16));
		KeyPair keyPair = KeyPairGenerator.generate(KeyType.Default, password);
		Recipient recipient = new Recipient(UUID.randomUUID().toString());

		try (StreamCipher encCipher = new StreamCipher()) {
			encCipher.addKeyRecipient(recipient, keyPair.getPublic());

			for (String sampleName : getAllSamples()) {
				InputStream is = getClass().getClassLoader().getResourceAsStream(SAMPLES_DIR + sampleName);

				File encodedFile = File.createTempFile(sampleName, ".encoded");
				encCipher.encrypt(is, new FileOutputStream(encodedFile));

				try (StreamCipher decCipher = new StreamCipher()) {
					decCipher.setContentInfo(encCipher.getContentInfo());

					File decodedFile = File.createTempFile(sampleName, ".decoded");
					decCipher.decryptWithKey(new FileInputStream(encodedFile), new FileOutputStream(decodedFile),
							recipient, keyPair.getPrivate(), password);
				} catch (Exception e) {
					fail(e.getMessage());
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void encrypt_and_decrypt_withKeyWithPassword_few_recipients() {
		Map<Recipient, KeyPair> keyPairs = new HashMap<>();
		Map<Recipient, Password> passwords = new HashMap<>();

		for (int i = 0; i < MAX_RECIPIENTS; i++) {
			Recipient recipient = new Recipient(UUID.randomUUID().toString());

			KeyPair keyPair = KeyPairGenerator.generate();
			keyPairs.put(recipient, keyPair);

			Password password = new Password(UUID.randomUUID().toString().substring(0, 16));
			passwords.put(recipient, password);
		}

		try (StreamCipher encCipher = new StreamCipher()) {
			for (Entry<Recipient, KeyPair> entry : keyPairs.entrySet()) {
				encCipher.addKeyRecipient(entry.getKey(), entry.getValue().getPublic());
			}

			for (String sampleName : getAllSamples()) {
				InputStream is = getClass().getClassLoader().getResourceAsStream(SAMPLES_DIR + sampleName);

				File encodedFile = File.createTempFile(sampleName, ".encoded");
				encCipher.encrypt(is, new FileOutputStream(encodedFile), true);

				for (Entry<Recipient, KeyPair> entry : keyPairs.entrySet()) {
					try (StreamCipher decCipher = new StreamCipher()) {

						Recipient recipient = entry.getKey();
						PrivateKey key = entry.getValue().getPrivate();
						Password password = passwords.get(recipient);

						File decodedFile = File.createTempFile(sampleName, ".decoded");
						decCipher.decryptWithKey(new FileInputStream(encodedFile), new FileOutputStream(decodedFile),
								recipient, key, password);
					} catch (Exception e) {
						fail(e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
