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

import java.util.UUID;

import com.virgilsecurity.sdk.crypto.CryptoHelper;
import com.virgilsecurity.sdk.crypto.Password;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.utils.ConversionUtils;

/**
 * Provides a helper methods to generate validation token based on application's
 * private key.
 *
 * @author Andrii Iakovenko
 *
 */
public class ValidationTokenGenerator {

	/**
	 * Generates the validation token based on application's private key.
	 * 
	 * @param type
	 *            The type of the identity.
	 * @param value
	 *            The identity value.
	 * @param privateKey
	 *            The application's private key.
	 * @return Generated validation token.
	 * @throws Exception
	 */
	public static String generate(String type, String value, PrivateKey privateKey) throws Exception {

		String guid = UUID.randomUUID().toString();

		String signature = CryptoHelper.sign(guid + type.toString() + value, privateKey);
		return ConversionUtils.toBase64String(guid + "." + signature);
	}

	/**
	 * Generates the validation token based on application's private key.
	 * 
	 * @param type
	 *            The type of the identity.
	 * @param value
	 *            The identity value.
	 * @param privateKey
	 *            The application's private key protected with password.
	 * @param privateKeyPassword
	 *            The private key password.
	 * @return Generated validation token.
	 * @throws Exception
	 */
	public static String generate(String type, String value, PrivateKey privateKey, Password privateKeyPassword)
			throws Exception {

		String guid = UUID.randomUUID().toString();

		String signature = CryptoHelper.sign(guid + type.toString() + value, privateKey, privateKeyPassword);
		return ConversionUtils.toBase64String(guid + "." + signature);
	}

}
