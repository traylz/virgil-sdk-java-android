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

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SignatureException;

import com.virgilsecurity.sdk.crypto.exception.CryptoException;
import com.virgilsecurity.sdk.crypto.exception.DecryptionException;
import com.virgilsecurity.sdk.crypto.exception.EncryptionException;
import com.virgilsecurity.sdk.crypto.exception.VerificationException;

/**
 * TODO: add type description
 *
 * @author Andrii Iakovenko
 *
 */
public interface Crypto {

	/**
	 * Calculate fingerprint.
	 * 
	 * @param content
	 *            The data to calculate fingerprint for.
	 * @return the fingerprint as {@code String}.
	 */

	Fingerprint calculateFingerprint(byte[] content);

	byte[] computeHash(byte[] data, HashAlgorithm algorithm);

	/**
	 * Decrypt byte array with private key.
	 * 
	 * @param data
	 *            the data to be decrypted.
	 * @param privateKey
	 *            the recipients private key.
	 * @return the decrypted data as byte array.
	 */

	byte[] decrypt(byte[] data, PrivateKey privateKey);

	/**
	 * Decrypt stream data with private key.
	 * 
	 * @param inputStream
	 *            the input stream to be decrypted.
	 * @param outputStream
	 *            the decrypted data as stream.
	 * @param privateKey
	 *            the recipients private key.
	 * @throws DecryptionException
	 * 
	 * @see PublicKey
	 * @see PrivateKey
	 */

	void decrypt(InputStream inputStream, OutputStream outputStream, PrivateKey privateKey);

	/**
	 * Encrypt byte array with public key set.
	 * 
	 * @param data
	 *            the data to be encrypted.
	 * @param recipients
	 *            the recipients public key set.
	 * @return the encrypted data as byte array.
	 * 
	 * @see PublicKey
	 */

	byte[] encrypt(byte[] data, PublicKey[] recipients);

	/**
	 * Encrypt stream data with public key set.
	 * 
	 * @param inputStream
	 *            the input stream to be encrypted.
	 * @param outputStream
	 *            the output stream encrypted data written to.
	 * @param recipients
	 *            the recipients public key set.
	 * @throws EncryptionException
	 */

	void encrypt(InputStream inputStream, OutputStream outputStream, PublicKey[] recipients);

	/**
	 * Export private key as byte array.
	 * 
	 * @param privateKey
	 *            the private key.
	 * @return
	 * 
	 * @see PrivateKey
	 */

	byte[] exportPrivateKey(PrivateKey privateKey);

	/**
	 * Export private key as byte array.
	 * 
	 * @param privateKey
	 *            the private key.
	 * @param password
	 *            the private key password.
	 * @return
	 * 
	 * @see PrivateKey
	 */

	byte[] exportPrivateKey(PrivateKey privateKey, String password);

	/**
	 * Export public key as byte array.
	 * 
	 * @param publicKey
	 *            the public key.
	 * @return
	 * 
	 * @see PublicKey
	 */

	byte[] exportPublicKey(PublicKey publicKey);
	
	/**
	 * @param privateKey
	 * @return
	 */
	PublicKey extractPublicKey(PrivateKey privateKey);

	/**
	 * Generate key pair.
	 * 
	 * @return the generated key pair.
	 * 
	 * @see PrivateKey
	 * @see PublicKey
	 */

	KeyPair generateKeys();

	/**
	 * Import private key from byte array.
	 * 
	 * @param keyData
	 *            the Base64 encoded private key.
	 * @return the private key.
	 * @throws CryptoException 
	 * 
	 * @see PrivateKey
	 */

	PrivateKey importPrivateKey(byte[] keyData);

	/**
	 * Import private key from byte array.
	 * 
	 * @param keyData
	 *            the Base64 encoded private key.
	 * @param password
	 *            the private key password.
	 * @return the private key.
	 * @throws CryptoException 
	 * 
	 * @see PrivateKey
	 */

	PrivateKey importPrivateKey(byte[] keyData, String password);

	/**
	 * Import public key from byte array.
	 * 
	 * @param publicKey
	 * @return the public key.
	 * 
	 * @see PublicKey
	 */

	PublicKey importPublicKey(byte[] publicKey);

	/**
	 * Sign byte array data with private key.
	 * 
	 * @param data
	 *            the data to be signed.
	 * @param privateKey
	 *            the signer's private key.
	 * @return the signature.
	 * 
	 * @see PrivateKey
	 */

	byte[] sign(byte[] data, PrivateKey privateKey);

	/**
	 * Sign stream data with private key.
	 * 
	 * @param inputStream
	 *            the input stream to be signed.
	 * @param privateKey
	 *            the signer's private key.
	 * @return the signature.
	 * @throws SignatureException
	 * 
	 * @see PrivateKey
	 */

	byte[] sign(InputStream inputStream, PrivateKey privateKey);

	/**
	 * Verify byte array with signature.
	 * 
	 * @param data
	 *            the data to be verified.
	 * @param signature
	 *            the signature.
	 * @param signer
	 *            the signer's public key.
	 * @return {@code true} if signature is valid. {@code false} in other case.
	 * @throws VerificationException 
	 */

	boolean verify(byte[] data, byte[] signature, PublicKey signer);

	/**
	 * Verify stream data with signature.
	 * 
	 * @param inputStream
	 *            the input stream to be verified.
	 * @param signature
	 *            the signature.
	 * @param signer
	 *            the signer's public key.
	 * @return {@code true} if signature is valid. {@code false} in other case.
	 * @throws VerificationException
	 */

	boolean verify(InputStream inputStream, byte[] signature, PublicKey signer);

	byte[] signThenEncrypt(byte[] data, PrivateKey privateKey, PublicKey[] recipients);

	byte[] decryptThenVerify(byte[] cipherData, PrivateKey privateKey, PublicKey publicKey);
}
