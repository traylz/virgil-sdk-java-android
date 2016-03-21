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

import java.util.Map;
import java.util.Map.Entry;

/**
 * This is utils class which covers Virgil Crypto functionality.
 * 
 * @author Andrii Iakovenko
 *
 */
public class CryptoHelper {

	/**
	 * Encrypt text with password.
	 * 
	 * @param text
	 *            the text to be encrypted.
	 * @param password
	 *            the password used for encryption.
	 * @return encrypted data as Base64 string.
	 * @throws Exception
	 */
	public static String encrypt(String text, String password) throws Exception {
		try (Cipher cipher = new Cipher()) {
			cipher.addPasswordRecipient(new Password(password));
			byte[] encrypted = cipher.encrypt(text.getBytes(), true);
			return Base64.encode(encrypted);
		}
	}

	/**
	 * Encrypt text with public key.
	 * 
	 * @param text
	 *            the text to be encrypted.
	 * @param recipientId
	 *            the recipient ID.
	 * @param publicKey
	 *            the public key of recipient.
	 * @return encrypted data as Base64 string.
	 * @throws Exception
	 */
	public static String encrypt(String text, String recipientId, PublicKey publicKey) throws Exception {
		try (Cipher cipher = new Cipher()) {
			cipher.addKeyRecipient(new Recipient(recipientId), publicKey);
			byte[] encrypted = cipher.encrypt(text.getBytes(), true);
			return Base64.encode(encrypted);
		}
	}

	/**
	 * Encrypt text with public key for multiple recipients.
	 * 
	 * @param text
	 *            the text to be encrypted.
	 * @param recipients
	 *            the map of recipients. Key is recipient identifier, value is
	 *            {@link PublicKey}.
	 * @return encrypted data as Base64 string.
	 * @throws Exception
	 * 
	 * @see PublicKey
	 */
	public static String encrypt(String text, Map<String, PublicKey> recipients) throws Exception {
		try (Cipher cipher = new Cipher()) {
			for (Entry<String, PublicKey> entry : recipients.entrySet()) {
				cipher.addKeyRecipient(new Recipient(entry.getKey()), entry.getValue());
			}
			byte[] encrypted = cipher.encrypt(text.getBytes(), true);
			return Base64.encode(encrypted);
		}
	}

	/**
	 * Decrypt text with password.
	 * 
	 * @param base64Text
	 *            encrypted data as Base64 string.
	 * @param password
	 *            the password used for decryption.
	 * @return decrypted text as String.
	 * @throws Exception
	 */
	public static String decrypt(String base64Text, String password) throws Exception {
		try (Cipher cipher = new Cipher()) {
			cipher.addPasswordRecipient(new Password(password));
			byte[] decrypted = cipher.decryptWithPassword(Base64.decode(base64Text), new Password(password));
			return new String(decrypted);
		}
	}

	/**
	 * Decrypt text with private key.
	 * 
	 * @param base64Text
	 *            encrypted data as Base64 string.
	 * @param recipientId
	 *            the recipient ID.
	 * @param privateKey
	 *            the private key of recipient.
	 * @return decrypted text as String.
	 * @throws Exception
	 */
	public static String decrypt(String base64Text, String recipientId, PrivateKey privateKey) throws Exception {
		try (Cipher cipher = new Cipher()) {
			byte[] decrypted = cipher.decryptWithKey(Base64.decode(base64Text), new Recipient(recipientId), privateKey);
			return new String(decrypted);
		}
	}

	/**
	 * Decrypt text with private key which protected with password.
	 * 
	 * @param base64Text
	 *            encrypted data as Base64 string.
	 * @param recipientId
	 *            the recipient ID.
	 * @param privateKey
	 *            the private key of recipient.
	 * @param password
	 *            the private key's password.
	 * @return decrypted text as String.
	 * @throws Exception
	 */
	public static String decrypt(String base64Text, String recipientId, PrivateKey privateKey, Password password)
			throws Exception {
		try (Cipher cipher = new Cipher()) {
			byte[] decrypted = cipher.decryptWithKey(Base64.decode(base64Text), new Recipient(recipientId), privateKey,
					password);
			return new String(decrypted);
		}
	}

	/**
	 * Sign text with private key.
	 * 
	 * @param text
	 *            the text to be signed.
	 * @param privateKey
	 *            the private key.
	 * @return sign as Base64 string.
	 * @throws Exception
	 */
	public static String sign(String text, PrivateKey privateKey) throws Exception {
		try (Signer signer = new Signer()) {
			byte[] signature = signer.sign(text.getBytes(), privateKey);
			return Base64.encode(signature);
		}
	}
	
	/**
	 * Sign Base64 encoded string with private key.
	 * 
	 * @param base64string
	 *            the Base64 encoded string to be signed.
	 * @param privateKey
	 *            the private key.
	 * @return sign as Base64 string.
	 * @throws Exception
	 */
	public static String signBase64(String base64string, PrivateKey privateKey) throws Exception {
		try (Signer signer = new Signer()) {
			byte[] signature = signer.sign(Base64.decode(base64string), privateKey);
			return Base64.encode(signature);
		}
	}

	/**
	 * Sign text with private key.
	 * 
	 * @param text
	 *            the text to be signed.
	 * @param privateKey
	 *            the private key.
	 * @param password
	 *            the private key's password.
	 * @return sign as Base64 string.
	 * @throws Exception
	 */
	public static String sign(String text, PrivateKey privateKey, Password password) throws Exception {
		try (Signer signer = new Signer()) {
			byte[] signature = signer.sign(text.getBytes(), privateKey, password);
			return Base64.encode(signature);
		}
	}
	
	/**
	 * Verify text with signature.
	 * 
	 * @param text
	 *            the text to be signed.
	 * @param signature
	 *            the sign as Base64 string.
	 * @param publicKey
	 *            the public key used for verification.
	 * @return true if verification success.
	 * @throws Exception
	 */
	public static boolean verify(String text, String signature, PublicKey publicKey) throws Exception {
		try (Signer signer = new Signer()) {
			return signer.verify(text.getBytes(), Base64.decode(signature), publicKey);
		}
	}
	
	/**
	 * Verify Base664 encoded string with signature.
	 * 
	 * @param base64string
	 *            the Base64 string to be signed.
	 * @param signature
	 *            the sign as Base64 string.
	 * @param publicKey
	 *            the public key used for verification.
	 * @return true if verification success.
	 * @throws Exception
	 */
	public static boolean verifyBase64(String base64string, String signature, PublicKey publicKey) throws Exception {
		try (Signer signer = new Signer()) {
			return signer.verify(Base64.decode(base64string), Base64.decode(signature), publicKey);
		}
	}
}
