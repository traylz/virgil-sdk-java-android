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

import com.virgilsecurity.crypto.VirgilCipher;

/**
 * This class provides the functionality of a cryptographic cipher for
 * encryption and decryption.
 * 
 * @author Andrii Iakovenko
 */
public class Cipher extends CipherBase {

	/**
	 * Create a new instance of {@code Cipher}
	 *
	 */
	public Cipher() {
		super(new VirgilCipher());
	}

	/**
	 * Encrypt data.
	 * 
	 * @param data
	 *            the data to be encrypted.
	 * @param embedContentInfo
	 *            {@code true} if content information should be embedded into
	 *            data.
	 * @return the encrypted data.
	 */
	public byte[] encrypt(byte[] data, boolean embedContentInfo) {
		return ((VirgilCipher) cipher).encrypt(data, embedContentInfo);
	}

	/**
	 * Encrypt data.
	 * 
	 * @param data
	 *            the data to be encrypted.
	 * @return the encrypted data.
	 */
	public byte[] encrypt(byte[] data) {
		return ((VirgilCipher) cipher).encrypt(data);
	}

	/**
	 * Decrypt data with private key.
	 * 
	 * @param encryptedData
	 *            the data for encryption.
	 * @param recipient
	 *            the key recipient.
	 * @param privateKey
	 *            the private key of recipient.
	 * @param privateKeyPassword
	 *            the password of private key.
	 * @return the decrypted data.
	 */
	public byte[] decryptWithKey(byte[] encryptedData, Recipient recipient, PrivateKey privateKey,
			Password privateKeyPassword) {
		return ((VirgilCipher) cipher).decryptWithKey(encryptedData, recipient.getId(), privateKey.getEncoded(),
				privateKeyPassword.getEncoded());
	}

	/**
	 * Decrypt data with private key.
	 * 
	 * @param encryptedData
	 *            the data for encryption.
	 * @param recipient
	 *            the key recipient.
	 * @param privateKey
	 *            the private key of recipient.
	 * @return the decrypted data.
	 */
	public byte[] decryptWithKey(byte[] encryptedData, Recipient recipient, PrivateKey privateKey) {
		return ((VirgilCipher) cipher).decryptWithKey(encryptedData, recipient.getId(), privateKey.getEncoded());
	}

	/**
	 * Decrypt data with password.
	 * 
	 * @param encryptedData
	 *            the data for encryption.
	 * @param password
	 *            the password.
	 * @return the decrypted data.
	 */
	public byte[] decryptWithPassword(byte[] encryptedData, Password password) {
		return ((VirgilCipher) cipher).decryptWithPassword(encryptedData, password.getEncoded());
	}

	/**
	 * Decrypt data with password.
	 * 
	 * @param encryptedData
	 *            the data for encryption.
	 * @param password
	 *            the password.
	 * @return the decrypted data.
	 */
	public byte[] decryptWithPassword(byte[] encryptedData, String password) {
		return ((VirgilCipher) cipher).decryptWithPassword(encryptedData, password.getBytes());
	}

}
