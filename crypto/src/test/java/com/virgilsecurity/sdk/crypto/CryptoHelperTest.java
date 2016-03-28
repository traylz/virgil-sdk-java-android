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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;

/**
 * This class contains unit tests for CryptoHelper.
 *
 * @author Andrii Iakovenko
 *
 */
public class CryptoHelperTest extends GenericSamplesTest {

	@Test
	public void testStreamSignAndVerify() throws Exception {
		KeyPair keyPair = KeyPairGenerator.generate();

		for (String sampleName : getAllSamples()) {
			String sign = CryptoHelper.sign(getResource(SAMPLES_DIR + sampleName), keyPair.getPrivate());
			assertNotNull("Sign can't be null", sign);

			boolean verified = CryptoHelper.verify(getResource(SAMPLES_DIR + sampleName), sign, keyPair.getPublic());
			assertTrue("Sign verification failed", verified);
		}
	}

	/**
	 * Get resource as InputStream.
	 * 
	 * @param name
	 *            the resource name
	 * @return an input stream for reading the resource
	 */
	private InputStream getResource(String name) {
		return getClass().getClassLoader().getResourceAsStream(name);
	}

}
