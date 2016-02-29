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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.virgilsecurity.crypto.VirgilHash;

/**
 *
 * @author Andrii Iakovenko
 */
public class SignerTest extends GenericSamplesTest {

	@Test
	public void createSigner() {
		try (Signer signer = new Signer()) {

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void createSignerWithHashCode() {
		try (Signer signer = new Signer(new VirgilHash())) {

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void sign_and_verify() {
		KeyPair keyPair = KeyPairGenerator.generate();
		try (Signer signer = new Signer()) {

			for (String sampleName : getAllSamples()) {
				byte[] sample = readResource(SAMPLES_DIR + sampleName);
				byte[] sign = signer.sign(sample, keyPair.getPrivate());
				assertNotNull("Sign is null", sign);

				boolean verified = signer.verify(sample, sign, keyPair.getPublic());
				assertTrue("Sign verification failed", verified);
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void sign_and_verify_withPassword() {
		Password password = new Password("myPassword".getBytes());
		KeyPair keyPair = KeyPairGenerator.generate(KeyType.Default, password);
		try (Signer signer = new Signer()) {

			for (String sampleName : getAllSamples()) {
				byte[] sample = readResource(SAMPLES_DIR + sampleName);
				byte[] sign = signer.sign(sample, keyPair.getPrivate(), password);
				assertNotNull("Sign is null", sign);

				boolean verified = signer.verify(sample, sign, keyPair.getPublic());
				assertTrue("Sign verification failed", verified);
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void sign_and_verify_withPassword_but_privateKey_doesnt_have_password() {
		Password password = new Password("myPassword".getBytes());
		KeyPair keyPair = KeyPairGenerator.generate();
		try (Signer signer = new Signer()) {

			for (String sampleName : getAllSamples()) {
				byte[] sample = readResource(SAMPLES_DIR + sampleName);
				byte[] sign = signer.sign(sample, keyPair.getPrivate(), password);
				assertNotNull("Sign is null", sign);

				boolean verified = signer.verify(sample, sign, keyPair.getPublic());
				assertTrue("Sign verification failed", verified);
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void sign_and_verify_withoutPassword_but_privateKey_have_password_throws_exception() {
		Password password = new Password("myPassword".getBytes());
		KeyPair keyPair = KeyPairGenerator.generate(KeyType.Default, password);
		try (Signer signer = new Signer()) {

			for (String sampleName : getAllSamples()) {
				byte[] sample = readResource(SAMPLES_DIR + sampleName);
				try {
					signer.sign(sample, keyPair.getPrivate());
				} catch (Exception ex) {
					assertEquals("PK has no password", "PK - Private key password can't be empty", ex.getMessage());
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
