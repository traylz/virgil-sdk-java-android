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

import com.virgilsecurity.crypto.VirgilCipherBase;

/**
 * This class provides basic functionality of a cryptographic cipher for
 * encryption and decryption.
 * 
 * @author Andrii Iakovenko
 */
public class CipherBase implements AutoCloseable {

	protected VirgilCipherBase cipher;

	/**
	 * Create a new instance of {@code CipherBase}
	 *
	 */
	public CipherBase() {
		this.cipher = new VirgilCipherBase();
	}

	/**
	 * Create a new instance of {@code CipherBase}
	 *
	 * @param cipher
	 *            the {@code VirgilCipherBase} which will be wrapped by this
	 *            istance of {@code CipherBase}.
	 */
	protected CipherBase(VirgilCipherBase cipher) {
		this.cipher = cipher;
	}

	/**
	 * Add key recipient to Cipher.
	 * 
	 * @param recipient
	 *            the recipient.
	 * @param publicKey
	 *            the public key of recipient.
	 * 
	 * @see Recipient
	 * @see PublicKey
	 */
	public void addKeyRecipient(Recipient recipient, PublicKey publicKey) {
		cipher.addKeyRecipient(recipient.getId(), publicKey.getEncoded());
	}

	/**
	 * Add key recipient to Cipher.
	 * 
	 * @param recipientId
	 *            the recipient's identifier.
	 * @param publicKey
	 *            the public key of recipient.
	 */
	public void addKeyRecipient(String recipientId, PublicKey publicKey) {
		addKeyRecipient(new Recipient(recipientId), publicKey);
	}

	/**
	 * Remove key recipient from Cipher.
	 * 
	 * @param recipient
	 *            the recipient
	 */
	public void removeKeyRecipient(Recipient recipient) {
		cipher.removeKeyRecipient(recipient.getId());
	}

	/**
	 * Add password recipient to Cipher.
	 * 
	 * @param password
	 *            the password for data encryption.
	 */
	public void addPasswordRecipient(Password password) {
		cipher.addPasswordRecipient(password.getEncoded());
	}

	/**
	 * Add password recipient to Cipher.
	 * 
	 * @param password
	 *            the password for data encryption.
	 */
	public void addPasswordRecipient(String password) {
		cipher.addPasswordRecipient(password.getBytes());
	}

	/**
	 * Remove password recipient from Cipher.
	 * 
	 * @param password
	 *            the password to be removed.
	 */
	public void removePasswordRecipient(Password password) {
		cipher.removePasswordRecipient(password.getEncoded());
	}

	/**
	 * Remove all recipients from Cipher.
	 */
	public void removeAllRecipients() {
		cipher.removeAllRecipients();
	}

	/**
	 * Returns the content information.
	 * 
	 * @return the content information.
	 */
	public byte[] getContentInfo() {
		return cipher.getContentInfo();
	}

	/**
	 * Sets the content information.
	 * 
	 * @param contentInfo
	 *            the content information.
	 */
	public void setContentInfo(byte[] contentInfo) {
		cipher.setContentInfo(contentInfo);
	}

	/**
	 * Define size of content information.
	 * @param data the content information.
	 * @return the size of content information
	 */
	public static long defineContentInfoSize(byte[] data) {
		return VirgilCipherBase.defineContentInfoSize(data);
	}

	/**
	 * @return the custom parameters.
	 * 
	 * @see CustomParams
	 */
	public CustomParams customParams() {
		return new CustomParams(cipher.customParams());
	}

	/* (non-Javadoc)
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		cipher.close();
	}

}
