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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.virgilsecurity.sdk.client.RequestSigner;
import com.virgilsecurity.sdk.client.exceptions.VirgilException;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsAlreadyExistsException;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsNotFoundException;
import com.virgilsecurity.sdk.client.requests.CreateCardRequest;
import com.virgilsecurity.sdk.client.requests.SignedRequest;
import com.virgilsecurity.sdk.client.utils.StringUtils;
import com.virgilsecurity.sdk.crypto.Crypto;
import com.virgilsecurity.sdk.crypto.KeyEntry;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyStorage;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;
import com.virgilsecurity.sdk.crypto.exceptions.EmptyArgumentException;
import com.virgilsecurity.sdk.crypto.exceptions.NullArgumentException;
import com.virgilsecurity.sdk.keystorage.VirgilKeyEntry;

/**
 * This class allows to store Virgil Keys in a storage.
 *
 * @author Andrii Iakovenko
 *
 */
public class VirgilKey {

	private KeyPair keyPair;

	private String keyName;

	/**
	 * Create a new instance of {@code VirgilKey}
	 *
	 */
	private VirgilKey() {
	}

	/**
	 * Create a new instance of {@code VirgilKey}
	 *
	 * @param keyName
	 *            The name of the key.
	 * @param keyPair
	 *            The key pair.
	 */
	private VirgilKey(String keyName, KeyPair keyPair) {
		this.keyName = keyName;
		this.keyPair = keyPair;
	}

	/**
	 * Creates a new {@linkplain VirgilKey} with custom Public/Private key pair.
	 * 
	 * @param keyName
	 *            Name of the key.
	 * @param keyPair
	 *            The key pair.
	 * @return The instance of {@linkplain VirgilKey}.
	 */
	public static VirgilKey create(String keyName, KeyPair keyPair) {
		return create(keyName, keyPair, null);
	}

	/**
	 * Creates a new {@linkplain VirgilKey} with custom Public/Private key pair.
	 * 
	 * @param keyName
	 *            Name of the key.
	 * @param keyPair
	 *            The key pair.
	 * @param password
	 *            The password.
	 * @return The instance of {@linkplain VirgilKey}.
	 * 
	 * @throws EmptyArgumentException
	 *             if key name is blank.
	 * @throws NullArgumentException
	 *             if key pair is null.
	 * @throws VirgilKeyIsAlreadyExistsException
	 *             if key with the same name already exists at storage.
	 */
	public static VirgilKey create(String keyName, KeyPair keyPair, String password) {
		if (StringUtils.isBlank(keyName)) {
			throw new EmptyArgumentException("keyName");
		}

		if (keyPair == null) {
			throw new NullArgumentException("keyPair");
		}

		Crypto crypto = VirgilConfig.getService(Crypto.class);
		KeyStorage storage = VirgilConfig.getService(KeyStorage.class);

		if (storage.exists(keyName)) {
			throw new VirgilKeyIsAlreadyExistsException();
		}

		VirgilKey virgilKey = new VirgilKey(keyName, keyPair);
		byte[] exportedPrivateKey = crypto.exportPrivateKey(virgilKey.getKeyPair().getPrivateKey(), password);

		storage.store(new VirgilKeyEntry(keyName, exportedPrivateKey));

		return virgilKey;
	}

	/**
	 * Creates a {@linkplain VirgilKey} with specified key name.
	 * 
	 * @param keyName
	 *            Name of the key.
	 * @return The instance of {@linkplain VirgilKey}.
	 */
	public static VirgilKey create(String keyName) {
		return create(keyName, (String) null);
	}

	/**
	 * Creates a {@linkplain VirgilKey} with specified key name.
	 * 
	 * @param keyName
	 *            Name of the key.
	 * @param password
	 *            The password.
	 * @return The instance of {@linkplain VirgilKey}.
	 */
	public static VirgilKey create(String keyName, String password) {
		Crypto crypto = VirgilConfig.getService(Crypto.class);
		KeyPair keyPair = crypto.generateKeys();

		return create(keyName, keyPair, password);
	}

	/**
	 * Loads the {@linkplain VirgilKey} by specified key name.
	 * 
	 * @param keyName
	 *            Name of the key.
	 * @return The instance of {@linkplain VirgilKey}.
	 */
	public static VirgilKey load(String keyName) {
		return load(keyName, null);
	}

	/**
	 * Loads the {@linkplain VirgilKey} by specified key name.
	 * 
	 * @param keyName
	 *            Name of the key.
	 * @param password
	 *            The password.
	 * @return The instance of {@linkplain VirgilKey}.
	 */
	public static VirgilKey load(String keyName, String password) {
		if (StringUtils.isBlank(keyName)) {
			throw new EmptyArgumentException("keyName");
		}

		Crypto crypto = VirgilConfig.getService(Crypto.class);
		KeyStorage storage = VirgilConfig.getService(KeyStorage.class);

		if (!storage.exists(keyName)) {
			throw new VirgilKeyIsNotFoundException();
		}

		KeyEntry entry = storage.load(keyName);
		PrivateKey privateKey = crypto.importPrivateKey(entry.getValue(), password);
		PublicKey publicKey = crypto.extractPublicKey(privateKey);

		VirgilKey virgilKey = new VirgilKey(keyName, new KeyPair(publicKey, privateKey));

		return virgilKey;
	}

	/**
	 * Exports the {@linkplain VirgilKey} to default Virgil Security format.
	 * 
	 * @return The private key as byte array.
	 */
	public byte[] export() {
		return export();
	}

	/**
	 * Exports the {@linkplain VirgilKey} to default Virgil Security format.
	 * 
	 * @param password
	 *            The password.
	 * @return The private key as byte array.
	 */
	public byte[] export(String password) {
		Crypto crypto = VirgilConfig.getService(Crypto.class);
		return crypto.exportPrivateKey(this.getKeyPair().getPrivateKey(), password);
	}

	/**
	 * Generates a digital signature for specified data using current
	 * {@linkplain VirgilKey}.
	 * 
	 * @param data
	 *            The data for which the digital signature will be generated.
	 * @return A byte array containing the result from performing the operation.
	 * 
	 * @throws NullArgumentException
	 *             if data is null.
	 */
	public byte[] sign(byte[] data) {
		if (data == null) {
			throw new NullArgumentException("data");
		}

		Crypto crypto = VirgilConfig.getService(Crypto.class);
		byte[] signature = crypto.sign(data, this.getKeyPair().getPrivateKey());

		return signature;
	}

	/**
	 * Decrypts the specified cipherdata using {@linkplain VirgilKey}.
	 * 
	 * @param cipherData
	 *            The encrypted data.
	 * @return A byte array containing the result from performing the operation.
	 * 
	 * @throws NullArgumentException
	 *             if cipherData is null.
	 */
	public byte[] decrypt(byte[] cipherData) {
		if (cipherData == null) {
			throw new NullArgumentException("cipherData");
		}

		Crypto crypto = VirgilConfig.getService(Crypto.class);
		byte[] data = crypto.decrypt(cipherData, this.getKeyPair().getPrivateKey());

		return data;
	}

	/**
	 * Encrypts and signs the data.
	 * 
	 * @param data
	 *            The data to be encrypted.
	 * @param recipients
	 *            The list of {@linkplain VirgilCard} recipients.
	 * @return The encrypted data.
	 * 
	 * @throws NullArgumentException
	 *             if recipients list is null.
	 */
	public byte[] signThenEncrypt(byte[] data, List<VirgilCard> recipients) {
		if (recipients == null) {
			throw new NullArgumentException("recipients");
		}

		Crypto crypto = VirgilConfig.getService(Crypto.class);
		List<PublicKey> publicKeys = new ArrayList<>();
		for (VirgilCard recipient : recipients) {
			publicKeys.add(crypto.importPublicKey(recipient.getPublicKey()));
		}

		byte[] cipherdata = crypto.signThenEncrypt(data, this.getKeyPair().getPrivateKey(),
				publicKeys.toArray(new PublicKey[0]));

		return cipherdata;
	}

	/**
	 * Decrypts and verifies the data.
	 * 
	 * @param cipherData
	 *            The data to be decrypted.
	 * @param signer
	 *            The signer's {@linkplain VirgilCard}.
	 * @return The decrypted data, which is the original plain text before
	 *         encryption.
	 */
	public byte[] decryptThenVerify(byte[] cipherData, VirgilCard signer) {
		Crypto crypto = VirgilConfig.getService(Crypto.class);
		PublicKey publicKey = crypto.importPublicKey(signer.getPublicKey());

		byte[] cipherdata = crypto.decryptThenVerify(cipherData, this.getKeyPair().getPrivateKey(), publicKey);

		return cipherdata;
	}

	/**
	 * Build Create Card Request
	 * 
	 * @param identity
	 *            The identity.
	 * @param type
	 *            The identity type.
	 * @return The Create Card Request.
	 */
	public CreateCardRequest buildCardRequest(String identity, String type) {
		return buildCardRequest(identity, type, null);
	}

	/**
	 * Build Create Card Request
	 * 
	 * @param identity
	 *            The identity.
	 * @param type
	 *            The identity type.
	 * @param data
	 *            The metadata.
	 * @return The Create Card Request.
	 */
	public CreateCardRequest buildCardRequest(String identity, String type, Map<String, String> data) {
		Crypto crypto = VirgilConfig.getService(Crypto.class);
		RequestSigner signer = VirgilConfig.getService(RequestSigner.class);

		byte[] exportedPublicKey = crypto.exportPublicKey(this.getKeyPair().getPublicKey());
		CreateCardRequest request = new CreateCardRequest(identity, type, exportedPublicKey, data);

		signer.selfSign(request, this.getKeyPair().getPrivateKey());

		return request;
	}

	/**
	 * Signs the request as authority.
	 * 
	 * @param request
	 *            The request to sign.
	 * @param appId
	 *            The application identifier.
	 */
	public void signRequest(SignedRequest request, String appId) {
		if (StringUtils.isBlank(appId)) {
			throw new EmptyArgumentException("appId");
		}

		RequestSigner signer = VirgilConfig.getService(RequestSigner.class);
		signer.authoritySign(request, appId, this.getKeyPair().getPrivateKey());
	}

	/**
	 * Destroys the current {@linkplain VirgilKey}.
	 */
	public void destroy() {
		if (StringUtils.isBlank(this.keyName)) {
			throw new VirgilException("Operation is not supported");
		}

		KeyStorage storage = VirgilConfig.getService(KeyStorage.class);
		storage.delete(this.getKeyName());
	}

	/**
	 * @return the keyPair
	 */
	public KeyPair getKeyPair() {
		return keyPair;
	}

	/**
	 * @param keyPair
	 *            the keyPair to set
	 */
	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	/**
	 * @return the keyName
	 */
	public String getKeyName() {
		return keyName;
	}

	/**
	 * @param keyName
	 *            the keyName to set
	 */
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

}
