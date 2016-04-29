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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.crypto.CryptoHelper;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;
import com.virgilsecurity.sdk.crypto.Password;
import com.virgilsecurity.sdk.crypto.utils.ConversionUtils;

/**
 * Test case for ValidationTokenGenerator
 *
 * @author Andrii Iakovenko
 *
 */
public class ValidationTokenGeneratorTest {

	@Test
	public void generate() {
		KeyPair keyPair = KeyPairGenerator.generate();
		
		for (IdentityType type : IdentityType.values()) {
			for (String value : new String[] {null, "", "Some string value"}) {
				try {
					String token = ValidationTokenGenerator.generate(type, value, keyPair.getPrivate());
					assertNotNull(token);
					String decodedToken = ConversionUtils.fromBase64String(token);
					int pos = decodedToken.indexOf(".");
					assertTrue(pos > -1);
					String guid = decodedToken.substring(0, pos);
					String signature = decodedToken.substring(pos + 1);

					assertTrue(CryptoHelper.verify(guid + type + value, signature, keyPair.getPublic()));
				} catch (Exception e) {
					fail(e.getMessage());
				}
			}
		}
	}
	
	@Test
	public void generate_privateKey_protected_with_password() {
		String password = "SoMePassWord123";
		KeyPair keyPair = KeyPairGenerator.generate(password);
		
		for (IdentityType type : IdentityType.values()) {
			for (String value : new String[] {null, "", "Some string value"}) {
				try {
					String token = ValidationTokenGenerator.generate(type, value, keyPair.getPrivate(), new Password(password));
					assertNotNull(token);
					String decodedToken = ConversionUtils.fromBase64String(token);
					int pos = decodedToken.indexOf(".");
					assertTrue(pos > -1);
					String guid = decodedToken.substring(0, pos);
					String signature = decodedToken.substring(pos + 1);

					assertTrue(CryptoHelper.verify(guid + type + value, signature, keyPair.getPublic()));
				} catch (Exception e) {
					fail(e.getMessage());
				}
			}
		}
	}

}
